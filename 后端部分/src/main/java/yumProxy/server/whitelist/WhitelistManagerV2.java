package yumProxy.server.whitelist;

import yumProxy.utils.UserMinecraftBinding;
import java.util.List;
import java.util.Map;


public class WhitelistManagerV2 {
    
    
    public static boolean validatePlayer(String minecraftUsername) {
        try {

            

            if (!isValidMinecraftUsername(minecraftUsername)) {

                return false;
            }
            

            UserMinecraftBinding.WhitelistStatus status = UserMinecraftBinding.checkMinecraftUserWhitelistStatus(minecraftUsername);
            
            if (status.isValid()) {

                                 " -> " + status.getMessage());
                if (status.getTimestampInfo() != null) {

                }
            } else {

                                 " -> " + status.getMessage());
            }
            
            return status.isValid();
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static boolean bindPlayer(String username, String minecraftUsername) {
        try {

            
            UserMinecraftBinding.BindingResult result = UserMinecraftBinding.bindMinecraftUser(username, minecraftUsername);
            
            if (result.isSuccess()) {

                return true;
            } else {

                return false;
            }
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static boolean unbindPlayer(String username) {
        try {

            
            UserMinecraftBinding.BindingResult result = UserMinecraftBinding.unbindMinecraftUser(username);
            
            if (result.isSuccess()) {

                return true;
            } else {

                return false;
            }
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static boolean unbindPlayerByMinecraftUsername(String minecraftUsername) {
        try {

            

            String systemUser = UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftUsername);
            if (systemUser == null) {

                return false;
            }
            

            return unbindPlayer(systemUser);
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static List<Map<String, Object>> getAllBindings() {
        try {

            return UserMinecraftBinding.getAllBindings();
        } catch (Exception e) {

            return new java.util.ArrayList<>();
        }
    }
    
    
    public static List<Map<String, Object>> searchBindings(String keyword) {
        try {

            
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllBindings();
            }
            
            List<Map<String, Object>> allBindings = getAllBindings();
            List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            String searchKeyword = keyword.toLowerCase();
            
            for (Map<String, Object> binding : allBindings) {
                String username = (String) binding.get("username");
                String minecraftUsername = (String) binding.get("minecraft_username");
                String email = (String) binding.get("email");
                
                if ((username != null && username.toLowerCase().contains(searchKeyword)) ||
                    (minecraftUsername != null && minecraftUsername.toLowerCase().contains(searchKeyword)) ||
                    (email != null && email.toLowerCase().contains(searchKeyword))) {
                    results.add(binding);
                }
            }
            

            return results;
            
        } catch (Exception e) {

            return new java.util.ArrayList<>();
        }
    }
    
    
    public static Map<String, Object> getBindingByUsername(String username) {
        try {
            List<Map<String, Object>> allBindings = getAllBindings();
            
            for (Map<String, Object> binding : allBindings) {
                String bindingUsername = (String) binding.get("username");
                if (username.equals(bindingUsername)) {
                    return binding;
                }
            }
            
            return null;
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static Map<String, Object> getBindingByMinecraftUsername(String minecraftUsername) {
        try {
            List<Map<String, Object>> allBindings = getAllBindings();
            
            for (Map<String, Object> binding : allBindings) {
                String bindingMinecraftUsername = (String) binding.get("minecraft_username");
                if (minecraftUsername.equals(bindingMinecraftUsername)) {
                    return binding;
                }
            }
            
            return null;
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static boolean isMinecraftUsernameBound(String minecraftUsername) {
        String systemUser = UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftUsername);
        return systemUser != null;
    }
    
    
    public static boolean isUserBound(String username) {
        String minecraftUsername = UserMinecraftBinding.getMinecraftUsernameByUser(username);
        return minecraftUsername != null;
    }
    
    
    public static Map<String, Object> getStatistics() {
        try {

            return UserMinecraftBinding.getBindingStats();
        } catch (Exception e) {

            return new java.util.HashMap<>();
        }
    }
    
    
    private static boolean isValidMinecraftUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        

        if (username.length() < 3 || username.length() > 16) {
            return false;
        }
        

        return username.matches("^[a-zA-Z0-9_]+$");
    }
    
    
    public static String getMinecraftUsername(String username) {
        return UserMinecraftBinding.getMinecraftUsernameByUser(username);
    }
    
    
    public static String getSystemUsername(String minecraftUsername) {
        return UserMinecraftBinding.getSystemUserByMinecraftUsername(minecraftUsername);
    }
} 
