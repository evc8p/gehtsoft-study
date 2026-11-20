package com.evch.rrm.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CustomWebServerProperties {
    private static volatile Properties properties = null;
    private static final Object LOCK =  new Object();

    public static Properties get() {
        if (properties == null) {
            synchronized (LOCK) {
                if (properties == null) {
                    try (InputStream in = CustomWebServer.class.getClassLoader().getResourceAsStream("webserver.properties")) {
                        if (in == null) {
                            throw new IllegalStateException("CustomWebServerProperties: a 'webserver.properties' file was not found.");
                        }
                        properties =  new Properties();
                        properties.load(in);
                    } catch (NullPointerException | IOException | NumberFormatException e) {
                        throw new IllegalStateException("Failed to initialize the CustomWebServer properties.", e);
                    }
                }
            }
        }
        return properties;
    }
}
