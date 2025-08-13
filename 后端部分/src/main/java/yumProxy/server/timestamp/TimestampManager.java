package yumProxy.server.timestamp;

import yumProxy.net.mysql.MySQLUtils;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimestampManager {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static void log(String msg) {

    }
    
    
    public static void initTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS player_timestamps (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(50) NOT NULL UNIQUE," +
            "activated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "expires_at TIMESTAMP NULL," +
            "is_active BOOLEAN DEFAULT TRUE," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "INDEX idx_username (username)," +
            "INDEX idx_expires_at (expires_at)," +
            "INDEX idx_is_active (is_active)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        
        try {
            MySQLUtils.executeUpdate(createTableSql);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    public static boolean activatePlayer(String username, int hours) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }
        

            yumProxy.server.user.UserManager userManager = yumProxy.net.httpAPI.UserApiServer.getUserManager();
            if (!userManager.exists(username)) {

                return false;
            }
        } catch (Exception e) {

            return false;
        }
        
        if (hours <= 0) {

            return false;
        }
        
        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            Timestamp nowTs = Timestamp.from(now.toInstant());
            Timestamp expiresTs = Timestamp.from(now.plusHours(hours).toInstant());
            

            
            String checkSql = "SELECT id, expires_at FROM player_timestamps WHERE username = ?";
            List<Map<String, Object>> existing = MySQLUtils.executeQuery(checkSql, username);
            
            if (existing.isEmpty()) {

                MySQLUtils.executeUpdate(insertSql, username, nowTs, expiresTs);

            } else {

                Timestamp currentExpiresAt = (Timestamp) existingData.get("expires_at");
                

                if (currentExpiresAt != null) {
                    currentExpiresAtStr = currentExpiresAt.toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .format(formatter);
                }
                String currentSystemTimeStr = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).format(formatter);

                
                ZonedDateTime newExpiresAt;
                if (currentExpiresAt != null && currentExpiresAt.after(new Timestamp(System.currentTimeMillis()))) {


                } else {


                }
                
                String updateSql = "UPDATE player_timestamps SET expires_at = ?, is_active = TRUE, updated_at = CURRENT_TIMESTAMP WHERE username = ?";
                MySQLUtils.executeUpdate(updateSql, Timestamp.from(newExpiresAt.toInstant()), username);
            }
            
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
    
    
    public static TimestampInfo getPlayerTimestamp(String username) {
        if (username == null || username.trim().isEmpty()) {

            return null;
        }
        
        try {
            String sql = "SELECT username, activated_at, expires_at, is_active FROM player_timestamps WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {

                return null;
            }
            
            Map<String, Object> row = results.get(0);
            TimestampInfo info = new TimestampInfo();
            info.username = (String) row.get("username");
            info.activatedAt = (Timestamp) row.get("activated_at");
            info.expiresAt = (Timestamp) row.get("expires_at");

            info.isActive = (Boolean) row.get("is_active");
            

            if (info.expiresAt != null && info.expiresAt.before(now)) {
                info.isExpired = true;

                String updateSql = "UPDATE player_timestamps SET is_active = FALSE WHERE username = ?";
                MySQLUtils.executeUpdate(updateSql, username);
                info.isActive = false;
            } else {
                info.isExpired = false;
            }
            
            return info;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
    
    
    public static boolean isPlayerActive(String username) {
        TimestampInfo info = getPlayerTimestamp(username);
        if (info == null) {
            return false;
        }
        return info.isActive && !info.isExpired;
    }
    
    
    public static boolean extendPlayerTime(String username, int hours) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }
        
        if (hours <= 0) {

            return false;
        }
        
        try {
            TimestampInfo currentInfo = getPlayerTimestamp(username);
            if (currentInfo == null) {

                return false;
            }
            
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime newExpiresAt;
            
            if (currentInfo.expiresAt != null && currentInfo.expiresAt.after(new Timestamp(System.currentTimeMillis()))) {

                newExpiresAt = currentExpires.plusHours(hours);

            } else {


            }
            
            String updateSql = "UPDATE player_timestamps SET expires_at = ?, is_active = TRUE, updated_at = CURRENT_TIMESTAMP WHERE username = ?";
            MySQLUtils.executeUpdate(updateSql, Timestamp.from(newExpiresAt.toInstant()), username);
            

            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
    
    
    public static boolean deactivatePlayer(String username) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }
        
        try {
            String updateSql = "UPDATE player_timestamps SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP WHERE username = ?";
            int affected = MySQLUtils.executeUpdate(updateSql, username);
            
            if (affected > 0) {

                return true;
            } else {

                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
    
    
    public static boolean deletePlayer(String username) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }
        
        try {
            String deleteSql = "DELETE FROM player_timestamps WHERE username = ?";
            int affected = MySQLUtils.executeUpdate(deleteSql, username);
            
            if (affected > 0) {

                return true;
            } else {

                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
    
    
    public static List<String> getExpiredPlayers() {
        try {
            String sql = "SELECT username FROM player_timestamps WHERE expires_at < NOW() AND is_active = TRUE";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql);
            
            List<String> expiredPlayers = new ArrayList<>();
            for (Map<String, Object> row : results) {
                expiredPlayers.add((String) row.get("username"));
            }
            return expiredPlayers;
        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    
    public static int cleanupExpiredPlayers() {
        try {
            String updateSql = "UPDATE player_timestamps SET is_active = FALSE WHERE expires_at < NOW() AND is_active = TRUE";
            int affected = MySQLUtils.executeUpdate(updateSql);

            return affected;
        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }
} 
