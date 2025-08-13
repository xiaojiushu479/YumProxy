package yumProxy.net.websocket;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.java_websocket.WebSocket;
import yumProxy.utils.UserMinecraftBinding;
import yumProxy.net.httpAPI.AuthValidator;
import java.util.List;
import java.util.Map;


public class WhitelistWebSocketApi {
    
    private static void log(String msg) {

    }
    
    
    private static void sendErrorResponse(WebSocket conn, String message, String requestId) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("message", message);
        response.put("request_id", requestId);
        conn.send(response.toJSONString());
    }
    
    
    private static void sendErrorResponse(WebSocket conn, String message) {
        sendErrorResponse(conn, message, null);
    }
    
    
    private static void sendSuccessResponse(WebSocket conn, String action, JSONObject data, String requestId) {
        JSONObject response = new JSONObject();
        response.put("action", action);
        response.put("success", true);
        response.put("request_id", requestId);
        if (data != null) {
            response.putAll(data);
        }
        conn.send(response.toJSONString());
    }
    
    
    private static void sendSuccessResponse(WebSocket conn, String action, JSONObject data) {
        sendSuccessResponse(conn, action, data, null);
    }
    

    public static void handleQuery(WebSocket conn, JSONObject data) {
        handleQuery(conn, data, null);
    }
    
    public static void handleValidate(WebSocket conn, JSONObject data) {
        handleValidate(conn, data, null);
    }
    
    public static void handleAdd(WebSocket conn, JSONObject data) {
        handleAdd(conn, data, null);
    }
    
    public static void handleRemove(WebSocket conn, JSONObject data) {
        handleRemove(conn, data, null);
    }
    
    public static void handleList(WebSocket conn, JSONObject data) {
        handleList(conn, data, null);
    }
    
    public static void handleSearch(WebSocket conn, JSONObject data) {
        handleSearch(conn, data, null);
    }
    
    public static void handleStats(WebSocket conn, JSONObject data) {
        handleStats(conn, data, null);
    }
    
    
    public static void handleQuery(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String requestUsername = data.getString("request_username");
            

            if (username == null || username.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername", requestId);
                return;
            }
            
            if (userKey == null || userKey.trim().isEmpty() || 
                requestUsername == null || requestUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username", requestId);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁煎疇濮ら悡锛勬嫚閵忥絽娈扮€规瓕浜▓鎴﹀箣閹寸姵鐣卞☉鎾寸墱閺呯嵒D", requestId);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(username, userKey)) {
                sendErrorResponse(conn, "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝", requestId);
                return;
            }
            

            
            JSONObject result = new JSONObject();
            result.put("username", username);
            result.put("minecraft_id", status.getMinecraftUsername());
            result.put("is_bound", status.getMinecraftUsername() != null);
            result.put("is_valid", status.isValid());
            result.put("status_message", status.getMessage());
            

                JSONObject timestampInfo = new JSONObject();
                timestampInfo.put("is_active", status.getTimestampInfo().isActive);
                timestampInfo.put("is_expired", status.getTimestampInfo().isExpired);
                timestampInfo.put("activated_at", status.getTimestampInfo().activatedAt != null ? 
                    status.getTimestampInfo().activatedAt.toString() : null);
                timestampInfo.put("expires_at", status.getTimestampInfo().expiresAt != null ? 
                    status.getTimestampInfo().expiresAt.toString() : null);
                result.put("timestamp_info", timestampInfo);
            }
            

            String resultMessage;
            if (status.getMinecraftUsername() == null) {
                resultMessage = "闁活潿鍔嶉崺娑㈠嫉椤忓棛鎷ㄩ悗瑙勭閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐡";
            } else if (status.isValid()) {
                resultMessage = "闁哄被鍎撮妤呭箣閹邦剙顫犻柨娑樼灱濞呇囧触瀹ュ懎绀嬮柡鍫濐槹閺?;
            } else {
                resultMessage = "鐎规瓕灏欑划锔锯偓瑙勭閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐡濞达絽妫涙慨鎼佸箑娴ｈ锟ラ柡浣哥墳缁? + status.getMessage();
            }
            result.put("message", resultMessage);
            
            sendSuccessResponse(conn, "whitelist_query", result, requestId);

                ", 闁绘鍩栭埀? " + status.getMessage());
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    public static void handleValidate(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String minecraftId = data.getString("minecraft_id");
            

            if (minecraftId == null || minecraftId.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劎inecraft_id", requestId);
                return;
            }
            

            UserMinecraftBinding.WhitelistStatus status = UserMinecraftBinding.checkMinecraftUserWhitelistStatus(minecraftId);
            
            JSONObject result = new JSONObject();
            result.put("minecraft_id", minecraftId);
            result.put("exists", status.isValid());
            result.put("message", status.getMessage());
            result.put("detailed_status", getDetailedStatus(status));
            

                String systemUser = UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftId);
                if (systemUser != null) {
                    result.put("bound_user", systemUser);
                }
            }
            

                JSONObject timestampInfo = new JSONObject();
                timestampInfo.put("is_active", status.getTimestampInfo().isActive);
                timestampInfo.put("is_expired", status.getTimestampInfo().isExpired);
                timestampInfo.put("activated_at", status.getTimestampInfo().activatedAt != null ? 
                    status.getTimestampInfo().activatedAt.toString() : null);
                timestampInfo.put("expires_at", status.getTimestampInfo().expiresAt != null ? 
                    status.getTimestampInfo().expiresAt.toString() : null);
                result.put("timestamp_info", timestampInfo);
            }
            
            sendSuccessResponse(conn, "whitelist_validate", result, requestId);

            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    private static String getDetailedStatus(UserMinecraftBinding.WhitelistStatus status) {
        if (status.isValid()) {
            return "闁?闁谎嗘閹洟宕￠弴銏㈠矗閻犲洣绶氶埀顒佷亢缁诲啴鏁嶇仦鎯т憾闁告瑯鍨禍鎺戭潰閿濆懐鍩楅弶鈺傜☉閸欏棝寮靛鍛潳闁革絻鍔嶉悥鍫曞箣?;
        } else {
            String message = status.getMessage();
            if (message.contains("闁哄牜浜炵划锔锯偓?)) {
                return "妫ｅ啯鏅?婵縿鍊栭崹婊堟儍閸曨亞鐟柣锝呯灱閺併倝骞嬪畡鐗堝€抽柡鍫簻濠€顏堟儌閽樺鍊抽柛妤佹磻閼垫垿鏁嶅畝鍐惧殲闁稿繐鐗忕划锔锯偓瑙勪亢婢跺嫰骞?;
            } else if (message.contains("閺夆晛娲﹀﹢?)) {
                return "闁?闁诡喓鍔庡▓鎴﹀籍閸洘锛熼柟鏉戝暱閸戔剝娼婚崶銊﹀焸闁挎稑鐬煎▍褔宕ュ鍛鐎瑰憡褰冮妵鎴﹀极閸剛绀夐悹鍥棑閻㈣崵鎷归悷鐗堝€甸梺鎻掔Х閻?;
            } else if (message.contains("闁哄牜浜濈缓鍝劽?)) {
                return "闁?闁诡喓鍔忕换鏇㈠嫉椤忓洤鏋犲☉鏃傚濡炲倿姊绘潏鍓х閻犲洨鏌夐崰妯荤▕閻楀牊顦ч梻鍌氱摠閸╂垿宕ユ惔銊ユ閻?;
            } else {
                return "闁?闁谎嗘閹洟宕￠弴銏㈠矗閻犲洣绀侀妵鎴犳嫻閵夘垳绐? + message;
            }
        }
    }
    
    
    public static void handleAdd(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String username = data.getString("username");
            String minecraftId = data.getString("minecraft_id");
            String userKey = data.getString("user_key");
            String requestUsername = data.getString("request_username");
            

            if (username == null || username.trim().isEmpty() || 
                minecraftId == null || minecraftId.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername 闁?minecraft_id", requestId);
                return;
            }
            
            if (userKey == null || userKey.trim().isEmpty() || 
                requestUsername == null || requestUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username", requestId);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁艰櫕鍨濈拹鐔兼嚊椤忓嫮绠掓繛锝堫嚙婵偤骞嬮幋鐘崇暠濞戞挻鐗滈弲鐛籇", requestId);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(username, userKey)) {
                sendErrorResponse(conn, "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝", requestId);
                return;
            }
            

            UserMinecraftBinding.BindingResult bindingResult = UserMinecraftBinding.bindMinecraftUser(username, minecraftId);
            
            if (bindingResult.isSuccess()) {
                JSONObject result = new JSONObject();
                result.put("username", username);
                result.put("minecraft_id", minecraftId);
                result.put("message", bindingResult.getMessage());
                sendSuccessResponse(conn, "whitelist_add", result, requestId);

            } else {
                sendErrorResponse(conn, bindingResult.getMessage(), requestId);
            }
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    public static void handleRemove(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String requestUsername = data.getString("request_username");
            

            if (username == null || username.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername", requestId);
                return;
            }
            
            if (userKey == null || userKey.trim().isEmpty() || 
                requestUsername == null || requestUsername.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username", requestId);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁煎疇妫勯崹褰掓⒔閵堝牆娈扮€规瓕浜▓鎴﹀箣閹寸姵鐣卞☉鎾寸墱閺呯嵒D", requestId);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(username, userKey)) {
                sendErrorResponse(conn, "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝", requestId);
                return;
            }
            

            UserMinecraftBinding.BindingResult unbindResult = UserMinecraftBinding.unbindMinecraftUser(username);
            
            if (unbindResult.isSuccess()) {
                JSONObject result = new JSONObject();
                result.put("username", username);
                result.put("message", unbindResult.getMessage());
                sendSuccessResponse(conn, "whitelist_remove", result, requestId);

            } else {
                sendErrorResponse(conn, unbindResult.getMessage(), requestId);
            }
            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    public static void handleList(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String superKey = data.getString("super_key");
            if (!AuthValidator.isAdmin(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?, requestId);
                return;
            }
            
            List<Map<String, Object>> bindings = UserMinecraftBinding.getAllBindings();
            
            JSONObject result = new JSONObject();
            result.put("total", bindings.size());
            
            JSONArray bindingArray = new JSONArray();
            bindingArray.addAll(bindings);
            result.put("bindings", bindingArray);
            
            sendSuccessResponse(conn, "whitelist_list", result, requestId);

            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    public static void handleSearch(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String superKey = data.getString("super_key");
            if (!AuthValidator.isAdmin(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?, requestId);
                return;
            }
            
            String keyword = data.getString("keyword");
            if (keyword == null || keyword.trim().isEmpty()) {
                sendErrorResponse(conn, "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劌eyword", requestId);
                return;
            }
            
            List<Map<String, Object>> bindings = UserMinecraftBinding.searchBindings(keyword);
            
            JSONObject result = new JSONObject();
            result.put("keyword", keyword);
            result.put("total", bindings.size());
            
            JSONArray bindingArray = new JSONArray();
            bindingArray.addAll(bindings);
            result.put("bindings", bindingArray);
            
            sendSuccessResponse(conn, "whitelist_search", result, requestId);

            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
    
    
    public static void handleStats(WebSocket conn, JSONObject data, String requestId) {
        try {

            
            String superKey = data.getString("super_key");
            if (!AuthValidator.isAdmin(superKey)) {
                sendErrorResponse(conn, "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?, requestId);
                return;
            }
            
            Map<String, Object> stats = UserMinecraftBinding.getBindingStats();
            
            JSONObject result = new JSONObject();
            result.putAll(stats);
            
            sendSuccessResponse(conn, "whitelist_stats", result, requestId);

            
        } catch (Exception e) {

            sendErrorResponse(conn, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage(), requestId);
        }
    }
} 
 
