package yumProxy.server.user;

import yumProxy.net.mysql.MySQLUtils;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import yumProxy.net.Config.ConfigManager;

public class EmailManager {









    private static final ExecutorService emailThreadPool = Executors.newFixedThreadPool(4);

    public EmailManager() {

        if (!MySQLUtils.tableExists("email_verification")) {
            String createTable = "CREATE TABLE IF NOT EXISTS `email_verification` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "email VARCHAR(128) NOT NULL," +
                    "code VARCHAR(16) NOT NULL," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status INT DEFAULT 0)";
            MySQLUtils.executeUpdate(createTable);

        } else {

        }
    }

    private void log(String msg) {

    }

    public boolean sendCode(String email) {


        String code = generateCode();
        if (saveCode(email, code)) {
            return sendEmailAsyncWait(email, code);
        }
        return false;
    }
    
    
    private String getCallStack() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < Math.min(stackTrace.length, 6); i++) {
                sb.append(element.getClassName()).append(".").append(element.getMethodName()).append(":");
                sb.append(element.getLineNumber()).append(" ");
            }
            return sb.toString();
        } catch (Exception e) {
            return "闁哄啰濮电涵鍫曟嚔瀹勬澘绲块悹瀣暟閺併倝寮?;
        }
    }

    private boolean saveCode(String email, String code) {
        String sql = "INSERT INTO `email_verification` (email, code, status) VALUES (?, ?, 0)";

        int result = MySQLUtils.executeUpdate(sql, email, code);

        return result > 0;
    }

    public boolean verifyCode(String email, String code) {

        String sql = "SELECT id FROM `email_verification` WHERE email = ? AND code = ? AND status = 0 ORDER BY create_time DESC LIMIT 1";



        List<Map<String, Object>> res = MySQLUtils.executeQuery(sql, email, code);

        boolean ok = !res.isEmpty();

        if (ok) {
            int id = ((Number)res.get(0).get("id")).intValue();
            String update = "UPDATE `email_verification` SET status = 1 WHERE id = ?";

            MySQLUtils.executeUpdate(update, id);
        }
        return ok;
    }

    private String generateCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000);
    }


        Future<Boolean> future = emailThreadPool.submit(() -> {

            String smtpUser = yumProxy.net.Config.ServiceConfig.getSmtpUser();
            String smtpPass = yumProxy.net.Config.ServiceConfig.getSmtpPass();
            String fromName = yumProxy.net.Config.ServiceConfig.getSmtpFromName();
            

            
            try {

                java.net.URL url = new java.net.URL(apiUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setDoInput(true);
                


                conn.setRequestProperty("X-SMTP-User", smtpUser);
                conn.setRequestProperty("X-SMTP-Pass", smtpPass);
                conn.setRequestProperty("X-SMTP-From-Name", fromName);
                

                String emailContent = "闁诡喓鍔庡▓鎴烆殽瀹€鍐闁活喕鐒﹀Σ? " + code + " 濠碘€冲€垮顏堝嫉椤戞寧鐪介柟鍨С缂嶆梻鎷犲畡鐗堝闁伙絻鍎埀?;
                String postData = "from=" + java.net.URLEncoder.encode(smtpUser, "UTF-8") +
                                "&to=" + java.net.URLEncoder.encode(to, "UTF-8") +
                                "&subject=" + java.net.URLEncoder.encode("闁诡喓鍔庡▓鎴烆殽瀹€鍐闁?, "UTF-8") +
                                "&content=" + java.net.URLEncoder.encode(emailContent, "UTF-8") +
                                "&contentType=text";
                

                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = postData.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                

                

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    return true;
                } else {

                    java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getErrorStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    

                    return false;
                }
                
            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
        });
        
        try {

        } catch (Exception e) {

            future.cancel(true);
            return false;
        }
    }
} 
