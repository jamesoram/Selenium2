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
 * @fileoverview Script injected into each page when its DOM has fully loaded.
 */

goog.provide('safaridriver.inject');

goog.require('bot.ErrorCode');
goog.require('goog.debug.Logger');
goog.require('webdriver.Command');
goog.require('webdriver.CommandName');
goog.require('webdriver.error');
goog.require('webdriver.promise');
goog.require('safaridriver.Command');
goog.require('safaridriver.message');
goog.require('safaridriver.console');
goog.require('safaridriver.inject.PageMessenger');
goog.require('safaridriver.inject.commands');


/**
 * @type {!goog.debug.Logger}
 * @const
 */
safaridriver.inject.LOG = goog.debug.Logger.getLogger(
    'safaridriver.inject');


/**
 * Whether the window containing this script is active and should respond to
 * commands from the extension's global page.
 *
 * <p>By default, only the top most window is automatically active, as it
 * receives focus first when a new page is loaded. Each sub-frame will be
 * activated in turn as the user switches to them; when a sub-frame is
 * activated, this frame will be deactivated.
 *
 * <p>This is necessary because a window may contain frames that load fully
 * initialize before the top window does. If this happens, the frames will
 * intercept and handle commands from the extension before the appropriate
 * window does.
 *
 * @type {boolean}
 */
safaridriver.inject.isActive = window === window.top;


/** Initializes this injected script. */
safaridriver.inject.init = function() {
  safaridriver.console.init();
  safaridriver.inject.LOG.info(
      'Loaded injected script for: ' + window.location.href +
      ' (is ' + (safaridriver.inject.isActive ? '' : 'not ') + 'active)');

  safari.self.addEventListener('message',
      safaridriver.inject.onExtensionMessage_, false);

  var pageMessenger = safaridriver.inject.PageMessenger.getInstance();
  var onMessage = goog.bind(pageMessenger.onMessage, pageMessenger);

  window.addEventListener('message', onMessage, true);
};


/**
 * Handles messages received from the extension's global page.
 * @param {!SafariExtensionMessageEvent} e The message event.
 * @private
 */
safaridriver.inject.onExtensionMessage_ = function(e) {
  try {
    var message = safaridriver.message.Message.fromEvent(e);
  } catch (ex) {
    safaridriver.inject.LOG.warning(
        'Unable to parse message: ' + ex +
            '\nOriginal message: ' + JSON.stringify(e.message));
    return;
  }

  switch (message.getType()) {
    case safaridriver.message.Type.COMMAND:
      safaridriver.inject.onCommand_(
          (/** @type {!safaridriver.message.CommandMessage} */message));
      break;

    case safaridriver.message.Type.CONNECT:
    case safaridriver.message.Type.RESPONSE:
      safaridriver.inject.LOG.severe(
          'Injected scripts should never receive ' + message.getType() +
              ' messages: ' + JSON.stringify(e.message));
      break;

    default:
      safaridriver.inject.LOG.warning(
          'Ignoring unrecognized message: ' + message);
  }
};


/**
 * Command message handler.
 * @param {!safaridriver.message.CommandMessage} message The command message.
 * @private
 */
safaridriver.inject.onCommand_ = function(message) {
  if (!safaridriver.inject.isActive) {
    return;
  }

  var command = message.getCommand();
  if (!command.getId()) {
    safaridriver.inject.LOG.severe(
        'Ignoring unidentified command message: ' + message);
    return;
  }

  safaridriver.inject.LOG.info('Handling command: ' + message);

  var handler = safaridriver.inject.COMMAND_MAP_[command.getName()];
  if (handler) {
    try {
      // Don't schedule through webdriver.promise.Application; just execute the
      // command immediately. We're assuming the global page is scheduling
      // commands and only dispatching one at a time.
      webdriver.promise.when(
          handler(command, safaridriver.inject.PageMessenger.getInstance()),
          sendSuccess, sendError);
    } catch (ex) {
      sendError(ex);
    }
  } else {
    sendError(Error('Unknown command: ' + message));
  }

  function sendError(error) {
    sendResponse(webdriver.error.createResponse(error));
  }

  function sendSuccess(value) {
    var response = webdriver.error.isResponseObject(value) ? value : {
      'status': bot.ErrorCode.SUCCESS,
      'value': value
    };
    sendResponse(response);
  }

  function sendResponse(response) {
    safaridriver.inject.LOG.info('Sending response' +
        '\ncommand:  ' + message +
        '\nresponse: ' + JSON.stringify(response));

    response = new safaridriver.message.ResponseMessage(command.id, response);
    response.send(safari.self.tab);
  }
};


/**
 * Maps command names to the function that handles it.
 * @type {!Object.<(
 *     function(!safaridriver.Command, !safaridriver.inject.PageMessenger)|
 *     function(!safaridriver.Command)|function())>}
 * @private
 */
safaridriver.inject.COMMAND_MAP_ = {};
goog.scope(function() {
  var CommandName = webdriver.CommandName;
  var map = safaridriver.inject.COMMAND_MAP_;
  var commands = safaridriver.inject.commands;

  map[CommandName.GET] = commands.loadUrl;
  map[CommandName.REFRESH] = commands.reloadPage;
  map[CommandName.GO_BACK] = commands.unsupportedHistoryNavigation;
  map[CommandName.GO_FORWARD] = commands.unsupportedHistoryNavigation;

  map[CommandName.GET_PAGE_SOURCE] = commands.getPageSource;

  map[CommandName.ADD_COOKIE] = commands.addCookie;
  map[CommandName.GET_ALL_COOKIES] = commands.getCookies;
  map[CommandName.DELETE_ALL_COOKIES] = commands.deleteCookies;
  map[CommandName.DELETE_COOKIE] = commands.deleteCookie;

  map[CommandName.FIND_ELEMENT] = commands.findElement;
  map[CommandName.FIND_CHILD_ELEMENT] = commands.findElement;
  map[CommandName.FIND_ELEMENTS] = commands.findElements;
  map[CommandName.FIND_CHILD_ELEMENTS] = commands.findElements;
  map[CommandName.GET_ACTIVE_ELEMENT] = commands.getActiveElement;

  map[CommandName.CLEAR_ELEMENT] = commands.clearElement;
  map[CommandName.CLICK_ELEMENT] = commands.clickElement;
  map[CommandName.SUBMIT_ELEMENT] = commands.submitElement;
  map[CommandName.GET_ELEMENT_ATTRIBUTE] = commands.getElementAttribute;
  map[CommandName.GET_ELEMENT_LOCATION] = commands.getElementLocation;
  map[CommandName.GET_ELEMENT_SIZE] = commands.getElementSize;
  map[CommandName.GET_ELEMENT_TEXT] = commands.getElementText;
  map[CommandName.GET_ELEMENT_TAG_NAME] = commands.getElementTagName;
  map[CommandName.IS_ELEMENT_DISPLAYED] = commands.isElementDisplayed;
  map[CommandName.IS_ELEMENT_ENABLED] = commands.isElementEnabled;
  map[CommandName.IS_ELEMENT_SELECTED] = commands.isElementSelected;
  map[CommandName.ELEMENT_EQUALS] = commands.elementEquals;
  map[CommandName.GET_ELEMENT_VALUE_OF_CSS_PROPERTY] = commands.getCssValue;
  map[CommandName.SEND_KEYS_TO_ELEMENT] = commands.sendKeysToElement;

  map[CommandName.GET_WINDOW_POSITION] = commands.getWindowPosition;
  map[CommandName.GET_WINDOW_SIZE] = commands.getWindowSize;
  map[CommandName.SET_WINDOW_POSITION] = commands.setWindowPosition;
  map[CommandName.SET_WINDOW_SIZE] = commands.setWindowSize;

  map[CommandName.EXECUTE_SCRIPT] = commands.executeScript;
  map[CommandName.EXECUTE_ASYNC_SCRIPT] = commands.executeScript;
});

