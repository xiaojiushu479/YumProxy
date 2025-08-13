package yumProxy.utils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.timestamp.TimestampInfo;

public class PaymentTimestampValidator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");
    
    private static void log(String msg) {

    }
    
    
    public static void validatePaymentTimestamp(String username) {

        
        try {

            if (info == null) {

                return;
            }
            


            

            String expiresAtStr = "null";
            if (info.activatedAt != null) {
                activatedAtStr = info.activatedAt.toInstant()
                    .atZone(SHANGHAI_ZONE)
                    .format(formatter);
            }
            if (info.expiresAt != null) {
                expiresAtStr = info.expiresAt.toInstant()
                    .atZone(SHANGHAI_ZONE)
                    .format(formatter);
            }




            

            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    private static void validateTimestampLogic(TimestampInfo info) {

        
        ZonedDateTime now = ZonedDateTime.now(SHANGHAI_ZONE);

        
        if (info.activatedAt != null) {
            ZonedDateTime activatedTime = info.activatedAt.toInstant().atZone(SHANGHAI_ZONE);
            


            } else if (activatedTime.isBefore(now.minusDays(30))) {

            } else {

            }
        }
        
        if (info.expiresAt != null) {
            ZonedDateTime expiresTime = info.expiresAt.toInstant().atZone(SHANGHAI_ZONE);
            

            if (info.activatedAt != null) {
                ZonedDateTime activatedTime = info.activatedAt.toInstant().atZone(SHANGHAI_ZONE);
                if (expiresTime.isBefore(activatedTime)) {

                } else {
                    long activatedHours = java.time.Duration.between(activatedTime, expiresTime).toHours();

                }
            }
            


            } else {
                long remainingHours = java.time.Duration.between(now, expiresTime).toHours();

            }
        }
    }
    
    
    private static void validateTimeZone(TimestampInfo info) {

        
        ZonedDateTime now = ZonedDateTime.now(SHANGHAI_ZONE);
        
        if (info.activatedAt != null) {
            ZonedDateTime activatedTime = info.activatedAt.toInstant().atZone(SHANGHAI_ZONE);
            




            } else {

            }
        }
        
        if (info.expiresAt != null) {
            ZonedDateTime expiresTime = info.expiresAt.toInstant().atZone(SHANGHAI_ZONE);
            

            if (expiresTime.isBefore(now.minusHours(6))) {



            } else {

            }
        }
    }
    
    
    public static void checkAllPaymentTimestamps() {

        
        try {

            String sql = "SELECT username, activated_at, expires_at, is_active FROM player_timestamps " +
                        "WHERE activated_at IS NOT NULL ORDER BY activated_at DESC LIMIT 20";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql);
            

            
            ZonedDateTime now = ZonedDateTime.now(SHANGHAI_ZONE);
            int problematicCount = 0;
            
            for (Map<String, Object> row : results) {
                String username = (String) row.get("username");
                Timestamp activatedAt = (Timestamp) row.get("activated_at");
                Timestamp expiresAt = (Timestamp) row.get("expires_at");
                Boolean isActive = (Boolean) row.get("is_active");
                

                
                boolean hasProblem = false;
                if (activatedAt != null) {
                    ZonedDateTime activatedTime = activatedAt.toInstant().atZone(SHANGHAI_ZONE);
                    
                    if (activatedTime.isBefore(now.minusHours(6))) {

                        hasProblem = true;
                    }
                }
                
                if (expiresAt != null) {
                    ZonedDateTime expiresTime = expiresAt.toInstant().atZone(SHANGHAI_ZONE);
                    
                    if (expiresTime.isBefore(now.minusHours(6))) {

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
    
    
    public static void main(String[] args) {

        
        if (args.length > 0) {
            validatePaymentTimestamp(args[0]);
        } else {


            checkAllPaymentTimestamps();
        }
    }
} 
