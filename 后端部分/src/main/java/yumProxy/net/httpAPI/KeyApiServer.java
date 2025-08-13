package yumProxy.net.httpAPI;

import com.google.gson.Gson;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.key.KeyManager;
import yumProxy.server.key.Key;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class KeyApiServer {
    private static final Gson gson = new Gson();

    private static void log(String msg) {

    }

    public static void handleKeyUsed(HttpExchange exchange) throws IOException {
            try {

            if (!HttpMessageHandler.isMessageEnabled(exchange)) {
                    return;
                }
                
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }
            Headers reqHeaders = exchange.getRequestHeaders();
                String superKey = reqHeaders.getFirst("super_key");
                String keyHeader = reqHeaders.getFirst("key");
                String usedHeader = reqHeaders.getFirst("used");


            Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("key", keyHeader != null ? keyHeader : "");
                respHeaders.set("used", usedHeader != null ? usedHeader : "");
                respHeaders.set("Content-Type", "application/json; charset=UTF-8");

                Map<String, Object> result = new HashMap<>();

                if (keyHeader == null || keyHeader.trim().isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                String[] parts = keyHeader.split("-", 2);
                if (parts.length != 2) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }
                String prefix = parts[0];
                String code = parts[1];

                String checkUsedSql = "SELECT id FROM used_keys WHERE prefix = ? AND code = ?";
                if (!MySQLUtils.executeQuery(checkUsedSql, prefix, code).isEmpty()) {

                    result.put("code", 404);
                    result.put("data", "Used");
                    result.put("Time", "");
                sendJson(exchange, result);
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
                sendJson(exchange, result);
                    return;
                } else {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("Time", "");
                sendJson(exchange, result);
                }
            } catch (Exception e) {

            }
    }

    
    public static void handleKeyCreate(HttpExchange exchange) throws IOException {
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


                String superKey = reqData.getOrDefault("super_key", "");
                
                if (!AuthValidator.isAdmin(superKey)) {

                    result.put("code", 403);
                    result.put("data", "Forbidden");
                    result.put("message", "Super key required");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                String prefix = reqData.getOrDefault("prefix", "");
                String countStr = reqData.getOrDefault("count", "1");
                String timeStr = reqData.getOrDefault("time", "0");

                if (prefix.isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Missing prefix");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                try {
                    int count = Integer.parseInt(countStr);
                    int time = Integer.parseInt(timeStr);
                    
                    if (count <= 0) {

                        result.put("code", 1);
                        result.put("data", "ERROR");
                        result.put("message", "Count must be greater than 0");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    
                    if (time < 0) {

                        result.put("code", 1);
                        result.put("data", "ERROR");
                        result.put("message", "Time cannot be negative");
                        result.put("Time", "");
                    sendJson(exchange, result);
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

            sendJson(exchange, result);
            } catch (Exception e) {

            }
    }

    
    public static void handleKeyQuery(HttpExchange exchange) throws IOException {
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


                String superKey = reqData.getOrDefault("super_key", "");
                
                if (!AuthValidator.isAdmin(superKey)) {

                    result.put("code", 403);
                    result.put("data", "Forbidden");
                    result.put("message", "Super key required");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                String prefix = reqData.getOrDefault("prefix", "");

                if (prefix.isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Missing prefix");
                    result.put("Time", "");
                sendJson(exchange, result);
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

            sendJson(exchange, result);
            } catch (Exception e) {

            }
    }

    
    public static void handleKeyDelete(HttpExchange exchange) throws IOException {
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


                String superKey = reqData.getOrDefault("super_key", "");
                
                if (!AuthValidator.isAdmin(superKey)) {

                    result.put("code", 403);
                    result.put("data", "Forbidden");
                    result.put("message", "Super key required");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                String fullKey = reqData.getOrDefault("key", "");

                if (fullKey.isEmpty()) {

                    result.put("code", 1);
                    result.put("data", "ERROR");
                    result.put("message", "Missing key");
                    result.put("Time", "");
                sendJson(exchange, result);
                    return;
                }

                try {
                    String[] parts = fullKey.split("-", 2);
                    if (parts.length != 2) {

                        result.put("code", 1);
                        result.put("data", "ERROR");
                        result.put("message", "Invalid key format");
                        result.put("Time", "");
                    sendJson(exchange, result);
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

            sendJson(exchange, result);
            } catch (Exception e) {

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
