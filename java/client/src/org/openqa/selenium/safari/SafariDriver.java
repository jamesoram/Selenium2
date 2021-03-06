/*
Copyright 2012 WebDriver committers
Copyright 2012 Software Freedom Conservancy

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

package org.openqa.selenium.safari;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;

/**
 * A WebDriver implementation that controls Safari using a browser extension
 * (consequently, only Safari 5+ is supported).
 */
public class SafariDriver extends RemoteWebDriver {
  
  public SafariDriver() {
    super(new SafariDriverCommandExecutor(0), DesiredCapabilities.safari());
  }

  @Override
  protected void startClient() {
    SafariDriverCommandExecutor executor = (SafariDriverCommandExecutor) this.getCommandExecutor();
    try {
      executor.start();
    } catch (IOException e) {
      throw new WebDriverException(e);
    }
  }

  @Override
  protected void stopClient() {
    SafariDriverCommandExecutor executor = (SafariDriverCommandExecutor) this.getCommandExecutor();
    executor.stop();
  }
}
