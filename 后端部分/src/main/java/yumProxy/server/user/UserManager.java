package yumProxy.server.user;

import yumProxy.net.mysql.MySQLUtils;
import java.util.List;
import java.util.Map;

public class UserManager {
    private EmailManager emailManager;
    public UserManager() {
        this.emailManager = new EmailManager();

        if (!MySQLUtils.tableExists("users")) {
            String createUserTable = "CREATE TABLE IF NOT EXISTS `users` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(64) NOT NULL UNIQUE," +
                    "password VARCHAR(128) NOT NULL," +
                    "email VARCHAR(128) NOT NULL," +
                    "rank VARCHAR(16) NOT NULL DEFAULT 'User'," +
                    "pid INT DEFAULT 1," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            MySQLUtils.executeUpdate(createUserTable);

        } else {


                String alter = "ALTER TABLE `users` ADD COLUMN `email` VARCHAR(128) NOT NULL DEFAULT '' AFTER `password`";
                MySQLUtils.executeUpdate(alter);

            }

            List<Map<String, Object>> cols = MySQLUtils.getTableColumns("users");
            boolean hasRank = false, hasPid = false;
            for (Map<String, Object> col : cols) {
                String name = String.valueOf(col.get("name"));
                if ("rank".equalsIgnoreCase(name)) hasRank = true;
                if ("pid".equalsIgnoreCase(name)) hasPid = true;
            }
            if (!hasRank) {
                String alter = "ALTER TABLE `users` ADD COLUMN `rank` VARCHAR(16) NOT NULL DEFAULT 'User' AFTER `email`";
                MySQLUtils.executeUpdate(alter);

            }
            if (!hasPid) {
                String alter = "ALTER TABLE `users` ADD COLUMN `pid` INT DEFAULT 1 AFTER `rank`";
                MySQLUtils.executeUpdate(alter);

            }
        }
    }

    private boolean hasEmailColumn() {
        List<Map<String, Object>> cols = MySQLUtils.getTableColumns("users");
        for (Map<String, Object> col : cols) {
            if ("email".equalsIgnoreCase(String.valueOf(col.get("name")))) {
                return true;
            }
        }
        return false;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    private void log(String msg) {

    }

    public boolean register(String username, String password, String email, String code) {

        if (exists(username)) {

            return false;
        }
        if (emailManager == null || !emailManager.verifyCode(email, code)) {

            return false;
        } else {

        }

        String maxPidSql = "SELECT MAX(pid) AS maxpid FROM users";
        List<Map<String, Object>> res = MySQLUtils.executeQuery(maxPidSql);
        int pid = 1;
        if (!res.isEmpty() && res.get(0).get("maxpid") != null) {
            int maxpid = ((Number)res.get(0).get("maxpid")).intValue();
            pid = Math.max(1, maxpid + 1);
        }
        String rank = "User";
        String sql = "INSERT INTO `users` (username, password, email, rank, pid) VALUES (?, ?, ?, ?, ?)";
        int result = MySQLUtils.executeUpdate(sql, username, password, email, rank, pid);


        return result > 0;
    }

    public boolean exists(String username) {
        String sql = "SELECT id FROM `users` WHERE username = ?";
        List<Map<String, Object>> res = MySQLUtils.executeQuery(sql, username);
        boolean exist = !res.isEmpty();

        return exist;
    }

    public boolean login(String username, String password) {

        try {
            yumProxy.server.user.UserBanManager.BanStatus banStatus = 
                yumProxy.server.user.UserBanManager.checkBanStatus(username);
            
            if (banStatus.isBanned) {

                return false;
            }
        } catch (Exception e) {


        }
        
        String sql = "SELECT id FROM `users` WHERE username = ? AND password = ?";
        List<Map<String, Object>> res = MySQLUtils.executeQuery(sql, username, password);
        boolean ok = !res.isEmpty();

        return ok;
    }
    
    
    public boolean deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }

        String sql = "DELETE FROM `users` WHERE username = ?";
        int result = MySQLUtils.executeUpdate(sql, username);
        boolean success = result > 0;

        return success;
    }
    
    
    public Map<String, Object> getUserInfo(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT username, email, rank, pid, create_time FROM `users` WHERE username = ?";
        List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
        
        if (results.isEmpty()) {
            return null;
        }
        
        return results.get(0);
    }
} 
