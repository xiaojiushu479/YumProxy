package yumProxy.net.Config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import yumProxy.YumProxy;

public class ConfigManager {
    private static final String CONFIG_PATH = "Config/config.properties";
    private static Properties configProps = new Properties();

    static {
        loadConfig();
    }

    public static void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            configProps.load(fis);
        } catch (Exception e) {

        }
    }

    public static void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_PATH)) {
            configProps.store(fos, "Config");
        } catch (Exception e) {

        }
    }

    public static String getConfig(String key, String defaultValue) {
        return configProps.getProperty(key, defaultValue);
    }

    public static void setConfig(String key, String value) {
        configProps.setProperty(key, value);
    }

    public static void initConfig(YumProxy mainInstance) {
        loadConfig();

        String smtpHost = getConfig("SMTP_HOST", "mail.example.com");
        String smtpPort = getConfig("SMTP_PORT", "25");
        String smtpUser = getConfig("SMTP_USER", "your_email@example.com");
        String smtpPass = getConfig("SMTP_PASS", "example_password");
        String smtpFromName = getConfig("SMTP_FROM_NAME", "YourTeam");

        mainInstance.emailManager = new yumProxy.server.user.EmailManager();

    }
} 
