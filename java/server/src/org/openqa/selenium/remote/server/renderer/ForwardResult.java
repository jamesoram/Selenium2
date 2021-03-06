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

package org.openqa.selenium.remote.server.renderer;

import org.openqa.selenium.remote.server.rest.RestishHandler;
import org.openqa.selenium.remote.server.rest.Renderer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ForwardResult implements Renderer {

  private final String to;

  public ForwardResult(String to) {
    this.to = to;
  }

  public void render(HttpServletRequest request, HttpServletResponse response, RestishHandler handler)
      throws Exception {

    RequestDispatcher dispatcher = request.getRequestDispatcher(to);
    dispatcher.forward(request, response);
  }

}
