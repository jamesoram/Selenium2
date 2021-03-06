/*
Copyright 2011 WebDriver committers
Copyright 2011 Google Inc.

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

import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class ResourceCopyResult implements Renderer {

  private final String propertyName;

  public ResourceCopyResult(String propertyName) {
    if (propertyName.startsWith(":")) {
      this.propertyName = propertyName.substring(1);
    } else {
      this.propertyName = propertyName;
    }
  }

  public void render(HttpServletRequest request, HttpServletResponse response, RestishHandler handler) throws Exception {
    URL resource = (URL) request.getAttribute(propertyName);
    if (resource == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    response.setStatus(HttpServletResponse.SC_OK);
    ByteStreams.copy(resource.openStream(), response.getOutputStream());
  }
}
