package yumProxy.net.websocket;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.java_websocket.WebSocket;
import yumProxy.utils.UserMinecraftBinding;
import yumProxy.server.whitelist.WhitelistManagerV2;
import yumProxy.net.httpAPI.AuthValidator;
import java.util.List;
import java.util.Map;


public class UserBindingWebSocketApi {
    
    
    private static boolean verifyAdminPermission(String superKey) {
        return AuthValidator.isAdmin(superKey);
    }
    
    
    private static boolean verifyUserPermission(String targetUsername, String requestUsername, String userKey, String superKey) {


                          ", requestUsername: " + requestUsername + 
                          ", userKey: " + (userKey != null ? "鐎圭寮惰ぐ浣圭瑹? : "闁哄牜浜濊ぐ浣圭瑹?) + 
                          ", superKey: " + (superKey != null ? "鐎圭寮惰ぐ浣圭瑹? : "闁哄牜浜濊ぐ浣圭瑹?));
        

        if (superKey != null && !superKey.trim().isEmpty()) {
            if (verifyAdminPermission(superKey)) {

                return true;
            } else {

            }
        }
        

        if (requestUsername != null && !requestUsername.trim().isEmpty() && 
            userKey != null && !userKey.trim().isEmpty()) {
            
            if (AuthValidator.hasUserPermission(requestUsername, userKey)) {

                if (targetUsername != null && !targetUsername.equals(requestUsername)) {

                    return false;
                }

                return true;
            } else {

            }
        } else {

        }
        

        return false;
    }
    
    
    public static void handleBindMinecraft(WebSocket conn, JSONObject data) {
        try {

            

            String requestUsername = data.getString("request_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            String targetUsername = data.getString("username");
            String minecraftUsername = data.getString("minecraft_username");
            

            if (targetUsername == null || targetUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername");
                return;
            }
            
            if (minecraftUsername == null || minecraftUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劎inecraft_username");
                return;
            }
            

            if (!verifyUserPermission(targetUsername, requestUsername, userKey, superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐑樻殢闁瑰顭籵ken闁瑰瓨鐗炵粔瀵哥棯瑜嶉惁鎴︽煢?);
                return;
            }
            

            UserMinecraftBinding.BindingResult result = UserMinecraftBinding.bindMinecraftUser(targetUsername, minecraftUsername);
            
            JSONObject response = new JSONObject();
            response.put("action", "bind_minecraft");
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("username", targetUsername);
            response.put("minecraft_username", minecraftUsername);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "缂備焦鍨甸悾鐐緞鏉堫偉袝: " + e.getMessage());
        }
    }
    
    
    public static void handleUnbindMinecraft(WebSocket conn, JSONObject data) {
        try {

            

            String requestUsername = data.getString("request_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            String targetUsername = data.getString("username");
            
            if (targetUsername == null || targetUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername");
                return;
            }
            

            if (!verifyUserPermission(targetUsername, requestUsername, userKey, superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐑樻殢闁瑰顭籵ken闁瑰瓨鐗炵粔瀵哥棯瑜嶉惁鎴︽煢?);
                return;
            }
            
            UserMinecraftBinding.BindingResult result = UserMinecraftBinding.unbindMinecraftUser(targetUsername);
            
            JSONObject response = new JSONObject();
            response.put("action", "unbind_minecraft");
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("username", targetUsername);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "閻熸瑱绲跨划锔藉緞鏉堫偉袝: " + e.getMessage());
        }
    }
    
    
    public static void handleValidateMinecraft(WebSocket conn, JSONObject data) {
        try {

            

            String requestUsername = data.getString("request_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            String minecraftUsername = data.getString("minecraft_username");
            
            if (minecraftUsername == null || minecraftUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劎inecraft_username");
                return;
            }
            

            if (!verifyUserPermission(null, requestUsername, userKey, superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐑樻殢闁瑰顭籵ken闁瑰瓨鐗炵粔瀵哥棯瑜嶉惁鎴︽煢?);
                return;
            }
            
            boolean isValid = WhitelistManagerV2.validatePlayer(minecraftUsername);
            
            JSONObject response = new JSONObject();
            response.put("action", "validate_minecraft");
            response.put("success", true);
            response.put("is_valid", isValid);
            response.put("minecraft_username", minecraftUsername);
            response.put("message", isValid ? "闁活潿鍔嶉崺娑㈠触瀹ュ懎鍤掔紓浣瑰灥閻ｉ箖鏁嶇仦钘夊笒閻犱焦鐡曠换姗€宕? : "闁活潿鍔嶉崺娑㈠触瀹ュ棙寮撶紓浣瑰灥閻ｉ箖鏁嶇仦鎯х彆缂備焦绻嗙换姗€宕?);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝: " + e.getMessage());
        }
    }
    
    
    public static void handleQueryBinding(WebSocket conn, JSONObject data) {
        try {

            

            String requestUsername = data.getString("request_username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            String queryUsername = data.getString("username");
            String minecraftUsername = data.getString("minecraft_username");
            

            if ((queryUsername == null || queryUsername.trim().isEmpty()) && 
                (minecraftUsername == null || minecraftUsername.trim().isEmpty())) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欏哺濞撳墎鎲版稊绲猠rname闁瑰瓨娼焛necraft_username");
                return;
            }
            

            boolean hasValidAuth = false;
            String authMethod = "";
            

            if (superKey != null && !superKey.trim().isEmpty() && verifyAdminPermission(superKey)) {
                hasValidAuth = true;
                authMethod = "閻℃帒鎳愭鍥┾偓闈涙閹?;

            }

            else if (requestUsername != null && !requestUsername.trim().isEmpty() && 
                     userKey != null && !userKey.trim().isEmpty() &&
                     AuthValidator.hasUserPermission(requestUsername, userKey)) {
                hasValidAuth = true;
                authMethod = "闁活潿鍔嶉崺娉僶ken";

            }
            
            if (!hasValidAuth) {

                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担瑙勭畳闁轰礁鐗忓▓鎴︽偨閵婏箑鐓晅oken闁瑰瓨鐗炵粔瀵哥棯瑜嶉惁鎴︽煢?);
                return;
            }
            
            JSONObject response = new JSONObject();
            response.put("action", "query_binding");
            response.put("success", true);
            
            if (queryUsername != null && !queryUsername.trim().isEmpty()) {


                

                if (!authMethod.equals("閻℃帒鎳愭鍥┾偓闈涙閹?)) {

                    if (!queryUsername.equals(requestUsername)) {

                        sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁煎疇濮ら悡锛勬嫚閵忥絽娈扮€规瓕浜▓鎴犵磼閹存繄鏆板ǎ鍥ｅ墲娴煎懘鏁嶇仦鐣岀Ъ闁告挸绉堕弫銈夊箣? " + requestUsername);
                        return;
                    }
                }
                
                String boundMinecraftUsername = UserMinecraftBinding.getMinecraftUsernameByUser(queryUsername);
                response.put("username", queryUsername);
                response.put("minecraft_username", boundMinecraftUsername);
                response.put("is_bound", boundMinecraftUsername != null);
                
            } else if (minecraftUsername != null && !minecraftUsername.trim().isEmpty()) {


                
                String boundUsername = UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftUsername);
                

                if (!authMethod.equals("閻℃帒鎳愭鍥┾偓闈涙閹?)) {
                    if (boundUsername != null && !boundUsername.equals(requestUsername)) {

                        sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欎亢椤曟瓉C闁活潿鍔嶉崺娑㈠触瀹ュ懐娼ｅù婊冮閸欑偓绂掗弽顐ｆ殢闁规潙鍤栫槐婵娿亹閹惧啿顤呴柣顫妽閸? " + requestUsername);
                        return;
                    }
                }
                
                response.put("minecraft_username", minecraftUsername);
                response.put("username", boundUsername);
                response.put("is_bound", boundUsername != null);
            }
            

            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            e.printStackTrace();
            sendErrorResponse(conn, "闁哄被鍎撮妤佸緞鏉堫偉袝: " + e.getMessage());
        }
    }
    
    
    public static void handleListBindings(WebSocket conn, JSONObject data) {
        try {

            

            String superKey = data.getString("super_key");
            

            if (!verifyAdminPermission(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐣屝㈢紒鐙欏啰妲曢梺?);
                return;
            }
            
            List<Map<String, Object>> bindings = UserMinecraftBinding.getAllBindings();
            
            JSONObject response = new JSONObject();
            response.put("action", "list_bindings");
            response.put("success", true);
            response.put("message", "闁兼儳鍢茶ぐ鍥╃磼閹存繄鏆伴柛鎺擃殙閵嗗啴骞嬮幇顒€顫?);
            response.put("count", bindings.size());
            response.put("bindings", bindings);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁兼儳鍢茶ぐ鍥礆濡ゅ嫨鈧啯寰勬潏顐バ? " + e.getMessage());
        }
    }
    
    
    public static void handleSearchBindings(WebSocket conn, JSONObject data) {
        try {

            

            String superKey = data.getString("super_key");
            String keyword = data.getString("keyword");
            
            if (keyword == null || keyword.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劌eyword");
                return;
            }
            

            if (!verifyAdminPermission(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐣屝㈢紒鐙欏啰妲曢梺?);
                return;
            }
            
            List<Map<String, Object>> bindings = WhitelistManagerV2.searchBindings(keyword);
            
            JSONObject response = new JSONObject();
            response.put("action", "search_bindings");
            response.put("success", true);
            response.put("message", "闁瑰吋绮庨崒銊р偓鐟版湰閸?);
            response.put("keyword", keyword);
            response.put("count", bindings.size());
            response.put("bindings", bindings);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁瑰吋绮庨崒銊﹀緞鏉堫偉袝: " + e.getMessage());
        }
    }
    
    
    public static void handleGetStats(WebSocket conn, JSONObject data) {
        try {

            

            String superKey = data.getString("super_key");
            

            if (!verifyAdminPermission(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担鐣屝㈢紒鐙欏啰妲曢梺?);
                return;
            }
            
            Map<String, Object> stats = UserMinecraftBinding.getBindingStats();
            
            JSONObject response = new JSONObject();
            response.put("action", "get_stats");
            response.put("success", true);
            response.put("message", "闁兼儳鍢茶ぐ鍥╃磼閻旀椿鍚€濞ｅ洠鍓濇导鍛村箣閹邦剙顫?);
            response.put("stats", stats);
            
            conn.send(response.toJSONString());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁兼儳鍢茶ぐ鍥╃磼閻旀椿鍚€濠㈡儼绮剧憴? " + e.getMessage());
        }
    }
    
    
    private static void sendErrorResponse(WebSocket conn, String message) {
        try {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("message", message);
            conn.send(errorResponse.toJSONString());
        } catch (Exception e) {

        }
    }
} 
