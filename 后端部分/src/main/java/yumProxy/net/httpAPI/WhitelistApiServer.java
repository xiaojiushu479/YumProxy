package yumProxy.net.httpAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import yumProxy.utils.UserMinecraftBinding;
import yumProxy.net.httpAPI.AuthValidator;
import yumProxy.server.timestamp.TimestampInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;


public class WhitelistApiServer {
    private static final Gson gson = new Gson();
    
    private static void log(String msg) {

    }
    
    
    private static void sendJson(HttpExchange exchange, Map<String, Object> result, int statusCode) throws IOException {
        String response = gson.toJson(result);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "application/json; charset=UTF-8");
        
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    
    private static Map<String, Object> timestampInfoToMap(TimestampInfo timestampInfo) {
        if (timestampInfo == null) {
            return null;
        }
        
        Map<String, Object> timestampMap = new HashMap<>();
        timestampMap.put("is_active", timestampInfo.isActive);
        timestampMap.put("is_expired", timestampInfo.isExpired);
        timestampMap.put("activated_at", timestampInfo.activatedAt != null ? timestampInfo.activatedAt.toString() : null);
        timestampMap.put("expires_at", timestampInfo.expiresAt != null ? timestampInfo.expiresAt.toString() : null);
        return timestampMap;
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
    
    
    public static void handleQueryMinecraftByUsername(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String username = (String) reqData.getOrDefault("username", "");
            String userKey = (String) reqData.getOrDefault("user_key", "");
            String requestUsername = (String) reqData.getOrDefault("request_username", "");
            

            

            if (username.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (userKey.isEmpty() || requestUsername.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁煎疇濮ら悡锛勬嫚閵忥絽娈扮€规瓕浜▓鎴﹀箣閹寸姵鐣卞☉鎾寸墱閺呯嵒D");
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(requestUsername, userKey)) {
                result.put("success", false);
                result.put("message", "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝");
                statusCode = 401;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            
            result.put("success", true);
            result.put("username", username);
            result.put("minecraft_id", status.getMinecraftUsername());
            result.put("is_bound", status.getMinecraftUsername() != null);
            result.put("is_valid", status.isValid());
            result.put("status_message", status.getMessage());
            

                result.put("timestamp_info", timestampInfoToMap(status.getTimestampInfo()));
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
            

                ", 闁绘鍩栭埀? " + status.getMessage());
            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleValidate(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String minecraftId = (String) reqData.getOrDefault("minecraft_id", "");
            

            

            if (minecraftId.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劎inecraft_id");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            UserMinecraftBinding.WhitelistStatus status = UserMinecraftBinding.checkMinecraftUserWhitelistStatus(minecraftId);
            
            result.put("success", true);
            result.put("minecraft_id", minecraftId);
            result.put("exists", status.isValid());
            result.put("message", status.getMessage());
            result.put("detailed_status", getDetailedStatus(status));
            

                String systemUser = UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftId);
                if (systemUser != null) {
                    result.put("bound_user", systemUser);
                }
            }
            

                result.put("timestamp_info", timestampInfoToMap(status.getTimestampInfo()));
            }
            

            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleAdd(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String username = (String) reqData.getOrDefault("username", "");
            String minecraftId = (String) reqData.getOrDefault("minecraft_id", "");
            String userKey = (String) reqData.getOrDefault("user_key", "");
            String requestUsername = (String) reqData.getOrDefault("request_username", "");
            

            

            if (username.isEmpty() || minecraftId.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername 闁?minecraft_id");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (userKey.isEmpty() || requestUsername.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁艰櫕鍨濈拹鐔兼嚊椤忓嫮绠掓繛锝堫嚙婵偤骞嬮幋鐘崇暠濞戞挻鐗滈弲鐛籇");
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(requestUsername, userKey)) {
                result.put("success", false);
                result.put("message", "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝");
                statusCode = 401;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            
            if (bindingResult.isSuccess()) {
                result.put("success", true);
                result.put("username", username);
                result.put("minecraft_id", minecraftId);
                result.put("message", "闁瑰瓨鍨瑰▓鎴炵▔閺嶎偅娅旾D婵烇綀顕ф慨鐐哄箣閹邦剙顫?);

            } else {
                result.put("success", false);
                result.put("message", bindingResult.getMessage());
                statusCode = 400;

            }
            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleRemove(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String username = (String) reqData.getOrDefault("username", "");
            String userKey = (String) reqData.getOrDefault("user_key", "");
            String requestUsername = (String) reqData.getOrDefault("request_username", "");
            

            

            if (username.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘sername");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (userKey.isEmpty() || requestUsername.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劘ser_key 闁?request_username");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            if (!username.equals(requestUsername)) {
                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欐皑閺併倝骞嬪畡鏉挎锭闁煎疇妫勯崹褰掓⒔閵堝牆娈扮€规瓕浜▓鎴﹀箣閹寸姵鐣卞☉鎾寸墱閺呯嵒D");
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (!AuthValidator.hasUserPermission(requestUsername, userKey)) {
                result.put("success", false);
                result.put("message", "闁活潿鍔嶉崺娉僶ken濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝");
                statusCode = 401;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            String currentMinecraftId = UserMinecraftBinding.getMinecraftUsernameByUser(username);
            

            UserMinecraftBinding.BindingResult unbindResult = UserMinecraftBinding.unbindMinecraftUser(username);
            
            if (unbindResult.isSuccess()) {
                result.put("success", true);
                result.put("username", username);
                result.put("removed_minecraft_id", currentMinecraftId);
                result.put("message", unbindResult.getMessage());

            } else {
                result.put("success", false);
                result.put("message", unbindResult.getMessage());
                statusCode = 400;

            }
            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleList(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String superKey = (String) reqData.getOrDefault("super_key", "");
            

            

                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?);
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            
            result.put("success", true);
            result.put("bindings", bindings);
            result.put("total", bindings.size());
            result.put("message", "闁兼儳鍢茶ぐ鍥儌閽樺鍊抽柛妤佹礀閸亞鎮伴妸锕€鐏囬柛?);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleSearch(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String superKey = (String) reqData.getOrDefault("super_key", "");
            String keyword = (String) reqData.getOrDefault("keyword", "");
            

            

                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?);
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            
            if (keyword.isEmpty()) {
                result.put("success", false);
                result.put("message", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜奸柨娑欘劌eyword");
                statusCode = 400;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            List<Map<String, Object>> searchResults = UserMinecraftBinding.searchBindings(keyword);
                
            result.put("success", true);
            result.put("results", searchResults);
            result.put("keyword", keyword);
            result.put("total", searchResults.size());
            result.put("message", "闁瑰吋绮庨崒銊р偓鐟版湰閸?);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleStats(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int statusCode = 200;
        
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            

            Map<String, Object> reqData = new HashMap<>();
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                reqData = gson.fromJson(reader, type);
            } catch (Exception e) {

            }
            
            String superKey = (String) reqData.getOrDefault("super_key", "");
            

            

                result.put("success", false);
                result.put("message", "闁哄鍟村鐑樼▔瀹ュ牆鍠曢柨娑欏哺濞撳墎鎲版担渚悁闁荤偛妫楅幉鎶藉级閸愵喗顎?);
                statusCode = 403;
                sendJson(exchange, result, statusCode);
                return;
            }
            

            Map<String, Object> stats = UserMinecraftBinding.getBindingStats();
            
            result.put("success", true);
            result.put("stats", stats);
            result.put("message", "闁兼儳鍢茶ぐ鍥╃磼閻旀椿鍚€濞ｅ洠鍓濇导鍛村箣閹邦剙顫?);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            statusCode = 500;
        }
        
        sendJson(exchange, result, statusCode);
    }
    
    
    public static void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("service", "WhitelistApiServer");
            result.put("status", "healthy");
            result.put("timestamp", System.currentTimeMillis());
            result.put("message", "闁谎嗘閹洟宕￠弮鐮匢闁哄牆绉存慨鐔告交閹邦垼鏀芥慨婵撶到閻?);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "闁哄牆绉存慨鐔奉嚕閸屾氨鍩? " + e.getMessage());
        }
        
        sendJson(exchange, result, 200);
    }
} 
