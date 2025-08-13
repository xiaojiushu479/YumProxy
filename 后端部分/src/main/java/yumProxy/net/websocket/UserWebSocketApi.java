package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.server.user.UserManager;
import yumProxy.net.httpAPI.AuthValidator;
import yumProxy.utils.Turnstile;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class UserWebSocketApi {
    
    private static final UserManager userManager = new UserManager();
    
    private static void log(String msg) {

    }
    
    
    public static void handleUserRegister(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String password = data.getString("password");
            String email = data.getString("email");
            String code = data.getString("code");
            String token = data.getString("token");


            
            Map<String, Object> result = new HashMap<>();

            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty() || 
                email == null || email.trim().isEmpty() || 
                code == null || code.trim().isEmpty()) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Missing parameters");
                result.put("Time", "");
                sendResponse(conn, "user_register", result);
                return;
            }
            

            String processedEmail = processEmailInput(email);

            

            if (!isValidEmail(processedEmail)) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Invalid email format");
                result.put("Time", "");
                sendResponse(conn, "user_register", result);
                return;
            }
            
            if (userManager.exists(username)) {

                result.put("code", 409);
                result.put("data", "User Exists");
                result.put("message", "User already exists");
                result.put("Time", "");
                sendResponse(conn, "user_register", result);
                return;
            }
            

                String userKey = yumProxy.server.user.UserKeyManager.generateAndSaveUserKey(username);

                result.put("code", 200);
                result.put("data", "Register Succeed");
                result.put("user_key", userKey);
                result.put("Time", "");
                sendResponse(conn, "user_register", result);
            } else {

                result.put("code", 500);
                result.put("data", "ERROR");
                result.put("message", "Register failed");
                result.put("Time", "");
                sendResponse(conn, "user_register", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "user_register", errorResult);
        }
    }
    
    
    public static void handleUserLogin(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String password = data.getString("password");
            String token = data.getString("token");
            

            
            Map<String, Object> result = new HashMap<>();

            if (!Turnstile.verify(token)) {
                result.put("code", 408);
                result.put("data", "ERROR");
                result.put("message", "robot verify failed");
                result.put("Time", "");
                sendResponse(conn, "user_login", result, requestId);
                return;
            }

            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Missing parameters");
                result.put("Time", "");
                sendResponse(conn, "user_login", result, requestId);
                return;
            }
            

            yumProxy.server.user.UserBanManager.BanStatus banStatus = 
                yumProxy.server.user.UserBanManager.checkBanStatus(username);
            
            if (banStatus.isBanned) {

                result.put("code", 403);
                result.put("data", "BANNED");
                result.put("message", "閻犳劧闄勯崺娑橆啅閼奸娼堕悘蹇庤兌椤? " + banStatus.reason);
                Map<String, Object> banInfo = new HashMap<>();
                banInfo.put("banned_by", banStatus.bannedBy);
                banInfo.put("banned_at", banStatus.bannedAt != null ? banStatus.bannedAt.toString() : null);
                banInfo.put("banned_until", banStatus.bannedUntil != null ? banStatus.bannedUntil.toString() : null);
                banInfo.put("ban_type", banStatus.bannedUntil == null ? "婵﹢鏅茬粻娆戜焊娴ｄ警娲? : "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?);
                result.put("ban_info", banInfo);
                result.put("Time", "");
                sendResponse(conn, "user_login", result, requestId);
                return;
            }
            
            if (userManager.login(username, password)) {
                String userKey = yumProxy.server.user.UserKeyManager.getUserKey(username);

                result.put("code", 200);
                result.put("data", "Login Succeed");
                result.put("user_key", userKey);
                result.put("Time", "");
                sendResponse(conn, "user_login", result, requestId);
            } else {

                result.put("code", 401);
                result.put("data", "Login Failed");
                result.put("message", "Login failed");
                result.put("Time", "");
                sendResponse(conn, "user_login", result, requestId);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "Internal server error: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "user_login", errorResult, requestId);
        }
    }
    
    
    public static void handleUserLogin(WebSocket conn, JSONObject data) {
        handleUserLogin(conn, data, null);
    }
    
    
    public static void handleUserInfo(WebSocket conn, JSONObject data) {
        try {
            String targetUsername = data.getString("target_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (!AuthValidator.hasPermission(targetUsername, userKey, superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key or user key required");
                result.put("Time", "");
                sendResponse(conn, "user_info", result);
                return;
            }
            
            if (targetUsername == null || targetUsername.trim().isEmpty()) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Missing target_username");
                result.put("Time", "");
                sendResponse(conn, "user_info", result);
                return;
            }
            
            Map<String, Object> userInfo = userManager.getUserInfo(targetUsername);
            if (userInfo != null) {

                result.put("code", 200);
                result.put("data", "Found");
                result.put("message", "User found");
                result.put("user_info", userInfo);
                result.put("Time", "");
                sendResponse(conn, "user_info", result);
            } else {

                result.put("code", 404);
                result.put("data", "Not Found");
                result.put("message", "User not found");
                result.put("Time", "");
                sendResponse(conn, "user_info", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "user_info", errorResult);
        }
    }
    
    
    public static void handleUserDelete(WebSocket conn, JSONObject data) {
        try {
            String targetUsername = data.getString("target_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (!AuthValidator.hasPermission(targetUsername, userKey, superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key or user key required");
                result.put("Time", "");
                sendResponse(conn, "user_delete", result);
                return;
            }
            
            if (targetUsername == null || targetUsername.trim().isEmpty()) {

                result.put("code", 400);
                result.put("data", "ERROR");
                result.put("message", "Missing target_username");
                result.put("Time", "");
                sendResponse(conn, "user_delete", result);
                return;
            }
            
            if (userManager.deleteUser(targetUsername)) {
                yumProxy.server.user.UserKeyManager.deleteUserKey(targetUsername);

                result.put("code", 200);
                result.put("data", "Deleted");
                result.put("message", "User deleted successfully");
                result.put("target_username", targetUsername);
                result.put("Time", "");
                sendResponse(conn, "user_delete", result);
            } else {

                result.put("code", 500);
                result.put("data", "ERROR");
                result.put("message", "Delete failed");
                result.put("Time", "");
                sendResponse(conn, "user_delete", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "user_delete", errorResult);
        }
    }
    
    
    public static void handleUserList(WebSocket conn, JSONObject data) {
        try {
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "user_list", result);
                return;
            }
            


            try {


                result.put("code", 200);
                result.put("data", "Found");
                result.put("message", "Users found successfully");
                result.put("users", new ArrayList<>());
                sendResponse(conn, "user_list", result);
            } catch (Exception e) {

                result.put("code", 500);
                result.put("data", "ERROR");
                result.put("message", "Failed to get user list: " + e.getMessage());
                result.put("Time", "");
                sendResponse(conn, "user_list", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "user_list", errorResult);
        }
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
    
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    
    public static void handleUserValidateToken(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (username == null || username.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "闁活潿鍔嶉崺娑㈠触瀹ュ嫮鐟濋柤铏灊鐠愮喓绮?);
                result.put("error_code", "MISSING_USERNAME");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_validate_token", result);
                return;
            }
            
            if (userKey == null || userKey.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "闁活潿鍔嶉崺娉僶ken濞戞挸绉烽崗妯荤▔閾忓厜鏁?);
                result.put("error_code", "MISSING_TOKEN");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_validate_token", result);
                return;
            }
            

            boolean isValid = yumProxy.server.user.UserKeyManager.validateUserKey(username, userKey);
            
            if (isValid) {

                yumProxy.server.user.UserBanManager.BanStatus banStatus = 
                    yumProxy.server.user.UserBanManager.checkBanStatus(username);
                
                if (banStatus.isBanned) {

                    result.put("success", false);
                    result.put("message", "閻犳劧闄勯崺娑橆啅閼奸娼堕悘蹇庤兌椤? " + banStatus.reason);
                    result.put("username", username);
                    result.put("valid", false);
                    result.put("error_code", "USER_BANNED");
                    
                    Map<String, Object> banInfo = new HashMap<>();
                    banInfo.put("banned_by", banStatus.bannedBy);
                    banInfo.put("banned_at", banStatus.bannedAt != null ? banStatus.bannedAt.toString() : null);
                    banInfo.put("banned_until", banStatus.bannedUntil != null ? banStatus.bannedUntil.toString() : null);
                    banInfo.put("ban_type", banStatus.bannedUntil == null ? "婵﹢鏅茬粻娆戜焊娴ｄ警娲? : "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?);
                    result.put("ban_info", banInfo);
                    
                    result.put("request_id", requestId);
                    result.put("timestamp", System.currentTimeMillis());
                    sendResponse(conn, "user_validate_token", result);
                    return;
                }
                

                

                Map<String, Object> userInfo = userManager.getUserInfo(username);
                
                result.put("success", true);
                result.put("message", "Token濡ょ姴鐭侀惁澶愬箣閹邦剙顫?);
                result.put("username", username);
                result.put("valid", true);
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                

                if (userInfo != null) {
                    Map<String, Object> basicInfo = new HashMap<>();
                    basicInfo.put("username", userInfo.get("username"));
                    basicInfo.put("email", userInfo.get("email"));
                    basicInfo.put("created_time", userInfo.get("created_time"));
                    result.put("user_info", basicInfo);
                }
                
                sendResponse(conn, "user_validate_token", result);
            } else {

                result.put("success", false);
                result.put("message", "Token闁哄啰濮甸弲銉╁箣閺嵮冨殥閺夆晛娲﹀﹢?);
                result.put("username", username);
                result.put("valid", false);
                result.put("error_code", "INVALID_TOKEN");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_validate_token", result);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "user_validate_token", result);
        }
    }
} 
