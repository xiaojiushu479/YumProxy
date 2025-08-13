package yumProxy.net.httpAPI;

import yumProxy.server.user.UserKeyManager;
import yumProxy.server.superkey.SuperKeyGenerator;


public class AuthValidator {
    
    private static void log(String msg) {

    }
    
    
    public static String getCurrentSuperKey() {
        return SuperKeyGenerator.getCurrentSuperKey();
    }
    
    
    public static boolean isAdmin(String superKey) {
        if (superKey == null || superKey.isEmpty()) {

            return false;
        }
        

        


            return true;
        }
        

        if (superKey.equals(currentTimeKey)) {

            return true;
        }
        

        String dailyKey = SuperKeyGenerator.generateSuperKey();
        if (superKey.equals(dailyKey)) {

            return true;
        }
        




        return false;
    }
    
    
    public static boolean hasUserPermission(String username, String userKey) {
        if (username == null || username.trim().isEmpty()) {

            return false;
        }
        
        if (userKey == null || userKey.trim().isEmpty()) {

            return false;
        }
        

        
        boolean isValid = UserKeyManager.validateUserKey(username, userKey);
        if (isValid) {

            try {
                yumProxy.server.user.UserBanManager.BanStatus banStatus = 
                    yumProxy.server.user.UserBanManager.checkBanStatus(username);
                
                if (banStatus.isBanned) {

                    return false;
                }
                

            } catch (Exception e) {


            }
        } else {

        }
        return isValid;
    }
    
    
    public static boolean hasPermission(String username, String userKey, String superKey) {

        if (isAdmin(superKey)) {

            return true;
        }
        

        if (hasUserPermission(username, userKey)) {

            return true;
        }
        

        return false;
    }
    
    
    public static boolean isAdmin(String username, String password, String superKey) {
        return isAdmin(superKey);
    }
    
    
    public static boolean isAdminUser(String username, String superKey) {
        return isAdmin(superKey);
    }
} 
