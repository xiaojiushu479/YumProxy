package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.timestamp.TimestampInfo;
import yumProxy.net.httpAPI.AuthValidator;

import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

public class TimestampWebSocketApi {
    
    private static void log(String msg) {

    }
    
    
    public static void handleTimestampActivate(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String hoursStr = data.getString("hours");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "timestamp_activate", result, requestId);
                return;
            }
            
            if (username == null || username.trim().isEmpty() || 
                hoursStr == null || hoursStr.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username or hours");
                result.put("Time", "");
                sendResponse(conn, "timestamp_activate", result, requestId);
                return;
            }
            
            try {
                int hours = Integer.parseInt(hoursStr);
                if (hours <= 0) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Hours must be greater than 0");
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_activate", result, requestId);
                    return;
                }
                
                if (TimestampManager.activatePlayer(username, hours)) {

                    result.put("code", 200);
                    result.put("data", "Activated");
                    result.put("message", "Player activated successfully");
                    result.put("username", username);
                    result.put("hours", hours);
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_activate", result, requestId);
                } else {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Activation failed");
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_activate", result, requestId);
                }
            } catch (NumberFormatException e) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Invalid hours format");
                result.put("Time", "");
                sendResponse(conn, "timestamp_activate", result, requestId);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_activate", errorResult, requestId);
        }
    }
    
    
    public static void handleTimestampQuery(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            


                ", superKey: " + (superKey != null ? "鐎圭寮惰ぐ浣圭瑹? : "闁哄牜浜濊ぐ浣圭瑹?));
            
            Map<String, Object> result = new HashMap<>();
            
            if (username == null || username.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username parameter");
                result.put("Time", "");
                sendResponse(conn, "timestamp_query", result, requestId);
                return;
            }
            

            String authMethod = "";
            

                hasValidAuth = true;
                authMethod = "閻℃帒鎳愭鍥┾偓闈涙閹?;

            }

            else if (userKey != null && !userKey.trim().isEmpty() && 
                     AuthValidator.hasUserPermission(username, userKey)) {
                hasValidAuth = true;
                authMethod = "闁活潿鍔嶉崺娑氣偓闈涙閹?;

            }
            
            if (!hasValidAuth) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "闂傚洠鍋撻悷鏇氱劍濠€渚€寮崼銏＄暠闁活潿鍔嶉崺娑氣偓闈涙閹告粓骞嬮弽顒傂㈢紒鐙欏啰妲曢梺濮愬劥缁绘鎮板畝鍐惧悋閻?);
                result.put("Time", "");
                sendResponse(conn, "timestamp_query", result, requestId);
                return;
            }
            
            TimestampInfo info = TimestampManager.getPlayerTimestamp(username);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            if (info != null) {

                result.put("code", 200);
                result.put("data", "Found");
                result.put("message", "Player found");
                result.put("username", info.username);

                String expiresAtStr = "";
                if (info.activatedAt != null) {
                    activatedAtStr = info.activatedAt.toInstant()
                        .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                        .format(formatter);
                }
                if (info.expiresAt != null) {
                    expiresAtStr = info.expiresAt.toInstant()
                        .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                        .format(formatter);
                }
                result.put("activatedAt", activatedAtStr);
                result.put("expiresAt", expiresAtStr);
                result.put("isActive", info.isActive);
                result.put("isExpired", info.isExpired);
                result.put("Time", "");
                sendResponse(conn, "timestamp_query", result, requestId);
            } else {

                result.put("code", 200);
                result.put("data", "Not Activated");
                result.put("message", "闁绘壕鏅涢宥囦焊濮橆厽寮撴繝纰樺亾婵炶尙绮鍌炴⒒鐎涙ê鐓堥柨娑樼焷椤曨剟宕楅崼锝呮灎濞戞梻澧楅崹銊モ攽閳ь剙煤缂佹ɑ绠涢柛?);
                result.put("username", username);
                result.put("activatedAt", "");
                result.put("expiresAt", "");
                result.put("isActive", false);
                result.put("isExpired", true);
                result.put("Time", "");
                sendResponse(conn, "timestamp_query", result, requestId);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_query", errorResult, requestId);
        }
    }
    
    
    public static void handleTimestampExtend(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String hoursStr = data.getString("hours");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "timestamp_extend", result);
                return;
            }
            
            if (username == null || username.trim().isEmpty() || 
                hoursStr == null || hoursStr.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username or hours");
                result.put("Time", "");
                sendResponse(conn, "timestamp_extend", result);
                return;
            }
            
            try {
                int hours = Integer.parseInt(hoursStr);
                if (hours <= 0) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Hours must be greater than 0");
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_extend", result);
                    return;
                }
                
                if (TimestampManager.extendPlayerTime(username, hours)) {

                    result.put("code", 200);
                    result.put("data", "Extended");
                    result.put("message", "Player extended successfully");
                    result.put("username", username);
                    result.put("hours", hours);
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_extend", result);
                } else {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Extension failed");
                    result.put("Time", "");
                    sendResponse(conn, "timestamp_extend", result);
                }
            } catch (NumberFormatException e) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Invalid hours format");
                result.put("Time", "");
                sendResponse(conn, "timestamp_extend", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_extend", errorResult);
        }
    }
    
    
    public static void handleTimestampDeactivate(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "timestamp_deactivate", result);
                return;
            }
            
            if (username == null || username.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username");
                result.put("Time", "");
                sendResponse(conn, "timestamp_deactivate", result);
                return;
            }
            
            if (TimestampManager.deactivatePlayer(username)) {

                result.put("code", 200);
                result.put("data", "Deactivated");
                result.put("message", "Player deactivated successfully");
                result.put("username", username);
                result.put("Time", "");
                sendResponse(conn, "timestamp_deactivate", result);
            } else {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Deactivation failed");
                result.put("Time", "");
                sendResponse(conn, "timestamp_deactivate", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_deactivate", errorResult);
        }
    }
    
    
    public static void handleTimestampDelete(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "timestamp_delete", result);
                return;
            }
            
            if (username == null || username.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username");
                result.put("Time", "");
                sendResponse(conn, "timestamp_delete", result);
                return;
            }
            
            if (TimestampManager.deletePlayer(username)) {

                result.put("code", 200);
                result.put("data", "Deleted");
                result.put("message", "Player deleted successfully");
                result.put("username", username);
                result.put("Time", "");
                sendResponse(conn, "timestamp_delete", result);
            } else {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Delete failed");
                result.put("Time", "");
                sendResponse(conn, "timestamp_delete", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_delete", errorResult);
        }
    }
    
    
    public static void handleTimestampCheckActive(WebSocket conn, JSONObject data, String requestId) {
        try {
            String username = data.getString("username");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (username == null || username.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username");
                result.put("Time", "");
                sendResponse(conn, "timestamp_check_active", result);
                return;
            }
            
            boolean isActive = TimestampManager.isPlayerActive(username);

            result.put("code", 200);
            result.put("data", "Checked");
            result.put("message", "Player status checked successfully");
            result.put("username", username);
            result.put("isActive", isActive);
            result.put("Time", "");
            sendResponse(conn, "timestamp_check_active", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "timestamp_check_active", errorResult);
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
    

    public static void handleTimestampExtend(WebSocket conn, JSONObject data) {
        handleTimestampExtend(conn, data, null);
    }
    
    public static void handleTimestampDeactivate(WebSocket conn, JSONObject data) {
        handleTimestampDeactivate(conn, data, null);
    }
    
    public static void handleTimestampDelete(WebSocket conn, JSONObject data) {
        handleTimestampDelete(conn, data, null);
    }
    
    public static void handleTimestampCheckActive(WebSocket conn, JSONObject data) {
        handleTimestampCheckActive(conn, data, null);
    }
    
    public static void handleTimestampActivate(WebSocket conn, JSONObject data) {
        handleTimestampActivate(conn, data, null);
    }
} 
