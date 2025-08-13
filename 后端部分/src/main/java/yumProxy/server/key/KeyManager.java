package yumProxy.server.key;

import yumProxy.net.mysql.MySQLUtils;
import java.security.SecureRandom;
import java.util.*;

public class KeyManager {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();


    private final Map<String, Key> keyMap = new HashMap<>();

    public KeyManager() {

        if (!MySQLUtils.tableExists("keys")) {
            String createKeysTable = "CREATE TABLE IF NOT EXISTS `keys` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "prefix VARCHAR(32) NOT NULL," +
                    "code VARCHAR(64) NOT NULL UNIQUE," +
                    "time_hours INT DEFAULT 0," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status INT DEFAULT 0)";
            MySQLUtils.executeUpdate(createKeysTable);

        } else {


            try {
                String checkColumnSql = "SELECT COUNT(*) AS cnt FROM information_schema.COLUMNS WHERE TABLE_NAME = 'keys' AND COLUMN_NAME = 'time_hours'";
                List<Map<String, Object>> result = MySQLUtils.executeQuery(checkColumnSql);
                long count = ((Number) result.get(0).get("cnt")).longValue();
                if (count == 0) {

                    String addColumnSql = "ALTER TABLE `keys` ADD COLUMN time_hours INT DEFAULT 0";
                    MySQLUtils.executeUpdate(addColumnSql);

                } else {

                }
            } catch (Exception e) {

            }
        }
        if (!MySQLUtils.tableExists("used_keys")) {
            String createUsedKeysTable = "CREATE TABLE IF NOT EXISTS `used_keys` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "prefix VARCHAR(32) NOT NULL," +
                    "code VARCHAR(64) NOT NULL UNIQUE," +
                    "used_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            MySQLUtils.executeUpdate(createUsedKeysTable);

        } else {

        }
    }

    private void log(String msg) {

    }

    
    public Key createKey(String prefix) {
        return createKey(prefix, 0);
    }

    
    public Key createKey(String prefix, int timeHours) {
        String code = generateCode();
        Key key = new Key(prefix, code, timeHours);
        keyMap.put(key.toString(), key);
        insertKeyToDB(key);

        return key;
    }

    
    public boolean deleteKey(String fullKey) {
        Key key = keyMap.remove(fullKey);
        boolean dbResult = false;
        if (key != null) {
            dbResult = deleteKeyFromDB(key);
        }

        return dbResult;
    }

    
    public List<Key> distributeKeys(String prefix, int count) {
        return distributeKeys(prefix, count, 0);
    }

    
    public List<Key> distributeKeys(String prefix, int count, int timeHours) {
        List<Key> keys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            keys.add(createKey(prefix, timeHours));
        }

        return keys;
    }

    
    private String generateCode() {
        StringBuilder sb = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    
    public Collection<Key> getAllKeys() {
        return keyMap.values();
    }

    
    private void insertKeyToDB(Key key) {
        String sql = "INSERT INTO `keys` (prefix, code, time_hours) VALUES (?, ?, ?)";
        MySQLUtils.executeUpdate(sql, key.getPrefix(), key.getCode(), key.getTime());

    }

    
    private boolean deleteKeyFromDB(Key key) {
        String sql = "DELETE FROM `keys` WHERE prefix = ? AND code = ?";
        boolean result = MySQLUtils.executeUpdate(sql, key.getPrefix(), key.getCode()) > 0;

        return result;
    }
}
