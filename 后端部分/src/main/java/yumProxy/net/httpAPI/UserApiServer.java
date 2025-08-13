package yumProxy.net.httpAPI;

import com.google.gson.Gson;
import yumProxy.server.user.UserManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class UserApiServer {
    private static final Gson gson = new Gson();
    private static final UserManager userManager = new UserManager();

    public static UserManager getUserManager() { return userManager; }

    private static void log(String msg) {

    }

    public static void handleUser(HttpExchange exchange) throws IOException {
            int statusCode = 200;
            try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }
            Headers reqHeaders = exchange.getRequestHeaders();
                String userHeader = reqHeaders.getFirst("user");
                String superKey = reqHeaders.getFirst("super_key");

            Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.set("user", userHeader != null ? userHeader : "");
                respHeaders.set("Content-Type", "application/json; charset=UTF-8");

                Map<String, Object> result = new HashMap<>();
                Map<String, Object> reqData = new HashMap<>();
            String rawBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

                    Type type = new TypeToken<Map<String, Object>>(){}.getType();
                    reqData = gson.fromJson(rawBody, type);
                } catch (Exception e) {

                    result.put("code", 400);
                    result.put("data", "ERROR");
                    result.put("message", "Invalid JSON body");
                    result.put("Time", "");
                    statusCode = 400;
                sendJson(exchange, result, statusCode);
                    return;
                }

                String username = reqData.getOrDefault("username", "").toString();
                String password = reqData.getOrDefault("password", "").toString();
                String email = reqData.getOrDefault("email", "").toString();
                String code = reqData.getOrDefault("code", "").toString();


                String processedEmail = processEmailInput(email);


                if ("Register".equalsIgnoreCase(userHeader)) {
                    if (username.isEmpty() || password.isEmpty() || email.isEmpty() || code.isEmpty()) {

                        result.put("code", 400);
                        result.put("data", "ERROR");
                        result.put("message", "Missing parameters");
                        result.put("Time", "");
                        statusCode = 400;
                    } else if (userManager.exists(username)) {

                        result.put("code", 409);
                        result.put("data", "User Exists");
                        result.put("message", "User already exists");
                        result.put("Time", "");
                        statusCode = 409;
                    } else if (userManager.register(username, password, processedEmail, code)) {
                        String userKey = yumProxy.server.user.UserKeyManager.generateAndSaveUserKey(username);

                        result.put("code", 200);
                        result.put("data", "Register Succeed");
                        result.put("user_key", userKey);
                        result.put("Time", "");
                        statusCode = 200;
                    } else {

                        result.put("code", 500);
                        result.put("data", "ERROR");
                        result.put("message", "Register failed");
                        result.put("Time", "");
                        statusCode = 500;
                    }
                } else if ("Login".equalsIgnoreCase(userHeader)) {
                    if (username.isEmpty() || password.isEmpty()) {

                        result.put("code", 400);
                        result.put("data", "ERROR");
                        result.put("message", "Missing parameters");
                        result.put("Time", "");
                        statusCode = 400;
                    } else {

                        yumProxy.server.user.UserBanManager.BanStatus banStatus = 
                            yumProxy.server.user.UserBanManager.checkBanStatus(username);
                        
                        if (banStatus.isBanned) {

                            result.put("code", 403);
                            result.put("data", "Login Forbidden");
                            result.put("message", "閻犳劧闄勯崺娑橆啅閼奸娼堕悘蹇庤兌椤? " + banStatus.reason);
                            result.put("ban_reason", banStatus.reason);
                            result.put("banned_by", banStatus.bannedBy);
                            if (banStatus.bannedAt != null) {
                                result.put("banned_at", banStatus.bannedAt.toString());
                            }
                            if (banStatus.bannedUntil != null) {
                                result.put("banned_until", banStatus.bannedUntil.toString());
                                result.put("ban_type", "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?);
                            } else {
                                result.put("ban_type", "婵﹢鏅茬粻娆戜焊娴ｄ警娲?);
                            }
                            result.put("Time", "");
                            statusCode = 403;
                        } else if (userManager.login(username, password)) {
                            String userKey = yumProxy.server.user.UserKeyManager.getUserKey(username);

                            result.put("code", 200);
                            result.put("data", "Login Succeed");
                            result.put("user_key", userKey);
                            result.put("Time", "");
                            statusCode = 200;
                        } else {

                            result.put("code", 401);
                            result.put("data", "Login Failed");
                            result.put("message", "Login failed");
                            result.put("Time", "");
                            statusCode = 401;
                        }
                    }
                } else if ("Delete".equalsIgnoreCase(userHeader)) {
                    String targetUsername = reqData.getOrDefault("target_username", "").toString();
                    String userKey = reqData.getOrDefault("user_key", "").toString();
                    if (!AuthValidator.hasPermission(targetUsername, userKey, superKey)) {

                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key or user key required");
                        result.put("Time", "");
                        statusCode = 403;
                    } else if (targetUsername.isEmpty()) {

                        result.put("code", 400);
                        result.put("data", "ERROR");
                        result.put("message", "Missing target_username");
                        result.put("Time", "");
                        statusCode = 400;
                    } else if (userManager.deleteUser(targetUsername)) {
                        yumProxy.server.user.UserKeyManager.deleteUserKey(targetUsername);

                        result.put("code", 200);
                        result.put("data", "Deleted");
                        result.put("message", "User deleted successfully");
                        result.put("target_username", targetUsername);
                        result.put("Time", "");
                        statusCode = 200;
                    } else {

                        result.put("code", 500);
                        result.put("data", "ERROR");
                        result.put("message", "Delete failed");
                        result.put("Time", "");
                        statusCode = 500;
                    }
                } else if ("GetInfo".equalsIgnoreCase(userHeader)) {
                    String targetUsername = reqData.getOrDefault("target_username", "").toString();
                    String userKey = reqData.getOrDefault("user_key", "").toString();
                    if (!AuthValidator.hasPermission(targetUsername, userKey, superKey)) {

                        result.put("code", 403);
                        result.put("data", "Forbidden");
                        result.put("message", "Super key or user key required");
                        result.put("Time", "");
                        statusCode = 403;
                    } else if (targetUsername.isEmpty()) {

                        result.put("code", 400);
                        result.put("data", "ERROR");
                        result.put("message", "Missing target_username");
                        result.put("Time", "");
                        statusCode = 400;
                    } else {
                        Map<String, Object> userInfo = userManager.getUserInfo(targetUsername);
                        if (userInfo != null) {

                            result.put("code", 200);
                            result.put("data", "OK");
                            result.put("user_info", userInfo);
                            result.put("Time", "");
                            statusCode = 200;
                        } else {

                            result.put("code", 404);
                            result.put("data", "NOT_FOUND");
                            result.put("message", "User not found");
                            result.put("Time", "");
                            statusCode = 404;
                        }
                    }
                } else {

                    result.put("code", 400);
                    result.put("data", "ERROR");
                    result.put("message", "Unknown action");
                    result.put("Time", "");
                    statusCode = 400;
                }

                sendJson(exchange, result, statusCode);
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("data", "ERROR");
            errorResult.put("message", "Internal error");
            errorResult.put("Time", "");
            sendJson(exchange, errorResult, 500);
        }
    }

    private static void sendJson(HttpExchange exchange, Map<String, Object> result) throws IOException {
        sendJson(exchange, result, 200);
    }

    private static void sendJson(HttpExchange exchange, Map<String, Object> result, int statusCode) throws IOException {
        String json = gson.toJson(result);
        exchange.getResponseHeaders().set("X-Raw-Response", json);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }


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
} 
