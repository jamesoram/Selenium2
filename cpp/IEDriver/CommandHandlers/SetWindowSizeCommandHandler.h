// Copyright 2011 Software Freedom Conservancy
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
#ifndef WEBDRIVER_IE_SETWINDOWSIZECOMMANDHANDLER_H_
#define WEBDRIVER_IE_SETWINDOWSIZECOMMANDHANDLER_H_

#include "../Browser.h"
#include "../IECommandHandler.h"
#include "../IECommandExecutor.h"
#include "../Generated/atoms.h"

namespace webdriver {

class SetWindowSizeCommandHandler : public IECommandHandler {
 public:
  SetWindowSizeCommandHandler(void) {
  }

  virtual ~SetWindowSizeCommandHandler(void) {
  }

 protected:
  void ExecuteInternal(const IECommandExecutor& executor,
                       const LocatorMap& locator_parameters,
                       const ParametersMap& command_parameters,
                       Response* response) {
    LocatorMap::const_iterator id_parameter_iterator = locator_parameters.find("windowHandle");
    ParametersMap::const_iterator width_parameter_iterator = command_parameters.find("width");
    ParametersMap::const_iterator height_parameter_iterator = command_parameters.find("height");
    if (id_parameter_iterator == locator_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter in URL: windowHandle");
      return;
    } else if (width_parameter_iterator == command_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter: x");
      return;
    } else if (height_parameter_iterator == command_parameters.end()) {
      response->SetErrorResponse(400, "Missing parameter: y");
      return;
    } else {
      int status_code = SUCCESS;
      int width = width_parameter_iterator->second.asInt();
      int height = height_parameter_iterator->second.asInt();
      std::string window_id = id_parameter_iterator->second;

      BrowserHandle browser_wrapper;
      if (window_id == "current") {
        status_code = executor.GetCurrentBrowser(&browser_wrapper);
      } else {
        status_code = executor.GetManagedBrowser(window_id, &browser_wrapper);
      }
      if (status_code != SUCCESS) {
        response->SetErrorResponse(status_code, "Error retrieving window with handle " + window_id);
        return;
      }

      // If the window is maximized, the window needs to be restored.
      HWND window_handle = browser_wrapper->GetTopLevelWindowHandle();
      if (::IsZoomed(window_handle)) {
        ::ShowWindow(window_handle, SW_RESTORE);
      }

      CComPtr<IHTMLDocument2> doc;
      browser_wrapper->GetDocument(&doc);
      std::wstring position_script = L"(function() { return function(){ return {'width':arguments[0], 'height':arguments[1]};};})();";
      Script position_script_wrapper(doc, position_script, 2);
      position_script_wrapper.AddArgument(width);
      position_script_wrapper.AddArgument(height);
      status_code = position_script_wrapper.Execute();

      // The atom is just the definition of an anonymous
      // function: "function() {...}"; Wrap it in another function so we can
      // invoke it with our arguments without polluting the current namespace.
      std::wstring script_source = L"(function() { return (";
      script_source += atoms::asString(atoms::SET_WINDOW_SIZE);
      script_source += L")})();";
      Script script_wrapper(doc, script_source, 1);
      script_wrapper.AddArgument(position_script_wrapper.result());
      status_code = script_wrapper.Execute();
      if (status_code != SUCCESS) {
        response->SetErrorResponse(EUNEXPECTEDJSERROR,
                                   "Unexpected JavaScript error getting window size");
        return;
      }

      response->SetSuccessResponse(Json::Value::null);
    }
  }
};

} // namespace webdriver

#endif // WEBDRIVER_IE_SETWINDOWSIZECOMMANDHANDLER_H_
