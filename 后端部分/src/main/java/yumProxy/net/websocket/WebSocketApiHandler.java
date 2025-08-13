package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketApiHandler {


    private static final Map<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    private static final long MIN_REQUEST_INTERVAL = 3000;
    private static void log(String msg) {

    }

    
    public static void handleApiRequest(WebSocket conn, String message) {
        String requestId = null;
        try {
            JSONObject jsonMessage = JSON.parseObject(message);
            String apiType = jsonMessage.getString("api_type");
            String action = jsonMessage.getString("action");
            JSONObject data = jsonMessage.getJSONObject("data");
            requestId = jsonMessage.getString("request_id");



            switch (apiType) {
                case "key":
                    handleKeyApi(conn, action, data, requestId);
                    break;
                case "user":
                    handleUserApi(conn, action, data, requestId);
                    break;
                case "email":
                    handleEmailApi(conn, action, data, requestId);
                    break;
                case "timestamp":
                    handleTimestampApi(conn, action, data, requestId);
                    break;
                case "pay":
                    handlePayApi(conn, action, data, requestId);
                    break;
                case "bill":
                    handleBillApi(conn, action, data, requestId);
                    break;
                case "merchant":
                    handleMerchantApi(conn, action, data, requestId);
                    break;
                case "whitelist":
                    handleWhitelistApi(conn, action, data, requestId);
                    break;
                case "binding":
                    handleBindingApi(conn, action, data, requestId);
                    break;
                case "user_ban":
                    handleUserBanApi(conn, action, data, requestId);
                    break;
                default:
                    sendErrorResponse(conn, "UNKNOWN_API_TYPE", "闁哄牜浜為悡锟犳儍閸戞┊I缂侇偉顕ч悗? " + apiType, requestId);
                    break;
            }

        } catch (Exception e) {

            sendErrorResponse(conn, "INVALID_REQUEST", "閻犲洭鏀遍惇浼村冀閻撳海纭€闂佹寧鐟ㄩ銈夊箣閺嵮屾П闁荤偛妫楅妵鎴犳嫻? " + e.getMessage(), requestId);
        }
    }

    
    private static void handleKeyApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "used":
                handleKeyUsed(conn, data, requestId);
                break;
            case "create":
                handleKeyCreate(conn, data, requestId);
                break;
            case "query":
                handleKeyQuery(conn, data, requestId);
                break;
            case "delete":
                handleKeyDelete(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨偆妲曢梺濮愬劜閹奸攱鎷? " + action, requestId);
                break;
        }
    }

    
    private static void handleUserApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "register":

                handleUserRegister(conn, data, requestId);
                break;
            case "login":

                handleUserLogin(conn, data, requestId);
                break;
            case "info":
                handleUserInfo(conn, data, requestId);
                break;
            case "delete":
                handleUserDelete(conn, data, requestId);
                break;
            case "list":
                handleUserList(conn, data, requestId);
                break;
            case "validate_token":
                handleUserValidateToken(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨厽鏆忛柟鎾敱閹奸攱鎷? " + action, requestId);
                break;
        }
    }

    
    private static void handleEmailApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "send":
                handleEmailSend(conn, data, requestId);
                break;
            case "verify":
                handleEmailVerify(conn, data, requestId);
                break;
            case "status":
                handleEmailStatus(conn, data, requestId);
                break;
            case "config":
                handleEmailConfig(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨垰浠忓ù鐘哄煐閹奸攱鎷? " + action, requestId);
                break;
        }
    }

    
    private static void handleTimestampApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "activate":
                handleTimestampActivate(conn, data, requestId);
                break;
            case "query":
                handleTimestampQuery(conn, data, requestId);
                break;
            case "extend":
                handleTimestampExtend(conn, data, requestId);
                break;
            case "deactivate":
                handleTimestampDeactivate(conn, data, requestId);
                break;
            case "delete":
                handleTimestampDelete(conn, data, requestId);
                break;
            case "check_active":
                handleTimestampCheckActive(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨剚顦ч梻鍌氱摠閸╂垿骞欏鍕▕: " + action, requestId);
                break;
        }
    }

    
    private static void handlePayApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "create":
                handlePayCreate(conn, data);
                break;
            case "api_create":
                handleApiPayCreate(conn, data);
                break;
            case "notify":
                handlePayNotify(conn, data);
                break;
            case "form":
                handlePayForm(conn, data);
                break;
            case "query":
                handleOrderQuery(conn, data);
                break;
            case "list":
                handleOrderList(conn, data);
                break;
            case "stop_tracking":
                handleStopTracking(conn, data);
                break;
            case "get_tracking_status":
                handleGetTrackingStatus(conn, data);
                break;
            default:
                sendErrorResponse(conn, "闁哄牜浜為悡锟犳儍閸曨剚鏆滃ù鐘成戦幖閿嬫媴? " + action);
                break;
        }
    }

    
    private static void handleBillApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "query":
                handleBillQuery(conn, data);
                break;
            case "get":
                handleBillGet(conn, data);
                break;
            case "list":
                handleBillList(conn, data);
                break;
            case "stats":
                handleBillStats(conn, data);
                break;
            case "delete":
                handleBillDelete(conn, data);
                break;
            default:
                sendErrorResponse(conn, "闁哄牜浜為悡锟犳儍閸曨喖顦╅柛妤佹礃閹奸攱鎷? " + action);
                break;
        }
    }

    
    private static void handleMerchantApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "info":
                handleMerchantInfo(conn, data);
                break;
            case "config":
                handleMerchantConfig(conn, data);
                break;
            case "status":
                handleMerchantStatus(conn, data);
                break;
            case "test":
                handleMerchantTest(conn, data);
                break;
            default:
                sendErrorResponse(conn, "闁哄牜浜為悡锟犳儍閸曨偅娅岄柟鎾敱閹奸攱鎷? " + action);
                break;
        }
    }

    
    private static void handleWhitelistApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "query":
                handleWhitelistQuery(conn, data, requestId);
                break;
            case "validate":
                handleWhitelistValidate(conn, data, requestId);
                break;
            case "add":
                handleWhitelistAdd(conn, data, requestId);
                break;
            case "remove":
                handleWhitelistRemove(conn, data, requestId);
                break;
            case "list":
                handleWhitelistList(conn, data, requestId);
                break;
            case "search":
                handleWhitelistSearch(conn, data, requestId);
                break;
            case "stats":
                handleWhitelistStats(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨厽顏ら柛姘Т瀹曠喖骞欏鍕▕: " + action, requestId);
                break;
        }
    }

    
    private static void handleBindingApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "bind_minecraft":
                handleBindMinecraft(conn, data);
                break;
            case "unbind_minecraft":
                handleUnbindMinecraft(conn, data);
                break;
            case "validate_minecraft":
                handleValidateMinecraft(conn, data);
                break;
            case "query_binding":
                handleQueryBinding(conn, data);
                break;
            case "list_bindings":
                handleListBindings(conn, data);
                break;
            case "search_bindings":
                handleSearchBindings(conn, data);
                break;
            case "get_stats":
                handleGetStats(conn, data);
                break;
            default:
                sendErrorResponse(conn, "闁哄牜浜為悡锟犳儍閸曨厾鎷ㄩ悗瑙勭閹奸攱鎷? " + action);
                break;
        }
    }

    
    private static void handleUserBanApi(WebSocket conn, String action, JSONObject data, String requestId) {
        switch (action) {
            case "ban_user":
                handleBanUser(conn, data, requestId);
                break;
            case "unban_user":
                handleUnbanUser(conn, data, requestId);
                break;
            case "check_ban_status":
                handleCheckBanStatus(conn, data, requestId);
                break;
            case "list_banned_users":
                handleListBannedUsers(conn, data, requestId);
                break;
            case "get_ban_logs":
                handleGetBanLogs(conn, data, requestId);
                break;
            case "clean_expired_bans":
                handleCleanExpiredBans(conn, data, requestId);
                break;
            default:
                sendErrorResponse(conn, "UNKNOWN_ACTION", "闁哄牜浜為悡锟犳儍閸曨偆娈辩紒鍌欑劍閹奸攱鎷? " + action, requestId);
                break;
        }
    }



    private static void handleWhitelistQuery(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleQuery(conn, data, requestId);
    }

    private static void handleWhitelistValidate(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleValidate(conn, data, requestId);
    }

    private static void handleWhitelistAdd(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleAdd(conn, data, requestId);
    }

    private static void handleWhitelistRemove(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleRemove(conn, data, requestId);
    }

    private static void handleWhitelistList(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleList(conn, data, requestId);
    }

    private static void handleWhitelistSearch(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleSearch(conn, data, requestId);
    }

    private static void handleWhitelistStats(WebSocket conn, JSONObject data, String requestId) {
        WhitelistWebSocketApi.handleStats(conn, data, requestId);
    }



    private static void handleKeyUsed(WebSocket conn, JSONObject data, String requestId) {
        KeyWebSocketApi.handleKeyUsed(conn, data);
    }

    private static void handleKeyCreate(WebSocket conn, JSONObject data, String requestId) {
        KeyWebSocketApi.handleKeyCreate(conn, data);
    }

    private static void handleKeyQuery(WebSocket conn, JSONObject data, String requestId) {
        KeyWebSocketApi.handleKeyQuery(conn, data);
    }

    private static void handleKeyDelete(WebSocket conn, JSONObject data, String requestId) {
        KeyWebSocketApi.handleKeyDelete(conn, data);
    }



    private static void handleUserRegister(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserRegister(conn, data);
    }

    private static void handleUserLogin(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserLogin(conn, data);
    }

    private static void handleUserInfo(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserInfo(conn, data);
    }

    private static void handleUserDelete(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserDelete(conn, data);
    }

    private static void handleUserList(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserList(conn, data);
    }

    private static void handleUserValidateToken(WebSocket conn, JSONObject data, String requestId) {
        UserWebSocketApi.handleUserValidateToken(conn, data, requestId);
    }



    private static void handleEmailSend(WebSocket conn, JSONObject data, String requestId) {
        EmailWebSocketApi.handleEmailSend(conn, data);
    }

    private static void handleEmailVerify(WebSocket conn, JSONObject data, String requestId) {
        EmailWebSocketApi.handleEmailVerify(conn, data);
    }

    private static void handleEmailStatus(WebSocket conn, JSONObject data, String requestId) {
        EmailWebSocketApi.handleEmailStatus(conn, data);
    }

    private static void handleEmailConfig(WebSocket conn, JSONObject data, String requestId) {
        EmailWebSocketApi.handleEmailConfig(conn, data);
    }



    private static void handleTimestampActivate(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampActivate(conn, data, requestId);
    }

    private static void handleTimestampQuery(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampQuery(conn, data, requestId);
    }

    private static void handleTimestampExtend(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampExtend(conn, data, requestId);
    }

    private static void handleTimestampDeactivate(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampDeactivate(conn, data, requestId);
    }

    private static void handleTimestampDelete(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampDelete(conn, data, requestId);
    }

    private static void handleTimestampCheckActive(WebSocket conn, JSONObject data, String requestId) {
        TimestampWebSocketApi.handleTimestampCheckActive(conn, data, requestId);
    }



    private static void handlePayCreate(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handlePayCreate(conn, data);
    }

    private static void handleApiPayCreate(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handleApiPayCreate(conn, data);
    }

    private static void handlePayNotify(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handlePayNotify(conn, data);
    }

    private static void handlePayForm(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handlePayForm(conn, data);
    }

    private static void handleOrderQuery(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handleOrderQuery(conn, data);
    }

    private static void handleOrderList(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handleOrderList(conn, data);
    }

    private static void handleStopTracking(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handleStopTracking(conn, data);
    }

    private static void handleGetTrackingStatus(WebSocket conn, JSONObject data) {
        PayWebSocketApi.handleGetTrackingStatus(conn, data);
    }



    private static void handleBillQuery(WebSocket conn, JSONObject data) {
        BillWebSocketApi.handleBillQuery(conn, data);
    }

    private static void handleBillGet(WebSocket conn, JSONObject data) {
        BillWebSocketApi.handleBillGet(conn, data);
    }

    private static void handleBillList(WebSocket conn, JSONObject data) {
        BillWebSocketApi.handleBillList(conn, data);
    }

    private static void handleBillStats(WebSocket conn, JSONObject data) {
        BillWebSocketApi.handleBillStats(conn, data);
    }

    private static void handleBillDelete(WebSocket conn, JSONObject data) {
        BillWebSocketApi.handleBillDelete(conn, data);
    }



    private static void handleMerchantInfo(WebSocket conn, JSONObject data) {
        MerchantWebSocketApi.handleMerchantInfo(conn, data);
    }

    private static void handleMerchantConfig(WebSocket conn, JSONObject data) {
        MerchantWebSocketApi.handleMerchantConfig(conn, data);
    }

    private static void handleMerchantStatus(WebSocket conn, JSONObject data) {
        MerchantWebSocketApi.handleMerchantStatus(conn, data);
    }

    private static void handleMerchantTest(WebSocket conn, JSONObject data) {
        MerchantWebSocketApi.handleMerchantTest(conn, data);
    }



    private static void handleBindMinecraft(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleBindMinecraft(conn, data);
    }

    private static void handleUnbindMinecraft(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleUnbindMinecraft(conn, data);
    }

    private static void handleValidateMinecraft(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleValidateMinecraft(conn, data);
    }

    private static void handleQueryBinding(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleQueryBinding(conn, data);
    }

    private static void handleListBindings(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleListBindings(conn, data);
    }

    private static void handleSearchBindings(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleSearchBindings(conn, data);
    }

    private static void handleGetStats(WebSocket conn, JSONObject data) {
        UserBindingWebSocketApi.handleGetStats(conn, data);
    }



    private static void handleBanUser(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleBanUser(conn, data, requestId);
    }

    private static void handleUnbanUser(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleUnbanUser(conn, data, requestId);
    }

    private static void handleCheckBanStatus(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleCheckBanStatus(conn, data, requestId);
    }

    private static void handleListBannedUsers(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleListBannedUsers(conn, data, requestId);
    }

    private static void handleGetBanLogs(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleGetBanLogs(conn, data, requestId);
    }

    private static void handleCleanExpiredBans(WebSocket conn, JSONObject data, String requestId) {
        UserBanWebSocketApi.handleCleanExpiredBans(conn, data, requestId);
    }



    
    private static void sendSuccessResponse(WebSocket conn, String action, String message) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "success");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }

    
    private static void sendSuccessResponse(WebSocket conn, String action, Map<String, Object> data) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "success");
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }

    
    private static void sendErrorResponse(WebSocket conn, String message) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("status", "error");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }

    
    private static void sendErrorResponse(WebSocket conn, int code, String message) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("status", "error");
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }

    
    private static void sendErrorResponse(WebSocket conn, String errorCode, String message, String requestId) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message);
        response.put("error_code", errorCode);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }



    
    private static boolean checkRateLimit(String clientIp) {
        long now = System.currentTimeMillis();
        Long lastTime = lastRequestTime.get(clientIp);

        if (lastTime != null && (now - lastTime) < MIN_REQUEST_INTERVAL) {
            return false;

        lastRequestTime.put(clientIp, now);
        return true;
    }

    
    private static String getClientIpFromWebSocket(WebSocket conn) {
        try {
            java.net.InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
            if (remoteAddress != null) {
                return remoteAddress.getAddress().getHostAddress();
            }
        } catch (Exception e) {

        }
        return "unknown";
    }

    

    
}
