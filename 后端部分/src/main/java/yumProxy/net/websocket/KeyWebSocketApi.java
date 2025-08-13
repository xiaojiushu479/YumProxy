package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.key.KeyManager;
import yumProxy.server.key.Key;
import yumProxy.net.httpAPI.AuthValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class KeyWebSocketApi {
    
    private static void log(String msg) {

    }
    
    
    public static void handleKeyUsed(WebSocket conn, JSONObject data) {
        try {
            String key = data.getString("key");
            String username = data.getString("username");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            
            if (key == null || key.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("Time", "");
                sendResponse(conn, "key_used", result);
                return;
            }
            
            String[] parts = key.split("-", 2);
            if (parts.length != 2) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("Time", "");
                sendResponse(conn, "key_used", result);
                return;
            }
            
            String prefix = parts[0];
            String code = parts[1];
            

            String checkUsedSql = "SELECT id FROM used_keys WHERE prefix = ? AND code = ?";
            if (!MySQLUtils.executeQuery(checkUsedSql, prefix, code).isEmpty()) {

                result.put("code", 404);
                result.put("data", "Used");
                result.put("Time", "");
                sendResponse(conn, "key_used", result);
                return;
            }
            

            String checkSql = "SELECT id, time_hours FROM `keys` WHERE prefix = ? AND code = ?";
            List<Map<String, Object>> keyResults = MySQLUtils.executeQuery(checkSql, prefix, code);
            
            if (!keyResults.isEmpty()) {
                Map<String, Object> keyData = keyResults.get(0);
                int timeHours = ((Number) keyData.get("time_hours")).intValue();
                

                if (username != null && !username.trim().isEmpty() && timeHours > 0) {
                    boolean activated = TimestampManager.activatePlayer(username, timeHours);
                    if (activated) {

                    } else {

                    }
                }
                

                String delSql = "DELETE FROM `keys` WHERE prefix = ? AND code = ?";
                MySQLUtils.executeUpdate(delSql, prefix, code);
                String insertUsedSql = "INSERT INTO used_keys (prefix, code) VALUES (?, ?)";
                MySQLUtils.executeUpdate(insertUsedSql, prefix, code);
                

                result.put("code", 200);
                result.put("data", "Succeed");
                result.put("Time", String.valueOf(timeHours));
                sendResponse(conn, "key_used", result);
                return;
            } else {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("Time", "");
                sendResponse(conn, "key_used", result);
            }
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "key_used", errorResult);
        }
    }
    
    
    public static void handleKeyCreate(WebSocket conn, JSONObject data) {
        try {
            String superKey = data.getString("super_key");
            String prefix = data.getString("prefix");
            String countStr = data.getString("count");
            String timeStr = data.getString("time");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "key_create", result);
                return;
            }
            
            if (prefix == null || prefix.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing prefix");
                result.put("Time", "");
                sendResponse(conn, "key_create", result);
                return;
            }
            
            try {
                int count = Integer.parseInt(countStr != null ? countStr : "1");
                int time = Integer.parseInt(timeStr != null ? timeStr : "0");
                
                if (count <= 0) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Count must be greater than 0");
                    result.put("Time", "");
                    sendResponse(conn, "key_create", result);
                    return;
                }
                
                if (time < 0) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Time cannot be negative");
                    result.put("Time", "");
                    sendResponse(conn, "key_create", result);
                    return;
                }
                
                KeyManager keyManager = new KeyManager();
                List<Key> keys = keyManager.distributeKeys(prefix, count, time);
                
                List<String> keyList = new ArrayList<>();
                for (Key key : keys) {
                    keyList.add(key.toString());
                }
                

                result.put("code", 200);
                result.put("data", "Created");
                result.put("message", "Keys created successfully");
                result.put("prefix", prefix);
                result.put("count", count);
                result.put("time", time);
                result.put("keys", keyList);
                result.put("Time", "");
                
            } catch (NumberFormatException e) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Invalid count or time format");
                result.put("Time", "");
            }
            
            sendResponse(conn, "key_create", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "key_create", errorResult);
        }
    }
    
    
    public static void handleKeyQuery(WebSocket conn, JSONObject data) {
        try {
            String superKey = data.getString("super_key");
            String prefix = data.getString("prefix");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "key_query", result);
                return;
            }
            
            if (prefix == null || prefix.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing prefix");
                result.put("Time", "");
                sendResponse(conn, "key_query", result);
                return;
            }
            
            try {
                String sql = "SELECT prefix, code, time_hours, create_time FROM `keys` WHERE prefix = ? ORDER BY create_time DESC LIMIT 50";
                List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, prefix);
                
                List<Map<String, Object>> keyList = new ArrayList<>();
                for (Map<String, Object> row : results) {
                    Map<String, Object> keyInfo = new HashMap<>();
                    keyInfo.put("prefix", row.get("prefix"));
                    keyInfo.put("code", row.get("code"));
                    keyInfo.put("fullKey", row.get("prefix") + "-" + row.get("code"));
                    keyInfo.put("timeHours", row.get("time_hours"));
                    keyInfo.put("createTime", row.get("create_time"));
                    keyList.add(keyInfo);
                }
                

                result.put("code", 200);
                result.put("data", "Found");
                result.put("message", "Keys found successfully");
                result.put("prefix", prefix);
                result.put("count", results.size());
                result.put("keys", keyList);
                result.put("Time", "");
                
            } catch (Exception e) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Query failed: " + e.getMessage());
                result.put("Time", "");
            }
            
            sendResponse(conn, "key_query", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "key_query", errorResult);
        }
    }
    
    
    public static void handleKeyDelete(WebSocket conn, JSONObject data) {
        try {
            String superKey = data.getString("super_key");
            String fullKey = data.getString("key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("data", "Forbidden");
                result.put("message", "Super key required");
                result.put("Time", "");
                sendResponse(conn, "key_delete", result);
                return;
            }
            
            if (fullKey == null || fullKey.trim().isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing key");
                result.put("Time", "");
                sendResponse(conn, "key_delete", result);
                return;
            }
            
            try {
                String[] parts = fullKey.split("-", 2);
                if (parts.length != 2) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Invalid key format");
                    result.put("Time", "");
                    sendResponse(conn, "key_delete", result);
                    return;
                }
                
                String prefix = parts[0];
                String code = parts[1];
                
                String deleteSql = "DELETE FROM `keys` WHERE prefix = ? AND code = ?";
                int affected = MySQLUtils.executeUpdate(deleteSql, prefix, code);
                
                if (affected > 0) {

                    result.put("code", 200);
                    result.put("data", "Deleted");
                    result.put("message", "Key deleted successfully");
                    result.put("key", fullKey);
                    result.put("Time", "");
                } else {

                    result.put("code", 404);
                    result.put("data", "Not Found");
                    result.put("message", "Key not found");
                    result.put("Time", "");
                }
                
            } catch (Exception e) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Delete failed: " + e.getMessage());
                result.put("Time", "");
            }
            
            sendResponse(conn, "key_delete", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage());
            errorResult.put("Time", "");
            sendResponse(conn, "key_delete", errorResult);
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
} 
