package org.openqa.selenium.remote.server.auth;

import org.openqa.selenium.remote.server.DefaultDriverSessions;
import org.openqa.selenium.remote.server.DriverServlet;
import org.seleniumhq.jetty7.security.ConstraintMapping;
import org.seleniumhq.jetty7.security.ConstraintSecurityHandler;
import org.seleniumhq.jetty7.security.HashLoginService;
import org.seleniumhq.jetty7.security.authentication.BasicAuthenticator;
import org.seleniumhq.jetty7.server.Connector;
import org.seleniumhq.jetty7.server.Server;
import org.seleniumhq.jetty7.server.nio.SelectChannelConnector;
import org.seleniumhq.jetty7.servlet.ServletContextHandler;
import org.seleniumhq.jetty7.servlet.ServletHolder;
import org.seleniumhq.jetty7.util.security.Constraint;
import org.seleniumhq.jetty7.util.security.Password;

public class AuthenticatedWebDriverServer {

  public static void main(String[] args) throws Exception {
    Server server = new Server();

    Connector connector = new SelectChannelConnector();
    connector.setPort(4444);
    server.addConnector(connector);

    Constraint constraint = new Constraint();
    constraint.setName(Constraint.__BASIC_AUTH);
    constraint.setRoles(new String[] {"user"});
    constraint.setAuthenticate(true);

    ConstraintMapping constraintMapping = new ConstraintMapping();
    constraintMapping.setConstraint(constraint);
    constraintMapping.setPathSpec("/*");

    ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
    securityHandler.addConstraintMapping(constraintMapping);

    HashLoginService loginService = new HashLoginService();
    loginService.putUser("fluffy", new Password("bunny"), new String[] {
        "user"
    });
    securityHandler.setLoginService(loginService);
    securityHandler.setAuthenticator(new BasicAuthenticator());

    ServletContextHandler context = new ServletContextHandler(
        ServletContextHandler.SESSIONS);
    context.setContextPath("/wd/hub");
    context.setAttribute(DriverServlet.SESSIONS_KEY,
        new DefaultDriverSessions());
    context.addServlet(new ServletHolder(DriverServlet.class), "/*");
    context.setSecurityHandler(securityHandler);

    server.setHandler(context);
    server.start();
  }
}
