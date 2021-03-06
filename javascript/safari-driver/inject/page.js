// Copyright 2012 Software Freedom Conservancy. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview Defines utilities for exchanging messages between the
 * sandboxed SafariDriver injected script and its corresponding content page.
 */

goog.provide('safaridriver.inject.page');

goog.require('bot.Error');
goog.require('bot.ErrorCode');
goog.require('bot.dom');
goog.require('goog.array');
goog.require('goog.debug.Logger');
goog.require('goog.dom');
goog.require('goog.object');
goog.require('goog.string');
goog.require('goog.dom.classes');
goog.require('safaridriver.console');
goog.require('safaridriver.message');
goog.require('webdriver.CommandName');
goog.require('webdriver.error');
goog.require('webdriver.promise');


/**
 * @define {boolean} Whether this script is being used by the extension or has
 *     been injected into the web page.
 */
safaridriver.inject.page.EXTENSION = true;


/**
 * Class name assigned to the SCRIPT element used to insert the compiled form of
 * this script into the web page.
 * @type {string}
 * @const
 * @private
 */
safaridriver.inject.page.SCRIPT_CLASS_NAME_ = 'safari-driver';


/**
 * @type {!goog.debug.Logger}
 * @const
 * @private
 */
safaridriver.inject.page.LOG_ = goog.debug.Logger.getLogger(
    'safaridriver.inject.page.' +
        (safaridriver.inject.page.EXTENSION ? 'extension' : 'webpage'));


/**
 * Initializes this script. When included in the SafariDriver extension, this
 * appends a SCRIPT element to the DOM that loads the page script. When
 * included as the page script, this sends a LOADED message to the injected
 * script, informing it that the page script has been successfully loaded.
 */
safaridriver.inject.page.init = function() {
  var script;
  if (safaridriver.inject.page.EXTENSION) {
    safaridriver.inject.page.LOG_.info('Initializing for extension');

    script = document.createElement('script');
    script.className = safaridriver.inject.page.SCRIPT_CLASS_NAME_;
    script.type = 'text/javascript';
    script.src = safari.extension.baseURI + 'page.js';
    document.documentElement.appendChild(script);
  } else {
    safaridriver.console.init();
    safaridriver.inject.page.LOG_.info('Initializing for page');

    window.addEventListener('message', safaridriver.inject.page.onMessage_,
        true);

    var message = new safaridriver.message.Message(
        safaridriver.message.Type.LOADED);
    safaridriver.inject.page.LOG_.info('Sending ' + message);
    message.send();

    script = document.querySelector(
        'script.' + safaridriver.inject.page.SCRIPT_CLASS_NAME_ +
            ':last-child');
    // If we find the script running this script, remove it.
    if (script) {
      goog.dom.removeNode(script);
    }
  }
};


if (!safaridriver.inject.page.EXTENSION) {
  goog.exportSymbol('init', safaridriver.inject.page.init);
}


/**
 * Responds to messages from the injected script.
 * @param {Event} e The message event.
 * @private
 */
safaridriver.inject.page.onMessage_ = function(e) {
  try {
    var message = safaridriver.message.Message.fromEvent(
        (/** @type {!MessageEvent} */e));
  } catch (ex) {
    // Silently ignore parse failure; message may not have origininated from the
    // injected script.
    return;
  }

  safaridriver.inject.page.LOG_.info('onMessage: ' + JSON.stringify(message));
  switch (message.getType()) {
    case safaridriver.message.Type.COMMAND:
      safaridriver.inject.page.onCommand_(
          (/** @type {!safaridriver.message.CommandMessage} */message));
      return;

    default:
      return;  // Ignore unknown message type.
  }
};


/**
 * Handles command messages from the injected script.
 * @param {!safaridriver.message.CommandMessage} message The command message.
 * @throws {Error} If the command is not supported by this script.
 * @private
 */
safaridriver.inject.page.onCommand_ = function(message) {
  safaridriver.inject.page.LOG_.info('Handling extension command: ' + message);
  var command = message.getCommand();
  switch (command.getName()) {
    case webdriver.CommandName.EXECUTE_SCRIPT:
      safaridriver.inject.page.executeScript_(command);
      return;

    default:
      throw Error('Unknown command: ' + command.getName());
  }
};


/**
 * Generates a CSS selector for the given element.
 * @param {!Element} element The element to build a selector for.
 * @param {boolean=} opt_absolute Whether to compute an absolute selector from
 *     the document root.
 * @return {string} The CSS selector for the given element.
 * @private
 */
safaridriver.inject.page.getElementCssSelector_ = function(element,
                                                           opt_absolute) {
  var selector = element.tagName;

  if (element.id) {
    selector += '#' + safaridriver.inject.page.cssEscape_(element.id);
  }

  var classes = goog.dom.classes.get(element);
  if (classes.length) {
    selector += '.' + goog.array.map(classes,
        safaridriver.inject.page.cssEscape_).join('.');
  }

  if (opt_absolute) {
    var parent = bot.dom.getParentElement(element);
    if (parent) {
      var parentSelector = safaridriver.inject.page.getElementCssSelector_(
          parent, opt_absolute);
      selector = parentSelector + ' > ' + selector;
    }
  }

  return selector;
};


/**
 * @param {string} str The string to escape.
 * @return {string} The escaped string.
 * @private
 */
safaridriver.inject.page.cssEscape_ = function(str) {
  return str.replace(/\./g, '\\.').replace(/#/g, '\\#');
};


/**
 * Key used in an object literal to indicate it is the encoded representation of
 * a DOM element. The corresponding property's value will be a CSS selector for
 * the encoded elmeent.
 * @type {string}
 * @const
 * @private
 */
safaridriver.inject.page.ENCODED_ELEMENT_KEY_ = 'WebElement';


/**
 * Encodes a value so it may be included in a message exchanged between the
 * document and sandboxed injected script. Any DOM element references will
 * be replaced with an object literal whose sole key is t
 * @param {*} value The value to encode.
 * @return {*} The encoded value.
 * @throws {Error} If the value is cannot be encoded (e.g. it is a function, or
 *     an array or object with a cyclical reference).
 */
safaridriver.inject.page.encodeValue = function(value) {
  var type = goog.typeOf(value);
  switch (type) {
    case 'boolean':
    case 'number':
    case 'string':
      return value;

    case 'null':
    case 'undefined':
      return null;

    case 'array':
      return goog.array.map((/** @type {!Array} */value),
          safaridriver.inject.page.encodeValue);

    case 'object':
      if (value instanceof Element) {
        // We don't permit elements belong to another document because we
        // wouldn't be able to find them again on the other side when the
        // element was decoded.
        if (value.ownerDocument !== document) {
          throw Error('The element does not belong to this document: ' +
              safaridriver.inject.page.getElementCssSelector_(value));
        }

        var encoded = {};
        encoded[safaridriver.inject.page.ENCODED_ELEMENT_KEY_] =
            safaridriver.inject.page.getElementCssSelector_(value, true);
        return encoded;
      }
      return goog.object.map((/** @type {!Object} */value),
          safaridriver.inject.page.encodeValue);

    case 'function':
    default:
      throw Error('Invalid value type: ' + type + ' => ' + value);
  }
};


/**
 * Decodes a value. Any object literals whose sole key is
 * {@link safaridriver.inject.page.ENCODED_ELEMENT_KEY_} will be considered an
 * encoded reference to a DOM element. The corresponding value for this key will
 * be used as a CSS selector to locate the element.
 * @param {*} value The value to decode.
 * @return {*} The decoded value.
 * @throws {bot.Error} If an encoded DOM element cannot be located on the page.
 * @throws {Error} If the value is an invalid type, or an array or object with
 *     cyclical references.
 */
safaridriver.inject.page.decodeValue = function(value) {
  var type = goog.typeOf(value);
  switch (type) {
    case 'boolean':
    case 'number':
    case 'string':
      return value;

    case 'null':
    case 'undefined':
      return null;

    case 'array':
      return goog.array.map((/** @type {!Array} */value),
          safaridriver.inject.page.decodeValue);

    case 'object':
      var obj = (/** @type {!Object} */value);
      var keys = Object.keys(obj);
      if (keys.length == 1 &&
          keys[0] === safaridriver.inject.page.ENCODED_ELEMENT_KEY_) {
        var selector = value[safaridriver.inject.page.ENCODED_ELEMENT_KEY_];
        var element = document.querySelector(selector);
        if (!element) {
          throw new bot.Error(bot.ErrorCode.STALE_ELEMENT_REFERENCE,
              'Unable to locate encoded element: ' + selector);
        }
        return element;
      }
      return goog.object.map(obj, safaridriver.inject.page.decodeValue);

    case 'function':
    default:
      throw Error('Invalid value type: ' + type + ' => ' + value);
  }
};


/**
 * Handles an executeScript command.
 * @param {!safaridriver.Command} command The command to execute.
 * @private
 */
safaridriver.inject.page.executeScript_ = function(command) {
  var response;
  try {
    // TODO: clean-up bot.inject.executeScript so it doesn't pull in so many
    // extra dependencies.
    var fn = new Function(command.getParameter('script'));

    var args = command.getParameter('args');
    args = (/** @type {!Array} */safaridriver.inject.page.decodeValue(args));

    response = fn.apply(window, args);
    response = safaridriver.inject.page.encodeValue(response);
    response = {
      'status': bot.ErrorCode.SUCCESS,
      'value': response
    };
  } catch (ex) {
    response = webdriver.error.createResponse(ex);
  }
  var responseMessage = new safaridriver.message.ResponseMessage(
      command.getId(), response);
  safaridriver.inject.page.LOG_.info(
      'Sending executeScript response: ' + responseMessage);
  responseMessage.send();
};
