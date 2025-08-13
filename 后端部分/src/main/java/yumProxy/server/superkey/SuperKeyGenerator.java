package yumProxy.server.superkey;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class SuperKeyGenerator {

    private static final String SECRET_SEED = "YOUR_SUPER_KEY_STATIC_SEED";
    private static final String TIME_BASED_SECRET = "YOUR_SUPER_KEY_TIME_BASED_SEED";
    private static final int TIME_WINDOW = 300;
    private static final int KEY_LENGTH = 64;
    private static final String KEY_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    private static final SecureRandom secureRandom = new SecureRandom();

    
    public static String generateSuperKey() {
        String today = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String raw = SECRET_SEED + today;
        return sha256(raw).substring(0, KEY_LENGTH);
    }

    
    public static String generateTimeBasedSuperKey() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        int minute = (now.getMinute() / 5) * 5;
        LocalDateTime windowTime = now.withMinute(minute).withSecond(0).withNano(0);
        
        String timeString = windowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        String raw = TIME_BASED_SECRET + timeString;
        return sha256(raw).substring(0, KEY_LENGTH);
    }

    
    public static String generateRandomSuperKey() {
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            key.append(KEY_CHARSET.charAt(secureRandom.nextInt(KEY_CHARSET.length())));
        }
        return key.toString();
    }

    
    public static boolean isValidSuperKey(String key) {
        if (key == null || key.length() != KEY_LENGTH) {
            return false;
        }
        

            if (!KEY_CHARSET.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        return true;
    }

    
    public static boolean validateTimeBasedSuperKey(String key) {
        if (!isValidSuperKey(key)) {
            return false;
        }
        

        String currentKey = generateTimeBasedSuperKey();
        if (key.equals(currentKey)) {
            return true;
        }
        

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int currentMinute = (now.getMinute() / 5) * 5;
        int previousMinute = currentMinute - 5;
        if (previousMinute < 0) {
            previousMinute = 55;
            now = now.minusHours(1);
        }
        
        LocalDateTime previousWindow = now.withMinute(previousMinute).withSecond(0).withNano(0);
        String previousTimeString = previousWindow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        String previousRaw = TIME_BASED_SECRET + previousTimeString;
        String previousKey = sha256(previousRaw).substring(0, KEY_LENGTH);
        
        return key.equals(previousKey);
    }

    
    public static void showCurrentKeyInfo() {





        
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int minute = (now.getMinute() / 5) * 5;
        LocalDateTime windowTime = now.withMinute(minute).withSecond(0).withNano(0);


    }

    
    public static String getCurrentSuperKey() {
        return generateTimeBasedSuperKey();
    }

    private static String sha256(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String hexStr = Integer.toHexString(0xff & b);
                if (hexStr.length() == 1) hex.append('0');
                hex.append(hexStr);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256閻犱緤绱曢悾缁樺緞鏉堫偉袝", e);
        }
    }

    
    public static void main(String[] args) {
        if (args.length == 0) {
            showCurrentKeyInfo();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "daily":

                break;
            case "time":

                break;
            case "random":

                break;
            case "validate":
                if (args.length < 2) {

                    return;
                }
                String key = args[1];


                break;
            case "info":
                showCurrentKeyInfo();
                break;
            default:


                break;
        }
    }
} 
