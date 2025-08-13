package yumProxy.server.user;

import yumProxy.net.mysql.MySQLUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public class UserBanManager {
    
    private static void log(String msg) {

    }
    
    
    public static void initBanSystem() {
        try {

            

            List<Map<String, Object>> columns = MySQLUtils.getTableColumns("users");
            boolean hasIsBanned = false, hasBannedUntil = false, hasBanReason = false;
            boolean hasBannedBy = false, hasBannedAt = false;
            
            for (Map<String, Object> col : columns) {
                String name = String.valueOf(col.get("name")).toLowerCase();
                switch (name) {
                    case "is_banned" -> hasIsBanned = true;
                    case "banned_until" -> hasBannedUntil = true;
                    case "ban_reason" -> hasBanReason = true;
                    case "banned_by" -> hasBannedBy = true;
                    case "banned_at" -> hasBannedAt = true;
                }
            }
            

            if (!hasIsBanned) {
                String sql = "ALTER TABLE `users` ADD COLUMN `is_banned` BOOLEAN DEFAULT FALSE";
                MySQLUtils.executeUpdate(sql);

            }
            
            if (!hasBannedUntil) {
                String sql = "ALTER TABLE `users` ADD COLUMN `banned_until` TIMESTAMP NULL";
                MySQLUtils.executeUpdate(sql);

            }
            
            if (!hasBanReason) {
                String sql = "ALTER TABLE `users` ADD COLUMN `ban_reason` TEXT NULL";
                MySQLUtils.executeUpdate(sql);

            }
            
            if (!hasBannedBy) {
                String sql = "ALTER TABLE `users` ADD COLUMN `banned_by` VARCHAR(64) NULL";
                MySQLUtils.executeUpdate(sql);

            }
            
            if (!hasBannedAt) {
                String sql = "ALTER TABLE `users` ADD COLUMN `banned_at` TIMESTAMP NULL";
                MySQLUtils.executeUpdate(sql);

            }
            

            createBanLogTable();
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    private static void createBanLogTable() {
        try {
            if (!MySQLUtils.tableExists("user_ban_logs")) {
                String createTable = """
                    CREATE TABLE IF NOT EXISTS `user_ban_logs` (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(64) NOT NULL,
                        action_type ENUM('BAN', 'UNBAN', 'TEMP_BAN') NOT NULL,
                        reason TEXT,
                        banned_by VARCHAR(64) NOT NULL,
                        banned_until TIMESTAMP NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        INDEX idx_username (username),
                        INDEX idx_action_type (action_type),
                        INDEX idx_created_at (created_at)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """;
                MySQLUtils.executeUpdate(createTable);

            } else {

            }
        } catch (Exception e) {

        }
    }
    
    
    public static BanResult banUser(String username, String reason, String bannedBy, Integer duration) {
        try {

            

            if (!userExists(username)) {
                return new BanResult(false, "闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛?, null);
            }
            

            if (isUserBanned(username)) {
                return new BanResult(false, "闁活潿鍔嶉崺娑橆啅閼奸娼堕悘蹇庤兌椤?, null);
            }
            

            Timestamp bannedUntil = null;
            if (duration != null && duration > 0) {
                bannedUntil = Timestamp.valueOf(LocalDateTime.now().plusMinutes(duration));
            }
            

            String updateSql = """
                UPDATE `users` SET 
                    is_banned = TRUE, 
                    banned_until = ?, 
                    ban_reason = ?, 
                    banned_by = ?, 
                    banned_at = CURRENT_TIMESTAMP 
                WHERE username = ?
                """;
            
            int affected = MySQLUtils.executeUpdate(updateSql, bannedUntil, reason, bannedBy, username);
            
            if (affected > 0) {

                String actionType = (duration == null) ? "BAN" : "TEMP_BAN";
                logBanAction(username, actionType, reason, bannedBy, bannedUntil);
                
                String message = (duration == null) ? 
                    "闁活潿鍔嶉崺娑橆啅閼奸娼舵慨姗€鏅茬粻娆戜焊娴ｄ警娲? : 
                    "闁活潿鍔嶉崺娑橆啅閼奸娼跺☉鎾崇摠濡炲倻浜告担渚矗 " + duration + " 闁告帒妫濋幐?;
                

                return new BanResult(true, message, bannedUntil);
            } else {
                return new BanResult(false, "閻忓繋鑳堕々锕傚箼瀹ュ嫮绋婂鎯扮簿鐟?, null);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            return new BanResult(false, "缂侇垵宕电划娲煥濞嗘帩鍤? " + e.getMessage(), null);
        }
    }
    
    
    public static BanResult unbanUser(String username, String unbannedBy) {
        try {

            

            if (!userExists(username)) {
                return new BanResult(false, "闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛?, null);
            }
            

            if (!isUserBanned(username)) {
                return new BanResult(false, "闁活潿鍔嶉崺娑㈠嫉椤忓浂娼堕悘蹇庤兌椤?, null);
            }
            

            String updateSql = """
                UPDATE `users` SET 
                    is_banned = FALSE, 
                    banned_until = NULL, 
                    ban_reason = NULL, 
                    banned_by = NULL, 
                    banned_at = NULL 
                WHERE username = ?
                """;
            
            int affected = MySQLUtils.executeUpdate(updateSql, username);
            
            if (affected > 0) {

                logBanAction(username, "UNBAN", "缂佺媴绱曢幃濠囧川濡晝鎺斾焊?, unbannedBy, null);
                

                return new BanResult(true, "闁活潿鍔嶉崺娑橆啅閼肩紟鎺斾焊?, null);
            } else {
                return new BanResult(false, "閻熸瑱绲介惃婵嬪箼瀹ュ嫮绋婂鎯扮簿鐟?, null);
            }
            
        } catch (Exception e) {

            e.printStackTrace();
            return new BanResult(false, "缂侇垵宕电划娲煥濞嗘帩鍤? " + e.getMessage(), null);
        }
    }
    
    
    public static BanStatus checkBanStatus(String username) {
        try {
            if (!userExists(username)) {
                return new BanStatus(false, "闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛?, null, null, null, null);
            }
            
            String sql = """
                SELECT is_banned, banned_until, ban_reason, banned_by, banned_at 
                FROM `users` WHERE username = ?
                """;
            
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {
                return new BanStatus(false, "闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛?, null, null, null, null);
            }
            
            Map<String, Object> user = results.get(0);
            boolean isBanned = Boolean.parseBoolean(String.valueOf(user.get("is_banned")));
            
            if (!isBanned) {
                return new BanStatus(false, "闁活潿鍔嶉崺娑㈠嫉椤忓浂娼堕悘蹇庤兌椤?, null, null, null, null);
            }
            

            Timestamp bannedUntil = (Timestamp) user.get("banned_until");
            if (bannedUntil != null && bannedUntil.before(new Timestamp(System.currentTimeMillis()))) {

                unbanUser(username, "SYSTEM_AUTO");
                return new BanStatus(false, "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲ｇ€规瓕灏换鍐嫉閻曞倻绀夌€规瓕灏崵婊堝礉閵娿剱鎺斾焊?, null, null, null, null);
            }
            
            String reason = (String) user.get("ban_reason");
            String bannedBy = (String) user.get("banned_by");
            Timestamp bannedAt = (Timestamp) user.get("banned_at");
            
            String message = (bannedUntil == null) ? "闁活潿鍔嶉崺娑氭偖椤愶絾顢嶅☉鏂挎噹閻ㄦ繄绮? : "闁活潿鍔嶉崺娑氭偖椤愨€愁槻闁哄啳娉涢惃婵堢矉娴ｈ棄娈?" + bannedUntil;
            
            return new BanStatus(true, message, reason, bannedBy, bannedAt, bannedUntil);
            
        } catch (Exception e) {

            return new BanStatus(false, "缂侇垵宕电划娲煥濞嗘帩鍤? " + e.getMessage(), null, null, null, null);
        }
    }
    
    
    public static List<Map<String, Object>> getBannedUsers() {
        try {
            String sql = """
                SELECT username, ban_reason, banned_by, banned_at, banned_until 
                FROM `users` 
                WHERE is_banned = TRUE 
                ORDER BY banned_at DESC
                """;
            
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql);
            List<Map<String, Object>> bannedUsers = new ArrayList<>();
            
            for (Map<String, Object> user : results) {
                Map<String, Object> banInfo = new HashMap<>();
                banInfo.put("username", user.get("username"));
                banInfo.put("ban_reason", user.get("ban_reason"));
                banInfo.put("banned_by", user.get("banned_by"));
                banInfo.put("banned_at", user.get("banned_at"));
                banInfo.put("banned_until", user.get("banned_until"));
                

                Timestamp bannedUntil = (Timestamp) user.get("banned_until");
                if (bannedUntil == null) {
                    banInfo.put("ban_type", "婵﹢鏅茬粻娆戜焊娴ｄ警娲?);
                } else if (bannedUntil.before(new Timestamp(System.currentTimeMillis()))) {
                    banInfo.put("ban_type", "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?鐎规瓕灏换鍐嫉?");
                } else {
                    banInfo.put("ban_type", "濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?);
                }
                
                bannedUsers.add(banInfo);
            }
            
            return bannedUsers;
            
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }
    
    
    public static List<Map<String, Object>> getBanLogs(String username, int limit) {
        try {
            String sql;
            List<Map<String, Object>> results;
            
            if (username != null && !username.trim().isEmpty()) {
                sql = """
                    SELECT * FROM `user_ban_logs` 
                    WHERE username = ? 
                    ORDER BY created_at DESC 
                    LIMIT ?
                    """;
                results = MySQLUtils.executeQuery(sql, username, limit);
            } else {
                sql = """
                    SELECT * FROM `user_ban_logs` 
                    ORDER BY created_at DESC 
                    LIMIT ?
                    """;
                results = MySQLUtils.executeQuery(sql, limit);
            }
            
            return results;
            
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }
    
    
    public static int cleanExpiredBans() {
        try {
            String sql = """
                UPDATE `users` SET 
                    is_banned = FALSE, 
                    banned_until = NULL, 
                    ban_reason = NULL, 
                    banned_by = NULL, 
                    banned_at = NULL 
                WHERE is_banned = TRUE 
                    AND banned_until IS NOT NULL 
                    AND banned_until < CURRENT_TIMESTAMP
                """;
            
            int affected = MySQLUtils.executeUpdate(sql);
            
            if (affected > 0) {

            }
            
            return affected;
            
        } catch (Exception e) {

            return 0;
        }
    }
    

    
    
    private static boolean userExists(String username) {
        try {
            String sql = "SELECT id FROM `users` WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    
    private static boolean isUserBanned(String username) {
        try {
            String sql = "SELECT is_banned FROM `users` WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {
                return false;
            }
            
            return Boolean.parseBoolean(String.valueOf(results.get(0).get("is_banned")));
        } catch (Exception e) {
            return false;
        }
    }
    
    
    private static void logBanAction(String username, String actionType, String reason, 
                                   String operatorName, Timestamp bannedUntil) {
        try {
            String sql = """
                INSERT INTO `user_ban_logs` (username, action_type, reason, banned_by, banned_until) 
                VALUES (?, ?, ?, ?, ?)
                """;
            
            MySQLUtils.executeUpdate(sql, username, actionType, reason, operatorName, bannedUntil);
        } catch (Exception e) {

        }
    }
    

    
    
    public static class BanResult {
        public final boolean success;
        public final String message;
        public final Timestamp bannedUntil;
        
        public BanResult(boolean success, String message, Timestamp bannedUntil) {
            this.success = success;
            this.message = message;
            this.bannedUntil = bannedUntil;
        }
    }
    
    
    public static class BanStatus {
        public final boolean isBanned;
        public final String message;
        public final String reason;
        public final String bannedBy;
        public final Timestamp bannedAt;
        public final Timestamp bannedUntil;
        
        public BanStatus(boolean isBanned, String message, String reason, 
                        String bannedBy, Timestamp bannedAt, Timestamp bannedUntil) {
            this.isBanned = isBanned;
            this.message = message;
            this.reason = reason;
            this.bannedBy = bannedBy;
            this.bannedAt = bannedAt;
            this.bannedUntil = bannedUntil;
        }
    }
} 
