package yumProxy.utils;

import yumProxy.net.mysql.MySQL;
import yumProxy.net.mysql.MySQLUtils;
import java.util.List;
import java.util.Map;

public class DatabaseFixer {
    
    public static void main(String[] args) {

        
        try {

            MySQL mysql = MySQL.getInstance();
            mysql.connect();
            if (!mysql.isConnected()) {

                return;
            }

            

            fixUsersTable();
            fixEmailVerificationTable();
            fixPlayerTimestampsTable();
            fixKeysTable();
            fixUsedKeysTable();
            fixBillTable();
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    private static void fixUsersTable() {

        
        try {

            if (!MySQLUtils.tableExists("users")) {

                String createTable = "CREATE TABLE `users` (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(64) NOT NULL UNIQUE," +
                        "password VARCHAR(128) NOT NULL," +
                        "email VARCHAR(128) NOT NULL," +
                        "rank VARCHAR(16) NOT NULL DEFAULT 'User'," +
                        "pid INT DEFAULT 1," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                MySQLUtils.executeUpdate(createTable);

                return;
            }
            

            List<Map<String, Object>> columns = MySQLUtils.getTableColumns("users");
            boolean hasEmail = false, hasRank = false, hasPid = false;
            
            for (Map<String, Object> col : columns) {
                String name = String.valueOf(col.get("name"));
                if ("email".equalsIgnoreCase(name)) hasEmail = true;
                if ("rank".equalsIgnoreCase(name)) hasRank = true;
                if ("pid".equalsIgnoreCase(name)) hasPid = true;
            }
            
            if (!hasEmail) {

                String addEmail = "ALTER TABLE `users` ADD COLUMN `email` VARCHAR(128) NOT NULL DEFAULT '' AFTER `password`";
                MySQLUtils.executeUpdate(addEmail);

            }
            
            if (!hasRank) {

                String addRank = "ALTER TABLE `users` ADD COLUMN `rank` VARCHAR(16) NOT NULL DEFAULT 'User' AFTER `email`";
                MySQLUtils.executeUpdate(addRank);

            }
            
            if (!hasPid) {

                String addPid = "ALTER TABLE `users` ADD COLUMN `pid` INT DEFAULT 1 AFTER `rank`";
                MySQLUtils.executeUpdate(addPid);

            }
            

            fixUserRankColumn();
            

            
        } catch (Exception e) {

            throw e;
        }
    }
    
    
    private static void fixEmailVerificationTable() {

        
        try {

            if (!MySQLUtils.tableExists("email_verification")) {

                String createTable = "CREATE TABLE `email_verification` (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "email VARCHAR(128) NOT NULL," +
                        "code VARCHAR(16) NOT NULL," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "status INT DEFAULT 0)";
                MySQLUtils.executeUpdate(createTable);

            } else {

                

                List<Map<String, Object>> columns = MySQLUtils.getTableColumns("email_verification");
                boolean hasAllFields = columns.size() >= 5;
                
                if (!hasAllFields) {


                    try {
                        String backupSql = "CREATE TABLE IF NOT EXISTS `email_verification_backup` AS SELECT * FROM `email_verification`";
                        MySQLUtils.executeUpdate(backupSql);

                    } catch (Exception e) {

                    }
                    

                    String dropSql = "DROP TABLE `email_verification`";
                    MySQLUtils.executeUpdate(dropSql);
                    

                    String createTable = "CREATE TABLE `email_verification` (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "email VARCHAR(128) NOT NULL," +
                            "code VARCHAR(16) NOT NULL," +
                            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "status INT DEFAULT 0)";
                    MySQLUtils.executeUpdate(createTable);

                } else {

                }
            }
            
        } catch (Exception e) {

            throw e;
        }
    }
    
    
    private static void fixPlayerTimestampsTable() {

        
        try {

            if (!MySQLUtils.tableExists("player_timestamps")) {

                String createTable = "CREATE TABLE `player_timestamps` (" +
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
                MySQLUtils.executeUpdate(createTable);

            } else {

                

                List<Map<String, Object>> columns = MySQLUtils.getTableColumns("player_timestamps");
                boolean hasAllFields = columns.size() >= 7;
                
                if (!hasAllFields) {


                    try {
                        String backupSql = "CREATE TABLE IF NOT EXISTS `player_timestamps_backup` AS SELECT * FROM `player_timestamps`";
                        MySQLUtils.executeUpdate(backupSql);

                    } catch (Exception e) {

                    }
                    

                    String dropSql = "DROP TABLE `player_timestamps`";
                    MySQLUtils.executeUpdate(dropSql);
                    

                    String createTable = "CREATE TABLE `player_timestamps` (" +
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
                    MySQLUtils.executeUpdate(createTable);

                } else {

                }
            }
            
        } catch (Exception e) {

            throw e;
        }
    }
    
    
    private static void fixKeysTable() {

        
        try {

            if (!MySQLUtils.tableExists("keys")) {

                String createTable = "CREATE TABLE `keys` (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "prefix VARCHAR(32) NOT NULL," +
                        "code VARCHAR(64) NOT NULL UNIQUE," +
                        "time_hours INT DEFAULT 0," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "status INT DEFAULT 0)";
                MySQLUtils.executeUpdate(createTable);

                return;
            }
            

            try {
                String checkSql = "SELECT time_hours FROM `keys` LIMIT 1";
                MySQLUtils.executeQuery(checkSql);

                return;
            } catch (Exception e) {

            }
            

            try {
                String addColumnSql = "ALTER TABLE `keys` ADD COLUMN time_hours INT DEFAULT 0";
                MySQLUtils.executeUpdate(addColumnSql);

            } catch (Exception e) {


                


                String backupSql = "CREATE TABLE IF NOT EXISTS `keys_backup` AS SELECT * FROM `keys`";
                MySQLUtils.executeUpdate(backupSql);

                

                String dropSql = "DROP TABLE `keys`";
                MySQLUtils.executeUpdate(dropSql);

                

                String createSql = "CREATE TABLE `keys` (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "prefix VARCHAR(32) NOT NULL," +
                        "code VARCHAR(64) NOT NULL UNIQUE," +
                        "time_hours INT DEFAULT 0," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "status INT DEFAULT 0)";
                MySQLUtils.executeUpdate(createSql);

                

                try {
                    String restoreSql = "INSERT INTO `keys` (prefix, code, create_time, status) " +
                            "SELECT prefix, code, create_time, status FROM `keys_backup`";
                    int affected = MySQLUtils.executeUpdate(restoreSql);

                } catch (Exception restoreException) {

                }
            }
            

            try {
                String verifySql = "SELECT time_hours FROM `keys` LIMIT 1";
                MySQLUtils.executeQuery(verifySql);

            } catch (Exception e) {

                throw e;
            }
            
        } catch (Exception e) {

            throw e;
        }
    }
    
    
    private static void fixUsedKeysTable() {

        
        try {

            if (!MySQLUtils.tableExists("used_keys")) {

                String createTable = "CREATE TABLE `used_keys` (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "prefix VARCHAR(32) NOT NULL," +
                        "code VARCHAR(64) NOT NULL UNIQUE," +
                        "used_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                MySQLUtils.executeUpdate(createTable);

            } else {

                

                List<Map<String, Object>> columns = MySQLUtils.getTableColumns("used_keys");
                boolean hasAllFields = columns.size() >= 4;
                
                if (!hasAllFields) {


                    try {
                        String backupSql = "CREATE TABLE IF NOT EXISTS `used_keys_backup` AS SELECT * FROM `used_keys`";
                        MySQLUtils.executeUpdate(backupSql);

                    } catch (Exception e) {

                    }
                    

                    String dropSql = "DROP TABLE `used_keys`";
                    MySQLUtils.executeUpdate(dropSql);
                    

                    String createTable = "CREATE TABLE `used_keys` (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "prefix VARCHAR(32) NOT NULL," +
                            "code VARCHAR(64) NOT NULL UNIQUE," +
                            "used_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                    MySQLUtils.executeUpdate(createTable);

                } else {

                }
            }
            
        } catch (Exception e) {

            throw e;
        }
    }

    
    public static void fixUserRankColumn() {
        try {
            String sql = "ALTER TABLE `users` MODIFY COLUMN `rank` VARCHAR(16) NOT NULL DEFAULT 'User'";
            int result = MySQLUtils.executeUpdate(sql);
            if (result >= 0) {

            } else {

            }
        } catch (Exception e) {

        }
    }
    
    
    public static void fixAllTables() {

        
        try {

            MySQL mysql = MySQL.getInstance();
            mysql.connect();
            if (!mysql.isConnected()) {

                return;
            }

            

            fixUsersTable();
            fixEmailVerificationTable();
            fixPlayerTimestampsTable();
            fixKeysTable();
            fixUsedKeysTable();
            fixBillTable();
            initUserMinecraftBinding();
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void fixBillTable() {

        try {
            if (!MySQLUtils.tableExists("bill")) {

                MySQLUtils.createBillTable();

            } else {

            }
        } catch (Exception e) {

            throw e;
        }
    }
    
    
    private static void initUserMinecraftBinding() {

        try {
            yumProxy.utils.UserMinecraftBinding.initUserMinecraftBinding();

        } catch (Exception e) {

        }
    }
} 
