package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.server.user.EmailManager;
import yumProxy.utils.Turnstile;

import java.util.HashMap;
import java.util.Map;

public class EmailWebSocketApi {
    
    private static EmailManager emailManager;
    
    private static void log(String msg) {

    }
    
    
    public static void setEmailManager(EmailManager manager) {
        emailManager = manager;
    }
    
    
    public static void handleEmailSend(WebSocket conn, JSONObject data) {
        try {
            String email = data.getString("email");
            String token = data.getString("token");
            

            
            Map<String, Object> result = new HashMap<>();

            if (!Turnstile.verify(token)) {
                result.put("code", 408);
                result.put("data", "ERROR");
                result.put("message", "robot verify failed");
                result.put("Time", "");
                sendResponse(conn, "email_send", result);
                return;
            }


            if (email == null || email.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing email parameter");
                result.put("Time", "");
                sendResponse(conn, "email_send", result);
                return;
            }
            

            String processedEmail = processEmailInput(email);
            

            if (!isValidEmail(processedEmail)) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Invalid email format");
                result.put("Time", "");
                sendResponse(conn, "email_send", result);
                return;
            }
            
            if (emailManager != null && emailManager.sendCode(processedEmail)) {

                result.put("code", 200);
                result.put("data", "Code Sent");
                result.put("message", "Verification code sent successfully");
                result.put("email", processedEmail);
                result.put("original_input", email);
                result.put("Time", "");
                sendResponse(conn, "email_send", result);
            } else {

                result.put("code", 500);
                result.put("data", "ERROR");
                result.put("message", "Failed to send verification code");
                result.put("Time", "");
                sendResponse(conn, "email_send", result);
            }

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "email_send", errorResult);
        }
    }
    
    
    public static void handleEmailVerify(WebSocket conn, JSONObject data) {
        try {
            String email = data.getString("email");
            String code = data.getString("code");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (email == null || email.trim().isEmpty() || 
                code == null || code.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing email or code parameter");
                result.put("Time", "");
                sendResponse(conn, "email_verify", result);
                return;
            }
            

            String processedEmail = processEmailInput(email);
            

            if (!isValidEmail(processedEmail)) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Invalid email format");
                result.put("Time", "");
                sendResponse(conn, "email_verify", result);
                return;
            }
            
            if (emailManager != null && emailManager.verifyCode(processedEmail, code)) {

                result.put("code", 200);
                result.put("data", "Verified");
                result.put("message", "Verification code verified successfully");
                result.put("email", processedEmail);
                result.put("original_input", email);
                result.put("Time", "");
                sendResponse(conn, "email_verify", result);
            } else {

                result.put("code", 401);
                result.put("data", "ERROR");
                result.put("message", "Invalid verification code");
                result.put("Time", "");
                sendResponse(conn, "email_verify", result);
            }

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "email_verify", errorResult);
        }
    }
    
    
    public static void handleEmailStatus(WebSocket conn, JSONObject data) {
        try {
            String email = data.getString("email");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (email == null || email.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing email parameter");
                result.put("Time", "");
                sendResponse(conn, "email_status", result);
                return;
            }
            

            String processedEmail = processEmailInput(email);
            

            if (!isValidEmail(processedEmail)) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Invalid email format");
                result.put("Time", "");
                sendResponse(conn, "email_status", result);
                return;
            }
            



            result.put("code", 200);
            result.put("data", "Found");
            result.put("message", "Email status retrieved successfully");
            result.put("email", processedEmail);
            result.put("original_input", email);
            result.put("verified", false);
            result.put("last_sent", null);
            result.put("Time", "");
            sendResponse(conn, "email_status", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "email_status", errorResult);
        }
    }
    
    
    public static void handleEmailConfig(WebSocket conn, JSONObject data) {
        try {
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (superKey == null || superKey.trim().isEmpty()) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "email_config", result);
                return;
            }
            


            result.put("code", 200);
            result.put("data", "Found");
            result.put("message", "Email configuration retrieved successfully");
            result.put("smtp_enabled", emailManager != null);
            result.put("smtp_host", "smtp.example.com");
            result.put("smtp_port", 587);
            result.put("smtp_secure", true);
            result.put("Time", "");
            sendResponse(conn, "email_config", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "email_config", errorResult);
        }
    }
    
    
    private static String processEmailInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String trimmed = input.trim();
        

        if (trimmed.contains("@")) {
            return trimmed;
        }
        

        if (trimmed.matches("^\\d+$")) {
            return trimmed + "@qq.com";
        }
        

        return trimmed;
    }
    
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    
    private static void sendResponse(WebSocket conn, String action, Map<String, Object> data, String requestId) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "success");
        response.put("data", data);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    
    
    private static void sendResponse(WebSocket conn, String action, Map<String, Object> data) {
        sendResponse(conn, action, data, null);
    }
} 
