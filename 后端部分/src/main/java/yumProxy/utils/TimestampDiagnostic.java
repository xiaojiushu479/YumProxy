package yumProxy.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import yumProxy.net.mysql.MySQLUtils;

public class TimestampDiagnostic {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static void log(String msg) {

    }
    
    
    public static void checkTimeZones() {

        

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime shanghaiTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        




        

        try {
            String sql = "SELECT @@global.time_zone, @@session.time_zone, NOW() as current_time, UTC_TIMESTAMP() as utc_time";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql);
            
            if (!results.isEmpty()) {
                Map<String, Object> row = results.get(0);




            }
        } catch (Exception e) {

        }
    }
    
    
    public static void checkAllTimestamps() {

        
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        
        try {
            String sql = "SELECT username, activated_at, expires_at, is_active, created_at, updated_at FROM player_timestamps ORDER BY username";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql);
            

            
            int problematicCount = 0;
            for (Map<String, Object> row : results) {
                String username = (String) row.get("username");
                Timestamp activatedAt = (Timestamp) row.get("activated_at");
                Timestamp expiresAt = (Timestamp) row.get("expires_at");
                Boolean isActive = (Boolean) row.get("is_active");
                Timestamp createdAt = (Timestamp) row.get("created_at");
                Timestamp updatedAt = (Timestamp) row.get("updated_at");
                

                String expiresStr = "null";
                if (activatedAt != null) {
                    activatedStr = activatedAt.toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .format(formatter);
                }
                if (expiresAt != null) {
                    expiresStr = expiresAt.toInstant()
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .format(formatter);
                }
                





                    createdAt.toInstant().atZone(ZoneId.of("Asia/Shanghai")).format(formatter) : "null"));

                    updatedAt.toInstant().atZone(ZoneId.of("Asia/Shanghai")).format(formatter) : "null"));
                

                boolean hasProblem = false;
                if (activatedAt != null) {
                    ZonedDateTime activatedDateTime = activatedAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));
                    if (activatedDateTime.isBefore(now.minusHours(6))) {

                        hasProblem = true;
                    }
                }
                
                if (expiresAt != null) {
                    ZonedDateTime expiresDateTime = expiresAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));
                    if (expiresDateTime.isBefore(now.minusHours(6))) {

                        hasProblem = true;
                    }
                }
                
                if (hasProblem) {
                    problematicCount++;
                }
                

            }
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    public static void fixPlayerTimestamp(String username) {

        
        try {
            String sql = "SELECT username, activated_at, expires_at FROM player_timestamps WHERE username = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (results.isEmpty()) {

                return;
            }
            
            Map<String, Object> row = results.get(0);
            Timestamp activatedAt = (Timestamp) row.get("activated_at");
            Timestamp expiresAt = (Timestamp) row.get("expires_at");
            

            String activatedAtStr = "null";
            String expiresAtStr = "null";
            if (activatedAt != null) {
                activatedAtStr = activatedAt.toInstant().atZone(ZoneId.of("Asia/Shanghai")).format(formatter);
            }
            if (expiresAt != null) {
                expiresAtStr = expiresAt.toInstant().atZone(ZoneId.of("Asia/Shanghai")).format(formatter);
            }


            

            ZonedDateTime correctActivatedAt = null;
            ZonedDateTime correctExpiresAt = null;
            
            if (activatedAt != null) {
                ZonedDateTime shanghaiTime = activatedAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));

                    correctActivatedAt = shanghaiTime.plusHours(8);

                } else {
                    correctActivatedAt = shanghaiTime;

                }
            }
            
            if (expiresAt != null) {
                ZonedDateTime shanghaiTime = expiresAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));

                    correctExpiresAt = shanghaiTime.plusHours(8);

                } else {
                    correctExpiresAt = shanghaiTime;

                }
            }
            

                String updateSql = "UPDATE player_timestamps SET activated_at = ?, expires_at = ? WHERE username = ?";
                MySQLUtils.executeUpdate(updateSql, correctActivatedAt, correctExpiresAt, username);

            } else {

            }
            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    public static void fixAllTimestampTimeZone() {

        
        try {

            String selectSql = "SELECT username, activated_at, expires_at FROM player_timestamps";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(selectSql);
            
            int fixedCount = 0;
            for (Map<String, Object> row : results) {
                String username = (String) row.get("username");
                Timestamp activatedAt = (Timestamp) row.get("activated_at");
                Timestamp expiresAt = (Timestamp) row.get("expires_at");
                
                boolean needsUpdate = false;
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
                Timestamp newActivatedAt = null;
                Timestamp newExpiresAt = null;
                

                    ZonedDateTime activatedDateTime = activatedAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));

                        ZonedDateTime correctedTime = activatedDateTime.plusHours(8);
                        newActivatedAt = Timestamp.from(correctedTime.toInstant());
                        needsUpdate = true;

                    } else {
                        newActivatedAt = activatedAt;
                    }
                }
                

                    ZonedDateTime expiresDateTime = expiresAt.toInstant().atZone(ZoneId.of("Asia/Shanghai"));

                    if (expiresDateTime.isBefore(now.minusHours(6))) {
                        ZonedDateTime correctedTime = expiresDateTime.plusHours(8);
                        newExpiresAt = Timestamp.from(correctedTime.toInstant());
                        needsUpdate = true;

                    } else {
                        newExpiresAt = expiresAt;
                    }
                }
                

                    String updateSql = "UPDATE player_timestamps SET activated_at = ?, expires_at = ? WHERE username = ?";
                    MySQLUtils.executeUpdate(updateSql, newActivatedAt, newExpiresAt, username);
                    fixedCount++;
                }
            }
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) {

        


        

        checkAllTimestamps();

        

        if (args.length > 0) {
            fixPlayerTimestamp(args[0]);

        }
        


    }
} 
