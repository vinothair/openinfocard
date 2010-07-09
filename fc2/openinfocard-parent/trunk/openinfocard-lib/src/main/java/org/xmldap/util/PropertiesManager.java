package org.xmldap.util;

//import javax.servlet.ServletContext;
//import java.io.InputStream;
//import java.io.IOException;
//import java.util.Properties;
//
//
//public class PropertiesManager {
//
//    public static final String RELYING_PARTY = "/WEB-INF/rp.properties";
//    public static final String SECURITY_TOKEN_SERVICE = "/WEB-INF/sts.properties";
//    public static final String IDENTITY_SELECTOR = "";
//
//    private Properties properties = new Properties();
//
//    public PropertiesManager(String type, ServletContext servletContext) throws IOException {
//
//        InputStream is = servletContext.getResourceAsStream(type);
//        properties.load(is);
//
//    }
//
//    public String getProperty(String propertyName) {
//
//        return properties.getProperty(propertyName);
//
//    }
//
//
//}


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class PropertiesManager {

	
	/** AXEL Style **/
  public static final String RELYING_PARTY = "/WEB-INF/rp.properties";
  public static final String SECURITY_TOKEN_SERVICE = "/WEB-INF/sts.properties";
  public static final String IDENTITY_SELECTOR = "";
  
  
  
  /*** Stef Style **/
  
	static Logger log = Logger.getLogger(PropertiesManager.class);
	public static void trace(Object msg){
		log.info(msg);
	}
	private static PropertiesManager instance = null;
	
	public static PropertiesManager getInstance() {
		if (instance == null) instance = new PropertiesManager();
		return instance;
	}
	
	
	
	private static final Properties props = new Properties();
	
//	static {
//		try {
//			props.load(PropertiesManager.class.getClassLoader().getResourceAsStream("sts.properties"));
//		
//		} catch (Exception e) {
//			trace("Property File not found");
//			//e.printStackTrace();
//		}
//	}
	public PropertiesManager(){
		try {
			props.load(PropertiesManager.class.getClassLoader().getResourceAsStream("sts.properties"));
		
		} catch (Exception e) {
			trace("Property File not found");
			//e.printStackTrace();
		}
	}
	public PropertiesManager(String type, ServletContext servletContext) throws IOException {
	
	        InputStream is = servletContext.getResourceAsStream(type);
	        props.load(is);
	
	}
	public static final String get(String name) {
		return props.getProperty(name);
	}

	public static final Set<String> names() {
		return props.stringPropertyNames();
	}
	public static void main(String arg[]){
		System.out.println(PropertiesManager.getInstance().names());
		System.out.println(PropertiesManager.get("keystore"));
		System.out.println(PropertiesManager.get("image-file"));
	}

	public String getProperty(String name) {
		return get(name);
	}

}

//package org.xmldap.util;
//
//import javax.servlet.ServletContext;
//import java.io.InputStream;
//import java.io.IOException;
//import java.util.Properties;
//
//
//public class PropertiesManager {
//
//    public static final String RELYING_PARTY = "/WEB-INF/rp.properties";
//    public static final String SECURITY_TOKEN_SERVICE = "/WEB-INF/sts.properties";
//    public static final String IDENTITY_SELECTOR = "";
//
//    private Properties properties = new Properties();
//
//    public PropertiesManager(String type, ServletContext servletContext) throws IOException {
//
//        InputStream is = servletContext.getResourceAsStream(type);
//        properties.load(is);
//
//    }
//
//    public String getProperty(String propertyName) {
//
//        return properties.getProperty(propertyName);
//
//    }
//
//
//}
