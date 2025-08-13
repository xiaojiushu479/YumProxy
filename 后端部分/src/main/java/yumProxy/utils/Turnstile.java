package yumProxy.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.io.InputStream;

public class Turnstile {
    private static final String VERIFY = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
    private static final String SECRET_KEY = loadSecretKey();

    private static String loadSecretKey() {

        String fromEnv = System.getenv("TURNSTILE_SECRET_KEY");
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv.trim();
        }

        try (InputStream in = Turnstile.class.getClassLoader().getResourceAsStream("turnstile_config.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                String key = p.getProperty("turnstile.secret_key", "").trim();
                if (!key.isEmpty()) return key;
            }
        } catch (Exception ignored) {}

        return "0xYOUR_TURNSTILE_SECRET_KEY";
    }

    public static boolean verify(String token) throws IOException {
        String json = "{\"secret\": \"" + SECRET_KEY + "\", \"response\": \"" + token + "\"}";
        URL url = new URL(VERIFY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
        }

        String responseJson = responseBuilder.toString();



        JSONObject jsonObject = JSONObject.parseObject(responseJson);
        Boolean success = jsonObject.getBoolean("success");
        return success;
    }
}
