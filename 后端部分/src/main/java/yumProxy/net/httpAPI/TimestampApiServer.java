package yumProxy.net.httpAPI;

import com.google.gson.Gson;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.timestamp.TimestampInfo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

public class TimestampApiServer {
    private static final Gson gson = new Gson();

    private static void log(String msg) {

    }

    
    public static void handleTimestamp(HttpExchange exchange) throws IOException {
            try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }

            Headers reqHeaders = exchange.getRequestHeaders();
                String actionHeader = reqHeaders.getFirst("action");
                
                Map<String, Object> result = new HashMap<>();
                Map<String, String> reqData = new HashMap<>();
                
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                    reqData = gson.fromJson(reader, reqData.getClass());
                } catch (Exception e) {

                }


                if (actionHeader == null || actionHeader.isEmpty()) {
                    actionHeader = reqData.getOrDefault("action", "");
                }
                


            Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("action", actionHeader != null ? actionHeader : "");
                respHeaders.set("Content-Type", "application/json; charset=UTF-8");

                String username = reqData.getOrDefault("username", "");
                String hoursStr = reqData.getOrDefault("hours", "");


                
                if ("Activate".equalsIgnoreCase(actionHeader)) {


                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleActivate(username, hoursStr, result);
                } else if ("Query".equalsIgnoreCase(actionHeader)) {

                    String userKey = reqData.getOrDefault("user_key", "");
                    if (!AuthValidator.hasUserPermission(username, userKey)) {

                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "User key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleQuery(username, result);
                } else if ("Extend".equalsIgnoreCase(actionHeader)) {


                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleExtend(username, hoursStr, result);
                } else if ("Deactivate".equalsIgnoreCase(actionHeader)) {


                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleDeactivate(username, result);
                } else if ("Delete".equalsIgnoreCase(actionHeader)) {


                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleDelete(username, result);
                } else if ("CheckActive".equalsIgnoreCase(actionHeader)) {


                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key required");
                        result.put("Time", "");
                    sendJson(exchange, result);
                        return;
                    }
                    handleCheckActive(username, result);
                } else {

                    result.put("code", 400);
                    result.put("data", "Bad Request");
                    result.put("message", "Unknown action: " + actionHeader);
                    result.put("Time", "");
                }
                
            sendJson(exchange, result);
                
            } catch (Exception e) {

                e.printStackTrace();
                
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("code", 500);
                errorResult.put("data", "Internal Server Error");
                errorResult.put("message", "濠㈣泛瀚幃濠勬嫚闁垮婀撮柡鍐硾閸ゎ參鏌? " + e.getMessage());
                errorResult.put("Time", "");
            sendJson(exchange, errorResult);
            }
    }

    
    private static void handleActivate(String username, String hoursStr, Map<String, Object> result) {
        if (username.isEmpty() || hoursStr.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username or hours");
            result.put("Time", "");
            return;
        }

        try {
            int hours = Integer.parseInt(hoursStr);
            if (hours <= 0) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Hours must be greater than 0");
                result.put("Time", "");
                return;
            }
            
            if (TimestampManager.activatePlayer(username, hours)) {

                result.put("code", 200);
                result.put("data", "Activated");
                result.put("message", "Player activated successfully");
                result.put("Time", "");
            } else {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Activation failed");
                result.put("Time", "");
            }
        } catch (NumberFormatException e) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Invalid hours format");
            result.put("Time", "");
        }
    }

    
    private static void handleQuery(String username, Map<String, Object> result) {
        if (username.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username");
            result.put("Time", "");
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
        } else {

            result.put("code", 200);
            result.put("data", "Not Activated");
            result.put("message", "Player not activated yet");
            result.put("username", username);
            result.put("activatedAt", "");
            result.put("expiresAt", "");
            result.put("isActive", false);
            result.put("isExpired", true);
            result.put("Time", "");
        }
    }

    
    private static void handleExtend(String username, String hoursStr, Map<String, Object> result) {
        if (username.isEmpty() || hoursStr.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username or hours");
            result.put("Time", "");
            return;
        }

        try {
            int hours = Integer.parseInt(hoursStr);
            if (hours <= 0) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Hours must be greater than 0");
                result.put("Time", "");
                return;
            }
            
            if (TimestampManager.extendPlayerTime(username, hours)) {

                result.put("code", 200);
                result.put("data", "Extended");
                result.put("message", "Player time extended successfully");
                result.put("Time", "");
            } else {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Extension failed");
                result.put("Time", "");
            }
        } catch (NumberFormatException e) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Invalid hours format");
            result.put("Time", "");
        }
    }

    
    private static void handleDeactivate(String username, Map<String, Object> result) {
        if (username.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username");
            result.put("Time", "");
            return;
        }

        if (TimestampManager.deactivatePlayer(username)) {

            result.put("code", 200);
            result.put("data", "Deactivated");
            result.put("message", "Player deactivated successfully");
            result.put("Time", "");
        } else {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Deactivation failed");
            result.put("Time", "");
        }
    }

    
    private static void handleDelete(String username, Map<String, Object> result) {
        if (username.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username");
            result.put("Time", "");
            return;
        }

        if (TimestampManager.deletePlayer(username)) {

            result.put("code", 200);
            result.put("data", "Deleted");
            result.put("message", "Player deleted successfully");
            result.put("Time", "");
        } else {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Deletion failed");
            result.put("Time", "");
        }
    }

    
    private static void handleCheckActive(String username, Map<String, Object> result) {
        if (username.isEmpty()) {

            result.put("code", 1);
            result.put("data", "ERROR");
            result.put("message", "Missing username");
            result.put("Time", "");
            return;
        }

        TimestampInfo info = TimestampManager.getPlayerTimestamp(username);
        if (info != null) {
            boolean isActive = info.isActive && !info.isExpired;

            result.put("code", 200);
            result.put("data", isActive ? "Active" : "Inactive");
            result.put("message", isActive ? "Player is active" : "Player is not active");
            result.put("isActive", isActive);
            result.put("Time", "");
        } else {

            result.put("code", 200);
            result.put("data", "Inactive");
            result.put("message", "Player not activated yet");
            result.put("isActive", false);
            result.put("Time", "");
        }
    }



    
    public static void handleUserTimestamp(HttpExchange exchange) throws IOException {
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

            String username = reqData.getOrDefault("username", "");

            if (username.isEmpty()) {

                result.put("code", 1);
                result.put("data", "ERROR");
                result.put("message", "Missing username");
                result.put("Time", "");
                sendJson(exchange, result);
                return;
            }

            TimestampInfo info = TimestampManager.getPlayerTimestamp(username);
            if (info != null) {

                result.put("code", 200);
                result.put("data", "Found");
                result.put("username", info.username);

                String expiresAtStr = "";
                if (info.activatedAt != null) {
                    activatedAtStr = info.activatedAt.toInstant()
                        .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                if (info.expiresAt != null) {
                    expiresAtStr = info.expiresAt.toInstant()
                        .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                result.put("activatedAt", activatedAtStr);
                result.put("expiresAt", expiresAtStr);
                result.put("isActive", info.isActive);
                result.put("isExpired", info.isExpired);
                result.put("Time", "");
            } else {

                result.put("code", 200);
                result.put("data", "Not Activated");
                result.put("username", username);
                result.put("activatedAt", "");
                result.put("expiresAt", "");
                result.put("isActive", false);
                result.put("isExpired", true);
                result.put("Time", "");
            }

            sendJson(exchange, result);
        } catch (Exception e) {

        }
    }

    private static void sendJson(HttpExchange exchange, Map<String, Object> result) throws IOException {
        String json = gson.toJson(result);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
} 
