package mail.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import mail.smtp.SMTPManager;
import mail.Mail;

public class EmailHttpServer {
    
    private static final int PORT = 5000;
    private static final int THREAD_POOL_SIZE = 20;
    private HttpServer server;
    private SMTPManager smtpManager;
    private ExecutorService executorService;
    
    public EmailHttpServer() {

        this.smtpManager = new SMTPManager();
        initializeServer();
    }
    
    private void initializeServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/send-email", new SendEmailHandler());
            server.createContext("/health", new HealthHandler());
            server.createContext("/status", new StatusHandler());
            

            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            server.setExecutor(executorService);
            


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    public void start() {
        server.start();




    }
    
    public void stop() {

        

        server.stop(0);
        

        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {

                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {

                executorService.shutdownNow();
            }
        }
        

        if (smtpManager != null) {
            smtpManager.shutdown();
        }
        

    }
    

    class SendEmailHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            

            

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(method)) {

                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(method)) {

                sendResponse(exchange, 405, "闁告瑯浜濋弫顕€骞愭稉鐮凷T闁哄倽顫夌涵?);
                return;
            }
            
            try {

                

                Map<String, String> headers = getHeaders(exchange);

                

                String smtpHost = headers.get("X-SMTP-Host");
                String smtpPortStr = headers.get("X-SMTP-Port");
                String smtpUser = headers.get("X-SMTP-User");
                String smtpPass = headers.get("X-SMTP-Pass");
                String smtpFromName = headers.get("X-SMTP-From-Name");
                

                        ", 缂佹棏鍨拌ぐ? " + (smtpPortStr != null ? smtpPortStr : "濮掓稒顭堥?) + 
                        ", 闁活潿鍔嶉崺? " + (smtpUser != null ? "鐎圭寮惰ぐ浣圭瑹? : "闁哄牜浜濊ぐ浣圭瑹?));
                

                String requestBody = getRequestBody(exchange);

                

                if (requestBody == null || requestBody.trim().isEmpty()) {

                    sendResponse(exchange, 400, "閻犲洭鏀遍惇鐗堟媴閹捐尪绀嬬紒宀€灏ㄧ槐婵堟嫚闁垮绲瑰〒姘洴閸嬫牗绂掔捄鍝勬闁?);
                    return;
                }
                
                Map<String, String> emailData = parseEmailData(requestBody);

                
                String from = emailData.get("from");
                String to = emailData.get("to");
                String subject = emailData.get("subject");
                String content = emailData.get("content");
                String contentType = emailData.get("contentType");
                

                

                if (from == null || to == null || subject == null || content == null) {

                    sendResponse(exchange, 400, "缂傚倸鎼惃顖濈疀閸涢偊娲ｉ柛娆忓€归弳? from, to, subject, content");
                    return;
                }
                

                boolean success;
                if (smtpHost != null && smtpPortStr != null) {


                    try {
                        int smtpPort = Integer.parseInt(smtpPortStr);

                        
                        if ("html".equalsIgnoreCase(contentType)) {

                            success = smtpManager.sendEmailWithCustomSMTPHTMLAsync(from, to, subject, content, 
                                    smtpHost, smtpPort, smtpUser, smtpPass, smtpFromName).get();
                        } else {

                            success = smtpManager.sendEmailWithCustomSMTPAsync(from, to, subject, content, 
                                    smtpHost, smtpPort, smtpUser, smtpPass, smtpFromName).get();
                        }
                    } catch (NumberFormatException e) {

                        sendResponse(exchange, 400, "SMTP缂佹棏鍨拌ぐ娑㈠冀閻撳海纭€闂佹寧鐟ㄩ?);
                        return;
                    } catch (Exception e) {

                        success = false;
                    }
                } else {


                    try {
                        if ("html".equalsIgnoreCase(contentType)) {

                            success = smtpManager.sendEmailWithHTMLAsync(from, to, subject, content).get();
                        } else {

                            success = smtpManager.sendEmailAsync(from, to, subject, content).get();
                        }
                    } catch (Exception e) {

                        success = false;
                    }
                }
                
                if (success) {

                    sendResponse(exchange, 200, "闂侇収鍠曞▎銏ゅ矗閹达腹鍋撴担鐟扮亣闁?);
                } else {

                    sendResponse(exchange, 500, "闂侇収鍠曞▎銏ゅ矗閹达腹鍋撴担鎼炰杭閻?);
                }
                
            } catch (Exception e) {

                e.printStackTrace();
                sendResponse(exchange, 500, "闁哄牆绉存慨鐔煎闯閵娿儱鏁堕梺顔哄姂閺佸﹦鎷? " + e.getMessage());
            }
        }
    }
    

    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            

            
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            sendResponse(exchange, 200, "闁哄牆绉存慨鐔奉潰閿濆懐鍩楅弶鈺傚姌椤?);
            

        }
    }
    

    class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            

            
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            
            String smtpStatus = smtpManager.getThreadPoolStatus();

            
            String response = String.format("{\"status\": 200, \"message\": \"闁哄牆绉存慨鐔兼偐閼哥鍋撴稊?, \"smtp_thread_pool\": \"%s\"}", smtpStatus);
            
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
            

        }
    }
    
    private Map<String, String> getHeaders(HttpExchange exchange) {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, java.util.List<String>> entry : exchange.getRequestHeaders().entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return headers;
    }
    
    private String getRequestBody(HttpExchange exchange) throws IOException {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    private Map<String, String> parseEmailData(String requestBody) {
        Map<String, String> emailData = new HashMap<>();
        


        

        if (requestBody.trim().startsWith("{")) {

            try {

                String jsonStr = requestBody.trim();

                jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
                
                String[] pairs = jsonStr.split(",");
                for (String pair : pairs) {
                    pair = pair.trim();
                    if (pair.contains(":")) {
                        int colonIndex = pair.indexOf(":");
                        String key = pair.substring(0, colonIndex).trim();
                        String value = pair.substring(colonIndex + 1).trim();
                        

                        if (key.startsWith("\"") && key.endsWith("\"")) {
                            key = key.substring(1, key.length() - 1);
                        }
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        emailData.put(key, value);

                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {


            try {
                String[] pairs = requestBody.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        emailData.put(key, value);

                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        
        return emailData;
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();

        
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        String jsonResponse = "{\"status\": " + statusCode + ", \"message\": \"" + response + "\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        

    }
} 
