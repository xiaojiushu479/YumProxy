package yumProxy.server.user;

import yumProxy.net.mysql.MySQLUtils;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;


public class UserKeyManager {
    
    private static void log(String msg) {

    }
    
    
    public static String generateUserKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        String key = Base64.getEncoder().encodeToString(bytes);

        key = key.replaceAll("[^A-Za-z0-9]", "");

        if (key.length() > 64) {
            key = key.substring(0, 64);
        } else if (key.length() < 64) {

            while (key.length() < 64) {
                key += generateRandomChar();
            }
        }
        return key;
    }
    
    
    private static char generateRandomChar() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return chars.charAt(random.nextInt(chars.length()));
    }
    
    
    public static boolean isValidUserKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }

        if (key.length() != 64) {
            return false;
        }

        return key.matches("^[A-Za-z0-9]{64}$");
    }
    
    
    public static String generateAndSaveUserKey(String username) {
        if (username == null || username.trim().isEmpty()) {

            return null;
        }
        
        try {

            String checkSql = "SELECT id FROM users WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(checkSql, username);
            
            if (results.isEmpty()) {

                return null;
            }
            

            String checkKeySql = "SELECT user_key FROM user_keys WHERE username = ?";
            List<Map<String, Object>> keyResults = MySQLUtils.executeQuery(checkKeySql, username);
            
            if (!keyResults.isEmpty()) {
                String existingKey = (String) keyResults.get(0).get("user_key");

                return existingKey;
            }
            

            String userKey = generateUserKey();
            

            String insertSql = "INSERT INTO user_keys (username, user_key, create_time) VALUES (?, ?, NOW())";
            MySQLUtils.executeUpdate(insertSql, username, userKey);
            

            return userKey;
            
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static boolean validateUserKey(String username, String userKey) {
        if (username == null || username.trim().isEmpty() || 
            userKey == null || userKey.trim().isEmpty()) {
            return false;
        }
        
        try {
            String sql = "SELECT user_key FROM user_keys WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {

                return false;
            }
            
            String storedKey = (String) results.get(0).get("user_key");
            boolean isValid = userKey.equals(storedKey);
            

            return isValid;
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static String getUserKey(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        try {
            String sql = "SELECT user_key FROM user_keys WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {
                return null;
            }
            
            return (String) results.get(0).get("user_key");
            
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static boolean deleteUserKey(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            String sql = "DELETE FROM user_keys WHERE username = ?";
            int affected = MySQLUtils.executeUpdate(sql, username);
            
            boolean success = affected > 0;

            return success;
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static void generateKeysForAllUsers() {
        try {

            

            String getAllUsersSql = "SELECT username FROM users";
            List<Map<String, Object>> users = MySQLUtils.executeQuery(getAllUsersSql);
            
            int totalUsers = users.size();
            int generatedKeys = 0;
            int existingKeys = 0;
            
            for (Map<String, Object> user : users) {
                String username = (String) user.get("username");
                

                String checkKeySql = "SELECT user_key FROM user_keys WHERE username = ?";
                List<Map<String, Object>> keyResults = MySQLUtils.executeQuery(checkKeySql, username);
                
                if (keyResults.isEmpty()) {

                    String userKey = generateUserKey();
                    String insertSql = "INSERT INTO user_keys (username, user_key, create_time) VALUES (?, ?, NOW())";
                    MySQLUtils.executeUpdate(insertSql, username, userKey);
                    generatedKeys++;

                } else {
                    existingKeys++;

                }
            }
            

                ", 闁哄倹澹嗛弫鎾诲箣閹邦剛妲曢梺? " + generatedKeys + 
                ", 鐎圭寮跺﹢浣衡偓闈涙閹? " + existingKeys);
                
        } catch (Exception e) {

        }
    }
    
    
    public static void createUserKeysTable() {
        try {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS user_keys (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    user_key VARCHAR(64) NOT NULL,
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_username (username),
                    INDEX idx_user_key (user_key)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
            
            MySQLUtils.executeUpdate(createTableSql);

            
        } catch (Exception e) {

        }
    }
} 
