/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.

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

import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.testing.Ignore.Driver.SELENESE;

import org.junit.Test;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;

/**
 * Tests for generic WebElement.
 */
public class WebElementTest extends JUnit4TestBase {
  @Ignore(SELENESE)
  @Test
  public void testElementImplementsWrapsDriver() {
    driver.get(pages.simpleTestPage);
    WebElement parent = driver.findElement(By.id("containsSomeDiv"));
    assertTrue(parent instanceof WrapsDriver);
  }

  @Ignore(SELENESE)
  @Test
  public void testElementReturnsOriginDriver() {
    driver.get(pages.simpleTestPage);
    WebElement parent = driver.findElement(By.id("containsSomeDiv"));
    assertTrue(((WrapsDriver) parent).getWrappedDriver() == driver);
  }

}
