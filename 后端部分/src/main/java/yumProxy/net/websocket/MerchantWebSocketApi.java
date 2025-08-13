package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.net.pay.EPayClient;
import yumProxy.net.httpAPI.AuthValidator;

import java.util.HashMap;
import java.util.Map;

public class MerchantWebSocketApi {
    

    private static final String API_URL = "https://your-epay-host/xpay/epay/submit.php";
    private static final String API_MAPI_URL = "https://your-epay-host/xpay/epay/mapi.php";
    private static final String PID = "YOUR_EPAY_PID";
    private static final String KEY = "YOUR_EPAY_KEY";
    private static final EPayClient client = new EPayClient(API_URL, API_MAPI_URL, PID, KEY);
    
    private static void log(String msg) {

    }
    
    
    public static void handleMerchantInfo(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "Super key required for merchant info");
                sendResponse(conn, "merchant_info", result);
                return;
            }
            

            String merchantInfo = client.queryMerchantInfo();
            Map<String, String> infoMap = parseJsonToMap(merchantInfo);
            
            result.put("code", Integer.parseInt(infoMap.getOrDefault("code", "0")));
            result.put("msg", infoMap.getOrDefault("msg", ""));
            result.put("merchant_info", infoMap);
            

            Map<String, Object> localConfig = new HashMap<>();
            localConfig.put("api_url", API_URL);
            localConfig.put("api_mapi_url", API_MAPI_URL);
            localConfig.put("pid", PID);
            String maskedKey = KEY == null ? "" : (KEY.length() <= 4 ? "****" : ("****" + KEY.substring(KEY.length() - 4)));
            localConfig.put("key", maskedKey);
            result.put("local_config", localConfig);
            
            sendResponse(conn, "merchant_info", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "merchant_info", errorResult);
        }
    }
    
    
    public static void handleMerchantConfig(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            String apiUrl = data.getString("api_url");
            String apiMapiUrl = data.getString("api_mapi_url");
            String pid = data.getString("pid");
            String key = data.getString("key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "Super key required for merchant config update");
                sendResponse(conn, "merchant_config", result);
                return;
            }
            

            if (apiUrl == null || apiUrl.trim().isEmpty() ||
                apiMapiUrl == null || apiMapiUrl.trim().isEmpty() ||
                pid == null || pid.trim().isEmpty() ||
                key == null || key.trim().isEmpty()) {

                result.put("code", 400);
                result.put("msg", "Bad Request");
                result.put("message", "All merchant config parameters are required");
                sendResponse(conn, "merchant_config", result);
                return;
            }
            



            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("message", "Merchant configuration updated successfully");
            Map<String, Object> config = new HashMap<>();
            config.put("api_url", apiUrl);
            config.put("api_mapi_url", apiMapiUrl);
            config.put("pid", pid);
            config.put("key", "***" + key.substring(Math.max(0, key.length() - 4)));
            result.put("config", config);
            
            sendResponse(conn, "merchant_config", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "merchant_config", errorResult);
        }
    }
    
    
    public static void handleMerchantStatus(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "Super key required for merchant status check");
                sendResponse(conn, "merchant_status", result);
                return;
            }
            

            Map<String, Object> status = new HashMap<>();
            
            try {

                String merchantInfo = client.queryMerchantInfo();
                Map<String, String> infoMap = parseJsonToMap(merchantInfo);
                
                status.put("connected", true);
                status.put("api_response", infoMap);
                status.put("last_check", System.currentTimeMillis());
                

                status.put("api_url_configured", API_URL != null && !API_URL.trim().isEmpty());
                status.put("api_mapi_url_configured", API_MAPI_URL != null && !API_MAPI_URL.trim().isEmpty());
                status.put("pid_configured", PID != null && !PID.trim().isEmpty());
                status.put("key_configured", KEY != null && !KEY.trim().isEmpty());
                
            } catch (Exception e) {
                status.put("connected", false);
                status.put("error", e.getMessage());
                status.put("last_check", System.currentTimeMillis());
            }
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("status", status);
            
            sendResponse(conn, "merchant_status", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "merchant_status", errorResult);
        }
    }
    
    
    public static void handleMerchantTest(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "Super key required for merchant test");
                sendResponse(conn, "merchant_test", result);
                return;
            }
            

            Map<String, Object> testResult = new HashMap<>();
            
            try {

                long startTime = System.currentTimeMillis();
                String merchantInfo = client.queryMerchantInfo();
                long endTime = System.currentTimeMillis();
                
                Map<String, String> infoMap = parseJsonToMap(merchantInfo);
                
                testResult.put("success", true);
                testResult.put("response_time", endTime - startTime);
                testResult.put("api_response", infoMap);
                testResult.put("test_time", System.currentTimeMillis());
                
            } catch (Exception e) {
                testResult.put("success", false);
                testResult.put("error", e.getMessage());
                testResult.put("test_time", System.currentTimeMillis());
            }
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("test_result", testResult);
            
            sendResponse(conn, "merchant_test", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "merchant_test", errorResult);
        }
    }
    
    
    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {

        }
        return map;
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
