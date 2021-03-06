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

package org.openqa.selenium.support.pagefactory;

import java.lang.reflect.Field;

/**
 * A factory for producing {@link ElementLocator}s. It is expected that a new ElementLocator will be
 * returned per call.
 */
public interface ElementLocatorFactory {
  /**
   * When a field on a class needs to be decorated with an {@link ElementLocator} this method will
   * be called.
   * 
   * @param field
   * @return
   */
  ElementLocator createLocator(Field field);
}
