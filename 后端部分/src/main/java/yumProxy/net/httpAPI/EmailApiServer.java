package yumProxy.net.httpAPI;

import com.google.gson.Gson;
import yumProxy.server.user.EmailManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EmailApiServer {
    public static EmailManager emailManager;
    private static final Gson gson = new Gson();

    private static void log(String msg) {

    }

    public static void handleSend(HttpExchange exchange) throws IOException {
            try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }
            Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("Content-Type", "application/json; charset=UTF-8");
                Map<String, Object> result = new HashMap<>();
                Map<String, String> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                    reqData = gson.fromJson(reader, reqData.getClass());
                } catch (Exception e) {

                }
                String email = reqData.getOrDefault("email", "");
                if (email.isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                } else {

                    String processedEmail = processEmailInput(email);
                    
                    if (emailManager.sendCode(processedEmail)) {

                    result.put("code", 200);
                    result.put("data", "Code Sent");
                        result.put("email", processedEmail);
                        result.put("original_input", email);
                    result.put("Time", "");
                } else {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                    }
                }
            sendJson(exchange, result);
            } catch (Exception e) {

            }
    }

    public static void handleVerify(HttpExchange exchange) throws IOException {
            try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }
            Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("Content-Type", "application/json; charset=UTF-8");
                Map<String, Object> result = new HashMap<>();
                Map<String, String> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                    reqData = gson.fromJson(reader, reqData.getClass());
                } catch (Exception e) {

                }
                String email = reqData.getOrDefault("email", "");
                String code = reqData.getOrDefault("code", "");
                if (email.isEmpty() || code.isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                } else {

                    String processedEmail = processEmailInput(email);
                    
                    if (emailManager.verifyCode(processedEmail, code)) {

                    result.put("code", 200);
                    result.put("data", "Verified");
                        result.put("email", processedEmail);
                        result.put("original_input", email);
                    result.put("Time", "");
                } else {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                    }
                }
            sendJson(exchange, result);
            } catch (Exception e) {

            }
    }

    
    private static String processEmailInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String trimmed = input.trim();
        

            return trimmed;
        }
        

        if (trimmed.matches("^\\d+$")) {
            return trimmed + "@qq.com";
        }
        

    }

    private static void sendJson(HttpExchange exchange, Map<String, Object> result) throws IOException {
        String json = gson.toJson(result);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
} 
