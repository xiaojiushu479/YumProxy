package yumProxy.net.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ServiceConfig {
    private static final String CONFIG_FILE = "service_config.properties";
    private static Properties config = new Properties();
    

    private static final boolean DEFAULT_WEBSOCKET_ENABLED = true;
    private static final boolean DEFAULT_HTTP_ENABLED = true;
    private static final boolean DEFAULT_WEBSOCKET_MESSAGE_ENABLED = true;
    private static final boolean DEFAULT_HTTP_MESSAGE_ENABLED = true;
    private static final int DEFAULT_WEBSOCKET_PORT = 5001;
    private static final int DEFAULT_HTTP_PORT = 5000;
    private static final int DEFAULT_CONFIG_PORT = 16482;
    

    private static final String DEFAULT_MAIL_API_URL = "http://localhost:5000/send-email";
    private static final String DEFAULT_SMTP_USER = "your_email@example.com";
    private static final String DEFAULT_SMTP_PASS = "example_password";
    private static final String DEFAULT_SMTP_FROM_NAME = "YourTeam";
    
    static {
        loadConfig();
    }
    
    
    public static void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    config.load(fis);

                }
            } else {

                createDefaultConfig();

            }
        } catch (Exception e) {

            createDefaultConfig();
        }
    }
    
    
    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            config.store(fos, "YumProxy Service Configuration");

        } catch (Exception e) {

        }
    }
    
    
    private static void createDefaultConfig() {
        config.setProperty("websocket.enabled", String.valueOf(DEFAULT_WEBSOCKET_ENABLED));
        config.setProperty("http.enabled", String.valueOf(DEFAULT_HTTP_ENABLED));
        config.setProperty("websocket.message.enabled", String.valueOf(DEFAULT_WEBSOCKET_MESSAGE_ENABLED));
        config.setProperty("http.message.enabled", String.valueOf(DEFAULT_HTTP_MESSAGE_ENABLED));
        config.setProperty("websocket.port", String.valueOf(DEFAULT_WEBSOCKET_PORT));
        config.setProperty("http.port", String.valueOf(DEFAULT_HTTP_PORT));
        config.setProperty("config.port", String.valueOf(DEFAULT_CONFIG_PORT));
        

        config.setProperty("mail.api.url", DEFAULT_MAIL_API_URL);
        config.setProperty("smtp.user", DEFAULT_SMTP_USER);
        config.setProperty("smtp.pass", DEFAULT_SMTP_PASS);
        config.setProperty("smtp.from.name", DEFAULT_SMTP_FROM_NAME);
        saveConfig();
    }
    
    
    public static boolean isWebSocketEnabled() {
        return Boolean.parseBoolean(config.getProperty("websocket.enabled", String.valueOf(DEFAULT_WEBSOCKET_ENABLED)));
    }
    
    
    public static void setWebSocketEnabled(boolean enabled) {
        config.setProperty("websocket.enabled", String.valueOf(enabled));
        saveConfig();
    }
    
    
    public static boolean isHttpEnabled() {
        return Boolean.parseBoolean(config.getProperty("http.enabled", String.valueOf(DEFAULT_HTTP_ENABLED)));
    }
    
    
    public static void setHttpEnabled(boolean enabled) {
        config.setProperty("http.enabled", String.valueOf(enabled));
        saveConfig();
    }
    
    
    public static boolean isWebSocketMessageEnabled() {
        return Boolean.parseBoolean(config.getProperty("websocket.message.enabled", String.valueOf(DEFAULT_WEBSOCKET_MESSAGE_ENABLED)));
    }
    
    
    public static void setWebSocketMessageEnabled(boolean enabled) {
        config.setProperty("websocket.message.enabled", String.valueOf(enabled));
        saveConfig();
    }
    
    
    public static boolean isHttpMessageEnabled() {
        return Boolean.parseBoolean(config.getProperty("http.message.enabled", String.valueOf(DEFAULT_HTTP_MESSAGE_ENABLED)));
    }
    
    
    public static void setHttpMessageEnabled(boolean enabled) {
        config.setProperty("http.message.enabled", String.valueOf(enabled));
        saveConfig();
    }
    
    
    public static int getWebSocketPort() {
        return Integer.parseInt(config.getProperty("websocket.port", String.valueOf(DEFAULT_WEBSOCKET_PORT)));
    }
    
    
    public static void setWebSocketPort(int port) {
        config.setProperty("websocket.port", String.valueOf(port));
        saveConfig();
    }
    
    
    public static int getHttpPort() {
        return Integer.parseInt(config.getProperty("http.port", String.valueOf(DEFAULT_HTTP_PORT)));
    }
    
    
    public static void setHttpPort(int port) {
        config.setProperty("http.port", String.valueOf(port));
        saveConfig();
    }
    
    
    public static int getConfigPort() {
        return Integer.parseInt(config.getProperty("config.port", String.valueOf(DEFAULT_CONFIG_PORT)));
    }
    
    
    public static void setConfigPort(int port) {
        config.setProperty("config.port", String.valueOf(port));
        saveConfig();
    }
    
    
    public static Properties getAllConfig() {
        return new Properties(config);
    }
    
    
    public static void resetToDefault() {
        createDefaultConfig();

    }
    
    
    public static void printConfig() {









    }
    

    
    
    public static String getMailApiUrl() {
        return config.getProperty("mail.api.url", DEFAULT_MAIL_API_URL);
    }
    
    
    public static void setMailApiUrl(String url) {
        config.setProperty("mail.api.url", url);
        saveConfig();
    }
    
    
    public static String getSmtpUser() {
        return config.getProperty("smtp.user", DEFAULT_SMTP_USER);
    }
    
    
    public static void setSmtpUser(String user) {
        config.setProperty("smtp.user", user);
        saveConfig();
    }
    
    
    public static String getSmtpPass() {
        return config.getProperty("smtp.pass", DEFAULT_SMTP_PASS);
    }
    
    
    public static void setSmtpPass(String pass) {
        config.setProperty("smtp.pass", pass);
        saveConfig();
    }
    
    
    public static String getSmtpFromName() {
        return config.getProperty("smtp.from.name", DEFAULT_SMTP_FROM_NAME);
    }
    
    
    public static void setSmtpFromName(String fromName) {
        config.setProperty("smtp.from.name", fromName);
        saveConfig();
    }
} 
