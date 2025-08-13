package yumProxy.net.httpAPI;

import com.sun.net.httpserver.HttpExchange;
import yumProxy.net.pay.EPayClient;
import yumProxy.net.mysql.MySQLUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PayApiServer {
    // 闂佹澘绉堕悿鍡涘矗閸屾稒娈堕柨娑樼墣椤曨剟寮界憴鍕ウ閻庡湱鍋ゅ顖炲箚閸涱厼鏋屽ǎ鍥跺枟閺佸ジ鏁?    private static final String API_URL = "https://888.seven-cloud.cn/xpay/epay/submit.php";
    private static final String API_MAPI_URL = "https://888.seven-cloud.cn/xpay/epay/mapi.php";
    private static final String PID = "10202";
    private static final String KEY = "zPXjSIVGeBJNFYoXzxWO";
    

    private static final String BASE_URL = "https://www.yumproxy.top";
    private static final String NOTIFY_URL = BASE_URL + "/api/pay/notify";
    private static final String RETURN_URL = BASE_URL + "/api/pay/return";
    
    private static final EPayClient client = new EPayClient(API_URL, API_MAPI_URL, PID, KEY);
    



    public static void handlePayCreate(HttpExchange exchange) throws IOException {

        
        String resp;
        int status = 200;
        try {
            Map<String, String> params = parseForm(exchange.getRequestBody());

            

            if (!params.containsKey("type") || !params.containsKey("out_trade_no") || !params.containsKey("name") || !params.containsKey("money")) {

                resp = "{\"code\":0,\"msg\":\"闁告瑥鍊归弳鐔虹磽閸濆嫨浜糪"}";
                status = 400;
            } else {

                if (!params.containsKey("notify_url") || params.get("notify_url").isEmpty()) {
                    params.put("notify_url", NOTIFY_URL);
                }
                if (!params.containsKey("return_url") || params.get("return_url").isEmpty()) {
                    params.put("return_url", RETURN_URL);
                }
                

                String result = client.createOrder(params);

                






                }
                
                resp = result;
                

                Map<String, String> bill = new HashMap<>(params);

                bill.put("trade_no", resultMap.getOrDefault("trade_no", ""));
                bill.put("status", resultMap.getOrDefault("code", "0"));
                bill.put("payurl", resultMap.getOrDefault("payurl", ""));
                bill.put("qrcode", resultMap.getOrDefault("qrcode", ""));
                bill.put("param", params.getOrDefault("param", ""));
                
                try {
                    MySQLUtils.insertBill(bill);

                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            resp = "{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}";
            status = 500;
        }
        

        sendJson(exchange, status, resp);
    }


    public static void handleBillQuery(HttpExchange exchange) throws IOException {
            try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                    return;
                }


            try (java.io.InputStreamReader reader = new java.io.InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                    reqData = new com.google.gson.Gson().fromJson(reader, reqData.getClass());
                } catch (Exception e) {

                }

                String username = reqData.getOrDefault("username", "");
                String userKey = reqData.getOrDefault("user_key", "");
                String superKey = reqData.getOrDefault("super_key", "");


                if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                    String errorResp = "{\"code\":403,\"msg\":\"Forbidden\",\"message\":\"User key or super key required\"}";
                sendJson(exchange, 403, errorResp);
                    return;
                }

            String query = exchange.getRequestURI().getQuery();
                String outTradeNo = null, status = null;
                if (query != null) {
                    for (String pair : query.split("&")) {
                        String[] kv = pair.split("=");
                        if (kv.length == 2) {
                            if (kv[0].equals("out_trade_no")) outTradeNo = kv[1];
                            if (kv[0].equals("status")) status = kv[1];
                        }
                    }
                }


                List<Map<String, Object>> bills;
                if (!username.isEmpty() && !AuthValidator.isAdmin(superKey)) {

                    bills = MySQLUtils.queryBillByUser(username, outTradeNo, status);
                } else {

                }

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < bills.size(); i++) {
                    sb.append(mapToJson(bills.get(i)));
                    if (i < bills.size() - 1) sb.append(",");
                }
                sb.append("]");
            sendJson(exchange, 200, sb.toString());
            } catch (Exception e) {

                String errorResp = "{\"code\":500,\"msg\":\"Internal Server Error\",\"message\":\"" + e.getMessage() + "\"}";
            sendJson(exchange, 500, errorResp);
            }
    }


        long startTime = System.currentTimeMillis();

        
        try {
        Map<String, String> params = parseForm(exchange.getRequestBody());

        

            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            
            if (outTradeNo == null || outTradeNo.isEmpty()) {

                sendText(exchange, 200, "fail");
                return;
            }
            

            synchronized (processedOrders) {
                if (processedOrders.containsKey(outTradeNo)) {

                    sendText(exchange, 200, "success");
                    return;
                }
            }
            

            boolean valid = client.verifyNotify(params);
            
            if (valid && "TRADE_SUCCESS".equals(tradeStatus)) {

                synchronized (processedOrders) {
                    processedOrders.put(outTradeNo, true);
                }
                
                String money = params.get("money");
                String param = params.get("param");
                

                

                String username = extractUsernameFromOrderImproved(outTradeNo, param);
                
                if (username != null && !username.isEmpty()) {

                    if (hours > 0) {

                        

                        if (activated) {

                            

                            

                            

                                try {
                                    sendWebSocketPaymentNotification(username, outTradeNo, params, hours);
                                } catch (Exception e) {

                                }
                            }).start();
                            
                        } else {

                        }
                    } else {

                    }
                } else {

                }
                
                sendText(exchange, 200, "success");
        } else {

                sendText(exchange, 200, "fail");
        }
        
        } catch (Exception e) {

            e.printStackTrace();
            sendText(exchange, 200, "fail");
        }
        
        long endTime = System.currentTimeMillis();

    }
    
    
    private static void sendWebSocketPaymentNotification(String username, String outTradeNo, Map<String, String> orderMap, int hours) {
        try {
            com.alibaba.fastjson.JSONObject notification = new com.alibaba.fastjson.JSONObject();
            notification.put("type", "payment_success");
            notification.put("out_trade_no", outTradeNo);
            notification.put("username", username);
            notification.put("hours", hours);
            notification.put("order_info", orderMap);
            notification.put("timestamp", System.currentTimeMillis());
            


            
        } catch (Exception e) {

        }
    }


    public static void handleMerchantInfo(HttpExchange exchange) throws IOException {
        String resp;
        int status = 200;
        try {
            resp = client.queryMerchantInfo();
        } catch (Exception e) {
            resp = "{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}";
            status = 500;
        }
        sendJson(exchange, status, resp);
    }


    public static void handleBillGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String outTradeNo = null, tradeNo = null;
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("out_trade_no")) outTradeNo = kv[1];
                    if (kv[0].equals("trade_no")) tradeNo = kv[1];
                }
            }
        }
        String resp;
        int status = 200;
        try {
            resp = client.queryOrder(outTradeNo, tradeNo);
        } catch (Exception e) {
            resp = "{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}";
            status = 500;
        }
        sendJson(exchange, status, resp);
    }


    public static void handleBillList(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int limit = 20, page = 1;
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("limit")) try { limit = Integer.parseInt(kv[1]); } catch (Exception ignored) {}
                    if (kv[0].equals("page")) try { page = Integer.parseInt(kv[1]); } catch (Exception ignored) {}
                }
            }
        }
        String resp;
        int status = 200;
        try {
            resp = client.queryOrders(limit, page);
        } catch (Exception e) {
            resp = "{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}";
            status = 500;
        }
        sendJson(exchange, status, resp);
    }



        String query = exchange.getRequestURI().getQuery();
        String redirectUrl = "https://888.seven-cloud.cn/xpay/epay/submit.php";
        if (query != null && !query.isEmpty()) {
            redirectUrl += "?" + query;
        }
        exchange.getResponseHeaders().add("Location", redirectUrl);
        exchange.sendResponseHeaders(302, -1);
    }


        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] bytes = buffer.toByteArray();
        String body = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], java.net.URLDecoder.decode(kv[1], "UTF-8"));
            }
        }
        return map;
    }


    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            for (String pair : json.split(",")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].replaceAll("[\"{} ]", "");
                    String v = kv[1].replaceAll("[\"{} ]", "");
                    map.put(k, v);
                }
            }
        }
        return map;
    }


    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue() == null ? "" : e.getValue().toString()).append("\"");
            if (i < map.size() - 1) sb.append(",");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
    
    
    private static String extractUsernameFromOrder(String outTradeNo, String param) {

        if (param != null && !param.isEmpty()) {
            return param;
        }
        

        if (outTradeNo != null && outTradeNo.contains("_")) {
            String[] parts = outTradeNo.split("_");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        

            List<Map<String, Object>> results = MySQLUtils.executeQuery(
                "SELECT param FROM bills WHERE out_trade_no = ?", outTradeNo);
            if (!results.isEmpty()) {
                Object paramObj = results.get(0).get("param");
                if (paramObj != null) {
                    return paramObj.toString();
                }
            }
        } catch (Exception e) {

        }
        
        return null;
    }
    


    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] resp = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
    
    
    private static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        byte[] resp = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
    
    
    private static Map<String, String> sanitizeParams(Map<String, String> params) {
        Map<String, String> sanitized = new HashMap<>(params);
        if (sanitized.containsKey("sign")) {
            sanitized.put("sign", "***");
        }
        return sanitized;
    }
    
    
    private static String extractUsernameFromOrderImproved(String outTradeNo, String param) {

            return param.trim();
        }
        

        if (outTradeNo != null) {

            if (outTradeNo.contains("@")) {
                return outTradeNo.split("@")[0];
            }

            if (outTradeNo.contains("_")) {
                String[] parts = outTradeNo.split("_");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    return parts[0];
                }
            }
        }
        

    }
    
    
    private static void updateOrderStatus(String outTradeNo, Map<String, String> paymentInfo) {
        try {
            String updateSql = "UPDATE bills SET status = ?, trade_no = ?, updated_at = NOW() WHERE out_trade_no = ?";
            MySQLUtils.executeUpdate(updateSql, "1", paymentInfo.get("trade_no"), outTradeNo);

        } catch (Exception e) {

        }
    }
    
    
    private static void recordUserActivation(String username, String outTradeNo, Map<String, String> paymentInfo, int hours) {
        try {
            String insertSql = "INSERT INTO user_payment_history (username, out_trade_no, trade_no, " +
                             "payment_amount, hours_added, payment_method, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            MySQLUtils.executeUpdate(insertSql, 
                username, 
                outTradeNo, 
                paymentInfo.get("trade_no"),
                paymentInfo.get("money"),
                hours,
                paymentInfo.get("type")
            );

        } catch (Exception e) {

        }
    }
    
    
    public static void handlePayReturn(HttpExchange exchange) throws IOException {

        
        try {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryString(query);
            
            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            

            

            boolean valid = client.verifyNotify(params);
            
            String redirectUrl;
            if (valid && "TRADE_SUCCESS".equals(tradeStatus)) {

                redirectUrl = BASE_URL + "/payment/success?order=" + outTradeNo;

            } else {


            }
            

            exchange.sendResponseHeaders(302, -1);
            
        } catch (Exception e) {


            exchange.getResponseHeaders().add("Location", errorUrl);
            exchange.sendResponseHeaders(302, -1);
        }
    }
    
    
    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(kv[1], "UTF-8");
                        params.put(key, value);
                    } catch (Exception e) {

                    }
                }
            }
        }
        return params;
    }
} 
