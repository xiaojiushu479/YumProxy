package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.server.user.UserBanManager;
import yumProxy.net.httpAPI.AuthValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.Timestamp;


public class UserBanWebSocketApi {
    
    private static void log(String msg) {

    }
    
    
    public static void handleBanUser(WebSocket conn, JSONObject data, String requestId) {
        try {
            String targetUsername = data.getString("target_username");
            String reason = data.getString("reason");
            String superKey = data.getString("super_key");
            Integer duration = data.getInteger("duration");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("success", false);
                result.put("message", "闂傚洠鍋撻悷鏇氳兌椤撴悂鎮堕崱妤佸枀闁哄鍟村?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban", result);
                return;
            }
            

            if (targetUsername == null || targetUsername.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "闁烩晩鍠楅悥锝夋偨閵婏箑鐓曢柛姘С缁楀鎳楅幋鎺曠缂?);
                result.put("error_code", "MISSING_TARGET_USERNAME");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban", result);
                return;
            }
            
            if (reason == null || reason.trim().isEmpty()) {
                reason = "缂佺媴绱曢幃濠囧川濡櫣娈辩紒?;
            }
            

            UserBanManager.BanResult banResult = UserBanManager.banUser(
                targetUsername, reason, "ADMIN", duration);
            
            if (banResult.success) {

                result.put("success", true);
                result.put("message", banResult.message);
                result.put("target_username", targetUsername);
                result.put("ban_reason", reason);
                result.put("duration", duration);
                if (banResult.bannedUntil != null) {
                    result.put("banned_until", banResult.bannedUntil.toString());
                }
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban", result);
            } else {

                result.put("success", false);
                result.put("message", banResult.message);
                result.put("target_username", targetUsername);
                result.put("error_code", "BAN_FAILED");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban", result);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "user_ban", result);
        }
    }
    
    
    public static void handleUnbanUser(WebSocket conn, JSONObject data, String requestId) {
        try {
            String targetUsername = data.getString("target_username");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("success", false);
                result.put("message", "闂傚洠鍋撻悷鏇氳兌椤撴悂鎮堕崱妤佸枀闁哄鍟村?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_unban", result);
                return;
            }
            

            if (targetUsername == null || targetUsername.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "闁烩晩鍠楅悥锝夋偨閵婏箑鐓曢柛姘С缁楀鎳楅幋鎺曠缂?);
                result.put("error_code", "MISSING_TARGET_USERNAME");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_unban", result);
                return;
            }
            

            UserBanManager.BanResult unbanResult = UserBanManager.unbanUser(targetUsername, "ADMIN");
            
            if (unbanResult.success) {

                result.put("success", true);
                result.put("message", unbanResult.message);
                result.put("target_username", targetUsername);
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_unban", result);
            } else {

                result.put("success", false);
                result.put("message", unbanResult.message);
                result.put("target_username", targetUsername);
                result.put("error_code", "UNBAN_FAILED");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_unban", result);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "user_unban", result);
        }
    }
    
    
    public static void handleCheckBanStatus(WebSocket conn, JSONObject data, String requestId) {
        try {
            String targetUsername = data.getString("target_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            boolean hasPermission = false;
            if (AuthValidator.isAdmin(superKey)) {
                hasPermission = true;
            } else if (userKey != null && !userKey.trim().isEmpty()) {

                String requestUsername = data.getString("request_username");
                if (targetUsername != null && targetUsername.equals(requestUsername) &&
                    AuthValidator.hasUserPermission(requestUsername, userKey)) {
                    hasPermission = true;
                }
            }
            
            if (!hasPermission) {

                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎欓柟瀛樼墱閺併倝骞嬮柨瀣嫳濞存粎鍎ゅ鍫ユ⒔?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban_status", result);
                return;
            }
            

            if (targetUsername == null || targetUsername.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "闁烩晩鍠楅悥锝夋偨閵婏箑鐓曢柛姘С缁楀鎳楅幋鎺曠缂?);
                result.put("error_code", "MISSING_TARGET_USERNAME");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "user_ban_status", result);
                return;
            }
            

            UserBanManager.BanStatus banStatus = UserBanManager.checkBanStatus(targetUsername);
            
            result.put("success", true);
            result.put("message", "闁哄被鍎撮妤呭箣閹邦剙顫?);
            result.put("target_username", targetUsername);
            result.put("is_banned", banStatus.isBanned);
            result.put("status_message", banStatus.message);
            
            if (banStatus.isBanned) {
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
            }
            
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "user_ban_status", result);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "user_ban_status", result);
        }
    }
    
    
    public static void handleListBannedUsers(WebSocket conn, JSONObject data, String requestId) {
        try {
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("success", false);
                result.put("message", "闂傚洠鍋撻悷鏇氳兌椤撴悂鎮堕崱妤佸枀闁哄鍟村?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "banned_users_list", result);
                return;
            }
            

            List<Map<String, Object>> bannedUsers = UserBanManager.getBannedUsers();
            
            result.put("success", true);
            result.put("message", "闁兼儳鍢茶ぐ鍥箣閹邦剙顫?);
            result.put("total", bannedUsers.size());
            result.put("banned_users", bannedUsers);
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "banned_users_list", result);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "banned_users_list", result);
        }
    }
    
    
    public static void handleGetBanLogs(WebSocket conn, JSONObject data, String requestId) {
        try {
            String superKey = data.getString("super_key");
            String targetUsername = data.getString("target_username");
            Integer limit = data.getInteger("limit");
            if (limit == null || limit <= 0) {
                limit = 50;
            }
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("success", false);
                result.put("message", "闂傚洠鍋撻悷鏇氳兌椤撴悂鎮堕崱妤佸枀闁哄鍟村?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "ban_logs", result);
                return;
            }
            

            List<Map<String, Object>> banLogs = UserBanManager.getBanLogs(targetUsername, limit);
            
            result.put("success", true);
            result.put("message", "闁兼儳鍢茶ぐ鍥箣閹邦剙顫?);
            result.put("total", banLogs.size());
            result.put("logs", banLogs);
            result.put("target_username", targetUsername);
            result.put("limit", limit);
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "ban_logs", result);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "ban_logs", result);
        }
    }
    
    
    public static void handleCleanExpiredBans(WebSocket conn, JSONObject data, String requestId) {
        try {
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("success", false);
                result.put("message", "闂傚洠鍋撻悷鏇氳兌椤撴悂鎮堕崱妤佸枀闁哄鍟村?);
                result.put("error_code", "INSUFFICIENT_PERMISSION");
                result.put("request_id", requestId);
                result.put("timestamp", System.currentTimeMillis());
                sendResponse(conn, "clean_expired_bans", result);
                return;
            }
            

            int cleanedCount = UserBanManager.cleanExpiredBans();
            
            result.put("success", true);
            result.put("message", "婵炴挸鎳愰幃濠勨偓鐟版湰閸?);
            result.put("cleaned_count", cleanedCount);
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "clean_expired_bans", result);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            result.put("error_code", "INTERNAL_ERROR");
            result.put("request_id", requestId);
            result.put("timestamp", System.currentTimeMillis());
            sendResponse(conn, "clean_expired_bans", result);
        }
    }
    
    
    private static void sendResponse(WebSocket conn, String action, Map<String, Object> data) {
        try {
            JSONObject response = new JSONObject();
            response.put("action", action);
            response.putAll(data);
            conn.send(response.toJSONString());
        } catch (Exception e) {

        }
    }
} 
