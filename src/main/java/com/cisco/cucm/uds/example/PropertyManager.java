package com.cisco.cucm.uds.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyManager {
	private static final String PROPERTIES_FILENAME = "/uds-example-app.properties";
	private static final Properties PROPERTIES = getProperties();
	
	public static String getProperty(String propertyName) {
		return (String)PROPERTIES.get(propertyName);
	}
	
	private static Properties getProperties() {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = PropertyManager.class.getResourceAsStream(PROPERTIES_FILENAME);
			prop.load(in);
		} catch (IOException e) {
			System.out.println("IOException while loading " + PROPERTIES_FILENAME + ": " + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("Unable to close property file InputStream: " + e.getMessage());
				}
			}
		}
		
		return prop;
	}
}
