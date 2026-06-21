package com.biblioteca.app;

import java.io.InputStream;
import java.util.Properties;

public class AppVersion {
    private static final String VERSION_FILE = "/version.properties";
    private static final String FALLBACK = "1.0.0";

    private static String version;
    private static String appName;
    private static String vendor;

    static {
        try (InputStream is = AppVersion.class.getResourceAsStream(VERSION_FILE)) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                version = props.getProperty("app.version", FALLBACK);
                appName = props.getProperty("app.name", "Indice-Digital-Biblioteca");
                vendor = props.getProperty("app.vendor", "Biblioteca");
            } else {
                version = FALLBACK;
                appName = "Indice-Digital-Biblioteca";
                vendor = "Biblioteca";
            }
        } catch (Exception e) {
            version = FALLBACK;
            appName = "Indice-Digital-Biblioteca";
            vendor = "Biblioteca";
        }
    }

    public static String getVersion() {
        return version;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getVendor() {
        return vendor;
    }

    public static String getFullString() {
        return appName + " v" + version;
    }
}
