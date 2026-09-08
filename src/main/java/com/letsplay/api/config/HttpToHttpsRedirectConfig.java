
package com.letsplay.api.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * HttpToHttpsRedirectConfig
 */
@Configuration
public class HttpToHttpsRedirectConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addAdditionalConnectors(createHttpConnector());
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector();
        connector.setPort(8080);
        connector.setRedirectPort(8443);
        return connector;
    }
}