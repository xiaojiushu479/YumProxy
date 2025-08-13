package yumProxy.utils;

import yumProxy.net.mysql.MySQLUtils;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.server.timestamp.TimestampInfo;
import java.util.List;
import java.util.Map;


public class UserMinecraftBinding {
    
    
    public static void initUserMinecraftBinding() {
        try {

            

            if (!hasMinecraftUsernameColumn()) {

                String alterSql = "ALTER TABLE `users` ADD COLUMN `minecraft_username` VARCHAR(64) DEFAULT NULL UNIQUE";
                MySQLUtils.executeUpdate(alterSql);

            } else {

            }
            

            addMinecraftUsernameIndex();
            

            
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    
    private static boolean hasMinecraftUsernameColumn() {
        try {
            List<Map<String, Object>> columns = MySQLUtils.getTableColumns("users");
            for (Map<String, Object> column : columns) {
                String columnName = String.valueOf(column.get("name"));
                if ("minecraft_username".equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {

            return false;
        }
    }
    
    
    private static void addMinecraftUsernameIndex() {
        try {
            String indexSql = "ALTER TABLE `users` ADD INDEX `idx_minecraft_username` (`minecraft_username`)";
            MySQLUtils.executeUpdate(indexSql);

        } catch (Exception e) {


        }
    }
    
    
    public static BindingResult bindMinecraftUser(String username, String minecraftUsername) {
        try {

            

            if (username == null || username.trim().isEmpty()) {
                return BindingResult.error("闁?闁活潿鍔嶉崺娑㈠触瀹ュ嫮鐟濋柤铏灊鐠愮喓绮?);
            }
            if (minecraftUsername == null || minecraftUsername.trim().isEmpty()) {
                return BindingResult.error("闁?闁瑰瓨鍨瑰▓鎴炵▔閺嶎偅娅曢柣顫妽閸╂盯宕ュ鍕憹闁艰櫕鍨濈拹鐔虹矚?);
            }
            if (!isValidMinecraftUsername(minecraftUsername)) {
                return BindingResult.error("闁?闁瑰瓨鍨瑰▓鎴炵▔閺嶎偅娅曢柣顫妽閸╂盯宕ュ鍡欏鐎殿喖绻戝Λ銈夊极閸剛绀勯梻鈧崹顔碱唺3-16閻庢稒顨堥渚€鏁嶇仦鑲╃煂闁衡偓椤栨稑鐦悗娑欘殕閻︽繈寮弶璺ㄦ憻濞戞挸顑呴崹婵堢棯閸栵紕绀?);
            }
            

            if (!userExists(username)) {
                return BindingResult.error("闁?缂侇垵宕电划娲偨閵婏箑鐓曞☉鎾崇Т閻°劑宕烽…鎺旂獥" + username);
            }
            

            TimestampInfo timestampInfo = TimestampManager.getPlayerTimestamp(username);
            if (timestampInfo == null) {
                return BindingResult.error("闁?闁诡喓鍔忕换鏇㈠嫉椤忓洤鏋犲☉鏃傚濡炲倿姊绘潏鍓х閻犲洤鍢查崢娑氭嫻椤撴繃瀚抽柡鍐ㄧ埣濡潡骞嬮崘鍙夊€甸柛鎰Ф缁妇鈧纰嶉崹婊堟儍閸曨亞鐟柣锝呯灱閺併倝骞嬪畡鐗堝€?);
            }
            
            if (timestampInfo.isExpired || !timestampInfo.isActive) {
                if (timestampInfo.isExpired) {
                    return BindingResult.error("闁?闁诡喓鍔庡▓鎴﹀籍閸洘锛熼柟鏉戝暱閸戔剝娼婚崶銊﹀焸闁挎稑鐭侀顒傜磼椤撯€崇€柛姘閸熲偓缂備焦鍨甸悾楣冨箣閹寸姵鐣卞☉鎾寸墱閺咁偊鎮介妸锕€鐓曢柛?);
                } else {
                    return BindingResult.error("闁?闁诡喓鍔庡▓鎴﹀籍閸洘锛熼柟纾嬫珪濠€顓炩攽閳ь剙煤娴兼瑧绀夐悹鍥╂焿閸犳ɑ绋婇悧鍫燁槯闂傚倹娼欓幃妤呭礃瀹ュ洨鎷ㄩ悗瑙勭閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐬奸弫銈夊箣瀹勭増鍊?);
                }
            }
            

                             " (婵犵鍋撴繛? " + timestampInfo.isActive + ", 閺夆晛娲﹀﹢? " + timestampInfo.isExpired + 
                             ", 闁告帞澧楀﹢锟犲籍閸洘锛? " + timestampInfo.expiresAt + ")");
            

            String existingUser = getSystemUserByMinecraftUsername(minecraftUsername);
            if (existingUser != null) {
                if (existingUser.equals(username)) {
                    return BindingResult.error("妫ｅ啯鏅?婵縿鍊栭崹婊堟儍閸曨亞鐟柣锝呯灱閺併倝骞嬪畡鐗堝€崇€规瓕灏欑划锔锯偓瑙勮壘閸╁矂骞冮妸褎鐣遍悹鎰堕檮閸?);
                } else {
                    return BindingResult.error("妫ｅ啯鐦?婵縿鍊栭崹婊堟儍閸曨亞鐟柣锝呯灱閺併倝骞嬪畡鐗堝€崇€规瓕灏～锕傚礂閺堢數閾傞柣顫妽閸╂稓绱掗幋婵堟毎");
                }
            }
            

            String existingMcUser = getMinecraftUsernameByUser(username);
            if (existingMcUser != null) {
                return BindingResult.error("妫ｅ啯鏅?闁诡喓鍔岄崙锛勭磼閹存繄鏆伴柟瀛樺灩濞堟垶绋夐弽顐ｆ珪闁活潿鍔嶉崺娑㈠触瀹ュ繒绐? + existingMcUser + "闁挎稑鐭侀顒勫礂閸及鎺旂磼閹存繃鍊甸柛鎰Ф缁妇鈧纰嶉弻濠囨儍?);
            }
            

            String updateSql = "UPDATE `users` SET `minecraft_username` = ? WHERE `username` = ?";
            int result = MySQLUtils.executeUpdate(updateSql, minecraftUsername, username);
            
            if (result > 0) {

                return BindingResult.success("闁?缂備焦鍨甸悾楣冨箣閹邦剙顫犻柨娑楃劍閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐬奸弫銈夊箣瀹勭増鍊?" + minecraftUsername + " 鐎规瓕灏欑划锔锯偓瑙勮壘閸╁矂骞冮妸褎鐣遍悹鎰堕檮閸?);
            } else {
                return BindingResult.error("闁?缂備焦鍨甸悾鐐緞鏉堫偉袝闁挎稑鏈弳鐔煎箲椤旇偐姘ㄩ柟鍨С缂嶆柨顕ｉ崒姘卞煑");
            }
            
        } catch (Exception e) {

            return BindingResult.error("闁?缂侇垵宕电划娲煥濞嗘帩鍤栭柨娑樼灱缁妇鈧鑹鹃妵鎴犳嫻閵夘垳绐? + e.getMessage());
        }
    }
    
    
    public static BindingResult unbindMinecraftUser(String username) {
        try {

            

            if (username == null || username.trim().isEmpty()) {
                return BindingResult.error("闁?闁活潿鍔嶉崺娑㈠触瀹ュ嫮鐟濋柤铏灊鐠愮喓绮?);
            }
            

            if (!userExists(username)) {
                return BindingResult.error("闁?缂侇垵宕电划娲偨閵婏箑鐓曞☉鎾崇Т閻°劑宕烽…鎺旂獥" + username);
            }
            

            String minecraftUsername = getMinecraftUsernameByUser(username);
            if (minecraftUsername == null) {
                return BindingResult.error("妫ｅ啯鏅?闁诡喓鍔忕换鏇㈠嫉椤忓棛鎷ㄩ悗瑙勭煯閹广垺鎷呴弴鐔风亯闁汇劌瀚粭姗€鎮惧畝鈧弫銈夊箣瀹勭増鍊?);
            }
            

            String updateSql = "UPDATE `users` SET `minecraft_username` = NULL WHERE `username` = ?";
            int result = MySQLUtils.executeUpdate(updateSql, username);
            
            if (result > 0) {

                return BindingResult.success("闁?閻熸瑱绲跨划锕傚箣閹邦剙顫犻柨娑楃劍閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐬奸弫銈夊箣瀹勭増鍊?" + minecraftUsername + " 鐎规瓕寮撶划鐘诲箖閵娧勭暠閻犳劧闄勯崺娑氭喆閿濆洨鎷?);
            } else {
                return BindingResult.error("闁?閻熸瑱绲跨划锔藉緞鏉堫偉袝闁挎稑鏈弳鐔煎箲椤旇偐姘ㄩ柟鍨С缂嶆柨顕ｉ崒姘卞煑");
            }
            
        } catch (Exception e) {

            return BindingResult.error("闁?缂侇垵宕电划娲煥濞嗘帩鍤栭柨娑樼焷琚欑紓浣瑰灥閵囨垹鎷归妷顖滅獥" + e.getMessage());
        }
    }
    
    
    public static boolean validateMinecraftUser(String minecraftUsername) {
        try {
            if (minecraftUsername == null || minecraftUsername.trim().isEmpty()) {
                return false;
            }
            
            String systemUser = getSystemUserByMinecraftUsername(minecraftUsername);
            if (systemUser == null) {

                return false;
            }
            

            TimestampInfo timestampInfo = TimestampManager.getPlayerTimestamp(systemUser);
            if (timestampInfo == null) {

                                 " -> 缂備焦鍨甸悾楣冩偨閵婏箑鐓?" + systemUser + " 闁哄牜浜濈缓鍝劽虹紒妯活槯闂傚倸鐡ㄩ崺?);
                return false;
            }
            
            if (timestampInfo.isExpired || !timestampInfo.isActive) {
                String statusMsg = timestampInfo.isExpired ? "鐎规瓕灏换鍐嫉? : "闁哄牜浜濈缓鍝劽?;

                                 " -> 缂備焦鍨甸悾楣冩偨閵婏箑鐓?" + systemUser + " 闁哄啫鐖煎Λ鍧楀箣? + statusMsg + 
                                 " (闁告帞澧楀﹢锟犲籍閸洘锛? " + timestampInfo.expiresAt + ")");
                return false;
            }
            

                             " -> 缂備焦鍨甸悾楣冨礆閹殿喗鏆忛柟? " + systemUser + 
                             " (闁告帞澧楀﹢锟犲籍閸洘锛? " + timestampInfo.expiresAt + ")");
            
            return true;
            
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public static String getSystemUserByMinecraftUsername(String minecraftUsername) {
        try {
            String sql = "SELECT `username` FROM `users` WHERE `minecraft_username` = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, minecraftUsername);
            
            if (!results.isEmpty()) {
                return (String) results.get(0).get("username");
            }
            
            return null;
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static String getMinecraftUsernameByUser(String username) {
        try {
            String sql = "SELECT `minecraft_username` FROM `users` WHERE `username` = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            
            if (!results.isEmpty()) {
                return (String) results.get(0).get("minecraft_username");
            }
            
            return null;
        } catch (Exception e) {

            return null;
        }
    }
    
    
    public static List<Map<String, Object>> getAllBindings() {
        try {
            String sql = "SELECT `username`, `minecraft_username`, `email`, `rank`, `pid`, `create_time` " +
                        "FROM `users` WHERE `minecraft_username` IS NOT NULL ORDER BY `create_time` DESC";
            return MySQLUtils.executeQuery(sql);
        } catch (Exception e) {

            return new java.util.ArrayList<>();
        }
    }
    
    
    public static List<Map<String, Object>> searchBindings(String keyword) {
        try {
            String sql = "SELECT `username`, `minecraft_username`, `email`, `rank`, `pid`, `create_time` " +
                        "FROM `users` WHERE `minecraft_username` IS NOT NULL AND " +
                        "(`username` LIKE ? OR `minecraft_username` LIKE ? OR `email` LIKE ?) " +
                        "ORDER BY `create_time` DESC";
            String searchPattern = "%" + keyword + "%";
            return MySQLUtils.executeQuery(sql, searchPattern, searchPattern, searchPattern);
        } catch (Exception e) {

            return new java.util.ArrayList<>();
        }
    }
    
    
    public static Map<String, Object> getBindingStats() {
        try {
            Map<String, Object> stats = new java.util.HashMap<>();
            

            String totalUsersSql = "SELECT COUNT(*) as count FROM `users`";
            List<Map<String, Object>> totalResults = MySQLUtils.executeQuery(totalUsersSql);
            if (!totalResults.isEmpty()) {
                stats.put("total_users", totalResults.get(0).get("count"));
            }
            

            String boundUsersSql = "SELECT COUNT(*) as count FROM `users` WHERE `minecraft_username` IS NOT NULL";
            List<Map<String, Object>> boundResults = MySQLUtils.executeQuery(boundUsersSql);
            if (!boundResults.isEmpty()) {
                stats.put("bound_users", boundResults.get(0).get("count"));
            }
            

            String unboundUsersSql = "SELECT COUNT(*) as count FROM `users` WHERE `minecraft_username` IS NULL";
            List<Map<String, Object>> unboundResults = MySQLUtils.executeQuery(unboundUsersSql);
            if (!unboundResults.isEmpty()) {
                stats.put("unbound_users", unboundResults.get(0).get("count"));
            }
            
            return stats;
        } catch (Exception e) {

            return new java.util.HashMap<>();
        }
    }
    
    
    private static boolean userExists(String username) {
        try {
            String sql = "SELECT COUNT(*) as count FROM `users` WHERE `username` = ?";
            List<Map<String, Object>> results = MySQLUtils.executeQuery(sql, username);
            if (!results.isEmpty()) {
                Long count = (Long) results.get(0).get("count");
                return count > 0;
            }
            return false;
        } catch (Exception e) {

            return false;
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
    
    
    public static class BindingResult {
        private boolean success;
        private String message;
        
        private BindingResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static BindingResult success(String message) {
            return new BindingResult(true, message);
        }
        
        public static BindingResult error(String message) {
            return new BindingResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return (success ? "闁瑰瓨鍔曟慨? : "濠㈡儼绮剧憴?) + ": " + message;
        }
    }
    
    
    public static WhitelistStatus checkUserWhitelistStatus(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return new WhitelistStatus(false, "闁?闁活潿鍔嶉崺娑㈠触瀹ュ嫯绀嬬紒?, null, null);
            }
            

            if (!userExists(username)) {
                return new WhitelistStatus(false, "闁?闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛?, null, null);
            }
            

            String minecraftUsername = getMinecraftUsernameByUser(username);
            if (minecraftUsername == null) {
                return new WhitelistStatus(false, "妫ｅ啯鏅?闁诡喓鍔忕换鏇㈠嫉椤忓棛鎷ㄩ悗瑙勭閸ㄦ粓鎯冮崟顏嗙懐闁伙絽鐬奸弫銈夊箣瀹勭増鍊?, null, null);
            }
            

            TimestampInfo timestampInfo = TimestampManager.getPlayerTimestamp(username);
            if (timestampInfo == null) {
                return new WhitelistStatus(false, "闁?闁诡喓鍔忕换鏇㈠嫉椤忓洤鏋犲☉鏃傚濡炲倿姊绘潏鍓х闁谎嗘閹洟宕￠弴鐔革骏闁?, minecraftUsername, null);
            }
            
            if (timestampInfo.isExpired || !timestampInfo.isActive) {
                if (timestampInfo.isExpired) {
                    return new WhitelistStatus(false, "闁?闁诡喓鍔庡▓鎴﹀籍閸洘锛熼柟鏉戝暱閸戔剝娼婚崶銊﹀焸闁挎稑鐬煎▍褔宕ュ鍛鐎瑰憡褰冮妵鎴﹀极?, minecraftUsername, timestampInfo);
                } else {
                    return new WhitelistStatus(false, "闁?闁诡喓鍔庡▓鎴﹀籍閸洘锛熼柟纾嬫珪濠€顓炩攽閳ь剙煤娴兼瑧绀夐柣褑妫勯幃鏇㈠础閺囩喐锟ラ柡?, minecraftUsername, timestampInfo);
                }
            }
            
            return new WhitelistStatus(true, "闁?闁谎嗘閹洟宕￠弴鐔哥畳闁轰礁鐗炵槐婵嬪矗椤栨瑤绨版慨婵撶到閻栬泛銆掗崨濠傜亞", minecraftUsername, timestampInfo);
            
        } catch (Exception e) {

            return new WhitelistStatus(false, "闁?缂侇垵宕电划娲煥濞嗘帩鍤栭柨? + e.getMessage(), null, null);
        }
    }
    
    
    public static WhitelistStatus checkMinecraftUserWhitelistStatus(String minecraftUsername) {
        try {
            if (minecraftUsername == null || minecraftUsername.trim().isEmpty()) {
                return new WhitelistStatus(false, "闁?闁瑰瓨鍨瑰▓鎴炵▔閺嶎偅娅曢柣顫妽閸╂盯宕ュ鍕缂?, null, null);
            }
            
            String systemUser = getSystemUserByMinecraftUsername(minecraftUsername);
            if (systemUser == null) {
                return new WhitelistStatus(false, "妫ｅ啯鏅?婵縿鍊栭崹婊堟儍閸曨亞鐟柣锝呯灱閺併倝骞嬪畡鐗堝€抽柡鍫簽缁妇鈧鑹鹃崺灞剧鐠佸磭绉块悹鎰堕檮閸?, minecraftUsername, null);
            }
            
            return checkUserWhitelistStatus(systemUser);
            
        } catch (Exception e) {

            return new WhitelistStatus(false, "闁?缂侇垵宕电划娲煥濞嗘帩鍤栭柨? + e.getMessage(), minecraftUsername, null);
        }
    }
    
    
    public static class WhitelistStatus {
        private boolean isValid;
        private String message;
        private String minecraftUsername;
        private TimestampInfo timestampInfo;
        
        public WhitelistStatus(boolean isValid, String message, String minecraftUsername, TimestampInfo timestampInfo) {
            this.isValid = isValid;
            this.message = message;
            this.minecraftUsername = minecraftUsername;
            this.timestampInfo = timestampInfo;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getMinecraftUsername() {
            return minecraftUsername;
        }
        
        public TimestampInfo getTimestampInfo() {
            return timestampInfo;
        }
        
        @Override
        public String toString() {
            return String.format("WhitelistStatus{isValid=%s, message='%s', minecraftUsername='%s', timestampInfo=%s}",
                    isValid, message, minecraftUsername, timestampInfo);
        }
    }
} 
