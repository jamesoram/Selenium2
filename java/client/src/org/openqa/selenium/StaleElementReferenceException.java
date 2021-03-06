/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */


package org.openqa.selenium;

/**
 * Indicates that a reference to an element is now "stale" --- the element no longer appears on the
 * DOM of the page.
 */
public class StaleElementReferenceException extends WebDriverException {
  public StaleElementReferenceException(String message) {
    super(message);
  }

  public StaleElementReferenceException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getSupportUrl() {
    return "http://seleniumhq.org/exceptions/stale_element_reference.html";
  }
}
