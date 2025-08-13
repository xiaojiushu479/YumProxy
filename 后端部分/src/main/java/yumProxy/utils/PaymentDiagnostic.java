package yumProxy.utils;

import yumProxy.net.mysql.MySQLUtils;
import yumProxy.net.pay.EPayClient;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.user.UserManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class PaymentDiagnostic {

    public static void main(String[] args) {

        
        Scanner scanner = new Scanner(System.in);
        
        try {

            String outTradeNo = scanner.nextLine().trim();
            
            if (outTradeNo.isEmpty()) {

                return;
            }
            


            

            checkOrderExists(outTradeNo);
            

            checkUsernameExtraction(outTradeNo);
            

            checkAmountCalculation(outTradeNo);
            

            checkUserExists(outTradeNo);
            

            checkTimestampActivation(outTradeNo);
            

            provideSolution(outTradeNo);
            
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    
    private static void checkOrderExists(String outTradeNo) {


        
        try {
            String sql = "SELECT out_trade_no, trade_no, name, money, type, status, param, created_at FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {


                return;
            }
            
            Map<String, Object> order = results.get(0);









            
        } catch (Exception e) {

        }
    }
    
    
    private static void checkUsernameExtraction(String outTradeNo) {


        
        try {
            String sql = "SELECT param FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {

                return;
            }
            
            String param = (String) results.get(0).get("param");
            String extractedUsername = extractUsernameFromOrderImproved(outTradeNo, param);
            



            
            if (extractedUsername != null && !extractedUsername.isEmpty()) {

            } else {





            }
            
        } catch (Exception e) {

        }
    }
    
    
    private static void checkAmountCalculation(String outTradeNo) {


        
        try {
            String sql = "SELECT money FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {

                return;
            }
            
            String money = results.get(0).get("money").toString();
            int hours = EPayClient.calculateHoursFromMoney(money);
            



            
            if (hours > 0) {


            } else {






            }
            
        } catch (Exception e) {

        }
    }
    
    
    private static void checkUserExists(String outTradeNo) {


        
        try {
            String sql = "SELECT param FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {

                return;
            }
            
            String param = (String) results.get(0).get("param");
            String username = extractUsernameFromOrderImproved(outTradeNo, param);
            
            if (username == null || username.isEmpty()) {

                return;
            }
            
            UserManager userManager = new UserManager();
            boolean userExists = userManager.exists(username);
            
            if (userExists) {

            } else {


            }
            
        } catch (Exception e) {

        }
    }
    
    
    private static void checkTimestampActivation(String outTradeNo) {


        
        try {
            String sql = "SELECT param, money FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {

                return;
            }
            
            Map<String, Object> order = results.get(0);
            String param = (String) order.get("param");
            String money = order.get("money").toString();
            String username = extractUsernameFromOrderImproved(outTradeNo, param);
            
            if (username == null || username.isEmpty()) {

                return;
            }
            
            int hours = EPayClient.calculateHoursFromMoney(money);
            if (hours <= 0) {

                return;
            }
            

            String timestampSql = "SELECT username, activated_at, expires_at, is_active FROM player_timestamps WHERE username = ?";
            List<Map<String, Object>> timestampResults = MySQLUtils.executeQuery(timestampSql, username);
            



            
            if (timestampResults.isEmpty()) {


            } else {
                Map<String, Object> timestamp = timestampResults.get(0);




            }
            
        } catch (Exception e) {

        }
    }
    
    
    private static void provideSolution(String outTradeNo) {


        
        try {
            String sql = "SELECT param, money, status FROM bills WHERE out_trade_no = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, outTradeNo);
            
            if (results.isEmpty()) {

                return;
            }
            
            Map<String, Object> order = results.get(0);
            String param = (String) order.get("param");
            String money = order.get("money").toString();
            String status = order.get("status").toString();
            String username = extractUsernameFromOrderImproved(outTradeNo, param);
            

            

            if (!"1".equals(status)) {

                return;
            }
            

            if (username == null || username.isEmpty()) {


                return;
            }
            

            int hours = EPayClient.calculateHoursFromMoney(money);
            if (hours <= 0) {

                return;
            }
            

            UserManager userManager = new UserManager();
            if (!userManager.exists(username)) {


                return;
            }
            


            boolean activated = TimestampManager.activatePlayer(username, hours);
            
            if (activated) {



                

                yumProxy.server.timestamp.TimestampInfo info = TimestampManager.getPlayerTimestamp(username);
                if (info != null) {



                }
            } else {


            }
            
        } catch (Exception e) {

        }
    }
    
    
    private static String extractUsernameFromOrderImproved(String outTradeNo, String param) {

        if (param != null && !param.trim().isEmpty()) {
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
        
        return null;
    }
} 
