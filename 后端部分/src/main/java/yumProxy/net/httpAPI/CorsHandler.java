package yumProxy.net.httpAPI;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;


public class CorsHandler {
    
    
    public static void handleCors(HttpExchange exchange) throws IOException {

        

        if (exchange.getResponseHeaders().getFirst("Access-Control-Allow-Origin") != null) {

            return;
        }
        

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, action, Accept, Origin, Cache-Control, X-File-Name, user, super_key, username, user_key");
        exchange.getResponseHeaders().set("Access-Control-Expose-Headers", "Content-Length, Content-Range");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "false");
        

        

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(200, -1);
            return;
        }
    }
    
    
    public static void handleRequest(HttpExchange exchange, RequestHandler handler) throws IOException {
        try {

            

            handleCors(exchange);
            


                return;
            }
            

            handler.handle(exchange);
            

            
        } catch (Exception e) {

            e.printStackTrace();
            

            byte[] response = errorResponse.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");

            exchange.sendResponseHeaders(500, response.length);
            exchange.getResponseBody().write(response);
        }
    }
    
    
    @FunctionalInterface
    public interface RequestHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
} 
