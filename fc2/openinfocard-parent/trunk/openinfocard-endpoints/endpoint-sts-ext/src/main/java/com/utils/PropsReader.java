package com.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropsReader {

	private static final Properties props = new Properties();
	static {
		try {
			props.load(PropsReader.class.getClassLoader().getResourceAsStream("sts.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String get(String name) {
		return props.getProperty(name);
	}

	public static final Set<String> names() {
		return props.stringPropertyNames();
	}
	public static void main(String arg[]){
		System.out.println(PropsReader.get("image-file"));
	}

}
