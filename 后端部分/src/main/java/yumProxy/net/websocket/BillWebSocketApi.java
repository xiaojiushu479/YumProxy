package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.net.httpAPI.AuthValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class BillWebSocketApi {
    
    private static void log(String msg) {

    }
    
    
    public static void handleBillQuery(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            String outTradeNo = data.getString("out_trade_no");
            String status = data.getString("status");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "bill_query", result);
                return;
            }
            

            List<Map<String, Object>> bills;
            if (username != null && !username.isEmpty() && !AuthValidator.isAdmin(superKey)) {


                bills = MySQLUtils.queryBillByUser(username, outTradeNo, status);
            } else {


                bills = MySQLUtils.queryBill(outTradeNo, status);
            }
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("bills", bills);
            result.put("count", bills.size());
            
            sendResponse(conn, "bill_query", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "bill_query", errorResult);
        }
    }
    
    
    public static void handleBillGet(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            String outTradeNo = data.getString("out_trade_no");
            String tradeNo = data.getString("trade_no");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "bill_get", result);
                return;
            }
            

            List<Map<String, Object>> bills;
            if (username != null && !username.isEmpty() && !AuthValidator.isAdmin(superKey)) {

                bills = MySQLUtils.queryBillByUser(username, outTradeNo, null);
            } else {

                bills = MySQLUtils.queryBill(outTradeNo, null);
            }
            
            if (bills.isEmpty()) {
                result.put("code", 404);
                result.put("msg", "Bill not found");
                result.put("bill", null);
            } else {
                result.put("code", 200);
                result.put("msg", "success");
                result.put("bill", bills.get(0));
            }
            
            sendResponse(conn, "bill_get", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "bill_get", errorResult);
        }
    }
    
    
    public static void handleBillList(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            String limitStr = data.getString("limit");
            String pageStr = data.getString("page");
            String status = data.getString("status");
            
            int limit = 20, page = 1;
            if (limitStr != null && !limitStr.trim().isEmpty()) {
                try { limit = Integer.parseInt(limitStr); } catch (Exception ignored) {}
            }
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (Exception ignored) {}
            }
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "bill_list", result);
                return;
            }
            

            StringBuilder sql = new StringBuilder("SELECT * FROM bill WHERE 1=1");
            List<Object> params = new ArrayList<>();
            
            if (username != null && !username.isEmpty() && !AuthValidator.isAdmin(superKey)) {

                sql.append(" AND (param LIKE ? OR out_trade_no LIKE ?)");
                params.add("%" + username + "%");
                params.add("%" + username + "%");
            }
            
            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            
            sql.append(" ORDER BY id DESC");
            

            Map<String, Object> paginationResult = MySQLUtils.executeQueryWithPagination(
                sql.toString(), page, limit, params.toArray()
            );
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("bills", paginationResult.get("data"));
            result.put("total", paginationResult.get("total"));
            result.put("page", page);
            result.put("limit", limit);
            result.put("total_pages", paginationResult.get("totalPages"));
            
            sendResponse(conn, "bill_list", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "bill_list", errorResult);
        }
    }
    
    
    public static void handleBillStats(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "bill_stats", result);
                return;
            }
            

            StringBuilder sql = new StringBuilder("SELECT status, COUNT(*) as count, SUM(CAST(money AS DECIMAL(10,2))) as total_amount FROM bill WHERE 1=1");
            List<Object> params = new ArrayList<>();
            
            if (username != null && !username.isEmpty() && !AuthValidator.isAdmin(superKey)) {

                sql.append(" AND (param LIKE ? OR out_trade_no LIKE ?)");
                params.add("%" + username + "%");
                params.add("%" + username + "%");
            }
            
            sql.append(" GROUP BY status");
            
            List<Map<String, Object>> stats = MySQLUtils.executeQuery(sql.toString(), params.toArray());
            

            long totalCount = 0;
            double totalAmount = 0.0;
            Map<String, Object> statusStats = new HashMap<>();
            
            for (Map<String, Object> stat : stats) {
                String status = (String) stat.get("status");
                Long count = (Long) stat.get("count");
                Double amount = (Double) stat.get("total_amount");
                
                if (count != null) totalCount += count;
                if (amount != null) totalAmount += amount;
                
                Map<String, Object> statusInfo = new HashMap<>();
                statusInfo.put("count", count);
                statusInfo.put("amount", amount);
                statusStats.put(status, statusInfo);
            }
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("total_count", totalCount);
            result.put("total_amount", totalAmount);
            result.put("status_stats", statusStats);
            
            sendResponse(conn, "bill_stats", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "bill_stats", errorResult);
        }
    }
    
    
    public static void handleBillDelete(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");
            String outTradeNo = data.getString("out_trade_no");
            String tradeNo = data.getString("trade_no");
            

            
            Map<String, Object> result = new HashMap<>();
            

            if (!AuthValidator.isAdmin(superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "Super key required for bill deletion");
                sendResponse(conn, "bill_delete", result);
                return;
            }
            

            StringBuilder sql = new StringBuilder("DELETE FROM bill WHERE 1=1");
            List<Object> params = new ArrayList<>();
            
            if (outTradeNo != null && !outTradeNo.isEmpty()) {
                sql.append(" AND out_trade_no = ?");
                params.add(outTradeNo);
            }
            
            if (tradeNo != null && !tradeNo.isEmpty()) {
                sql.append(" AND trade_no = ?");
                params.add(tradeNo);
            }
            

            if (params.isEmpty()) {
                result.put("code", 400);
                result.put("msg", "Bad Request");
                result.put("message", "out_trade_no or trade_no is required");
                sendResponse(conn, "bill_delete", result);
                return;
            }
            
            int affectedRows = MySQLUtils.executeUpdate(sql.toString(), params.toArray());
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("affected_rows", affectedRows);
            result.put("deleted", affectedRows > 0);
            
            sendResponse(conn, "bill_delete", result);
            
        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "bill_delete", errorResult);
        }
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
