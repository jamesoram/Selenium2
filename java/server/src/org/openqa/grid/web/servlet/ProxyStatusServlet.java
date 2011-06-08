package org.openqa.grid.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.internal.GridException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;

public class ProxyStatusServlet extends RegistryBasedServlet {

	private static final long serialVersionUID = 7653463271803124556L;

	public ProxyStatusServlet() {
		this(null);
	}

	public ProxyStatusServlet(Registry registry) {
		super(registry);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("TEST");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(200);
		JSONObject res;
		try {
			res = getResponse(request);
			response.getWriter().print(res);
			response.getWriter().close();
		} catch (JSONException e) {
			throw new GridException(e.getMessage());
		}

	}

	private JSONObject getResponse(HttpServletRequest request) throws IOException, JSONException {
		JSONObject requestJSON = null;
		if (request.getInputStream() != null) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer s = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				s.append(line);
			}
			rd.close();
			String json = s.toString();
			if (json != null && !"".equals(json)){
				requestJSON = new JSONObject(json);	
			}
			
		}

		JSONObject res = new JSONObject();
		res.put("success", false);

		// the id can be specied via a param, or in the json request.
		String id = null;
		if (requestJSON == null) {
			id = request.getParameter("id");
		} else {
			if ( !requestJSON.has("id") ) {
				res.put("msg", "you need to specify at least an id when call the node  status service.");
				return res;
			} else {
				id = requestJSON.getString("id");
			}
		}

		// id is defined from here.
		RemoteProxy proxy = getRegistry().getProxyById(id);
		if (proxy == null) {
			res.put("msg", "Cannot find proxy with ID =" + id + " in the registry.");
			return res;
		} else {
			res.put("msg", "proxy found !");
			res.put("success", true);
			res.put("id", proxy.getId());
			res.put("request", proxy.getOriginalRegistrationRequest().getAssociatedJSON());

			// maybe the request was for more info
			if (requestJSON != null) {
				// use basic (= no objects ) reflexion to get the extra stuff
				// requested.
				List<String> methods = getExtraMethodsRequested(requestJSON);

				List<String> errors = new ArrayList<String>();
				for (String method : methods) {
					try {
						Object o = getValueByReflection(proxy, method);
						res.put(method, o);
					} catch (Throwable t) {
						errors.add(t.getMessage());
					}
				}
				if (!errors.isEmpty()) {
					res.put("success", false);
					res.put("errors", errors.toString());
				}
			}
			return res;
		}

	}

	private Object getValueByReflection(RemoteProxy proxy, String method) {
		Class<?>[] argsClass = new Class[] {};
		try {
			Method m = proxy.getClass().getDeclaredMethod(method, argsClass);
			Object value = m.invoke(proxy, new Object[0]);
			return value;
		} catch (Throwable e) {
			throw new RuntimeException(e.getClass() + " - " + e.getMessage());
		}
	}

	private List<String> getExtraMethodsRequested(JSONObject request) {
		List<String> res = new ArrayList<String>();
		
		for (Iterator iterator = request.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			res.add(key);
			
		}
		return res;
	}

}