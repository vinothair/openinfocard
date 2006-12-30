package org.xmldap.util;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;


public class PropertiesManager {

    public static final String RELYING_PARTY = "/WEB-INF/rp.properties";
    public static final String SECURITY_TOKEN_SERVICE = "/WEB-INF/sts.properties";
    public static final String IDENTITY_SELECTOR = "";

    private Properties properties = new Properties();

    public PropertiesManager(String type, ServletContext servletContext) throws IOException {

        InputStream is = servletContext.getResourceAsStream(type);
        properties.load(is);

    }

    public String getProperty(String propertyName) {

        return properties.getProperty(propertyName);

    }


}
