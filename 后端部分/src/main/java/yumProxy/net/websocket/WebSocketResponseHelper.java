package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;


public class WebSocketResponseHelper {
    
    
    public static void sendSuccessResponse(WebSocket conn, String action, Map<String, Object> data, String requestId) {
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "闁瑰灝绉崇紞鏃堝箣閹邦剙顫?);
        response.put("data", data);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    
    
    public static void sendErrorResponse(WebSocket conn, String errorCode, String message, String requestId) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message);
        response.put("error_code", errorCode);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    
    
    public static void sendLegacyResponse(WebSocket conn, String action, Map<String, Object> data, String requestId) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "success");
        response.put("data", data);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    
    
    public static void sendLegacyErrorResponse(WebSocket conn, String action, String message, String requestId) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "error");
        response.put("message", message);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
} 
