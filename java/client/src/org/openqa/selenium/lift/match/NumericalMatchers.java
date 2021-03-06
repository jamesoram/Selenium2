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

package org.openqa.selenium.lift.match;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Various numerical hamcrest {@link Matcher}s.
 */
public class NumericalMatchers {

  @Factory
  public static Matcher<Integer> atLeast(int i) {
    return greaterThan(i - 1);
  }

  @Factory
  public static Matcher<Integer> exactly(int i) {
    return equalTo(i);
  }
}
