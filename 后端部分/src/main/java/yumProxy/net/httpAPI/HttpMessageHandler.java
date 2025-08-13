package yumProxy.net.httpAPI;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessageHandler {
    
    private static void log(String msg) {

    }
    
    
    public static boolean isMessageEnabled(HttpExchange exchange) {

        if (!yumProxy.net.Config.ServiceConfig.isHttpMessageEnabled()) {

            

            Map<String, Object> result = new HashMap<>();
            result.put("code", 503);
            result.put("data", "SERVICE_UNAVAILABLE");
            result.put("message", "HTTP婵炴垵鐗婃导鍛緞閸曨厽鍊炵€规瓕灏欓々锕傛偨?);
            result.put("timestamp", System.currentTimeMillis());
            result.put("Time", "");
            
            try {
                sendJson(exchange, result);
            } catch (IOException e) {

            }
            
            return false;
        }
        
        return true;
    }
    
    
    private static void sendJson(HttpExchange exchange, Map<String, Object> result) throws IOException {
        String json = new com.google.gson.Gson().toJson(result);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }
} 
