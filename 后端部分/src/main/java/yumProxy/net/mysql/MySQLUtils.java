package yumProxy.net.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MySQLUtils {
    private static MySQL mysql = MySQL.getInstance();
    private static final int MAX_RETRY_ATTEMPTS = 3;

    
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Connection conn = mysql.getConnection();
                if (conn == null) {

                    return results;
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    try (ResultSet rs = pstmt.executeQuery()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        while (rs.next()) {
                            Map<String, Object> row = new HashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = metaData.getColumnName(i);
                                Object value = rs.getObject(i);
                                row.put(columnName, value);
                            }
                            results.add(row);
                        }
                    }
                }
                return results;
            } catch (SQLException e) {

                

                if (isConnectionError(e) && attempt < MAX_RETRY_ATTEMPTS) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }
                
                if (attempt == MAX_RETRY_ATTEMPTS) {

                    e.printStackTrace();
                }
                break;
            }
        }
        return results;
    }

    
    public static int executeUpdate(String sql, Object... params) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Connection conn = mysql.getConnection();
                if (conn == null) {

                    return -1;
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    return pstmt.executeUpdate();
                }
                
            } catch (SQLException e) {

                

                if (isConnectionError(e) && attempt < MAX_RETRY_ATTEMPTS) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }
                
                if (attempt == MAX_RETRY_ATTEMPTS) {

                    e.printStackTrace();
                }
                break;
            }
        }
        return -1;
    }

    
    public static long executeInsert(String sql, Object... params) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Connection conn = mysql.getConnection();
                if (conn == null) {

                    return -1;
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet rs = pstmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                return rs.getLong(1);
                            }
                        }
                    }
                }
                
            } catch (SQLException e) {

                

                if (isConnectionError(e) && attempt < MAX_RETRY_ATTEMPTS) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }
                
                if (attempt == MAX_RETRY_ATTEMPTS) {

                    e.printStackTrace();
                }
                break;
            }
        }
        return -1;
    }

    
    public static int[] executeBatch(String sql, List<Object[]> paramsList) {
        Connection conn = mysql.getConnection();
        if (conn == null) {

            return new int[0];
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] params : paramsList) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.addBatch();
            }
            return pstmt.executeBatch();
        } catch (SQLException e) {

            e.printStackTrace();
            return new int[0];
        }
    }

    
    public static boolean executeTransaction(List<SQLOperation> operations) {
        Connection conn = mysql.getConnection();
        if (conn == null) {

            return false;
        }
        try {
            conn.setAutoCommit(false);
            for (SQLOperation operation : operations) {
                operation.execute(conn);
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {

            }

            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {

            }
        }
    }

    
    public static boolean tableExists(String tableName) {
        Connection conn = mysql.getConnection();
        if (conn == null) {

            return false;
        }
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            return rs.next();
        } catch (SQLException e) {

            return false;
        }
    }

    
    public static List<Map<String, Object>> getTableColumns(String tableName) {
        List<Map<String, Object>> columns = new ArrayList<>();
        
        Connection conn = mysql.getConnection();
        if (conn == null) {

            return columns;
        }
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, null);

            while (rs.next()) {
                Map<String, Object> column = new HashMap<>();
                column.put("name", rs.getString("COLUMN_NAME"));
                column.put("type", rs.getString("TYPE_NAME"));
                column.put("size", rs.getInt("COLUMN_SIZE"));
                column.put("nullable", rs.getInt("NULLABLE"));
                column.put("default", rs.getString("COLUMN_DEF"));
                columns.add(column);
            }
        } catch (SQLException e) {

        }
        
        return columns;
    }

    
    public static boolean createTable(String tableName, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        return executeUpdate(sql) >= 0;
    }

    
    public static boolean dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        return executeUpdate(sql) >= 0;
    }

    
    public static long getCount(String tableName, String whereClause, Object... params) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + tableName);
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
        
        List<Map<String, Object>> results = executeQuery(sql.toString(), params);
        if (!results.isEmpty()) {
            Object count = results.get(0).get("COUNT(*)");
            return count instanceof Number ? ((Number) count).longValue() : 0;
        }
        return 0;
    }

    
    public static Map<String, Object> executeQueryWithPagination(String sql, int page, int pageSize, Object... params) {
        Map<String, Object> result = new HashMap<>();
        

        

        String paginatedSql = sql + " LIMIT " + pageSize + " OFFSET " + offset;
        

        List<Map<String, Object>> data = executeQuery(paginatedSql, params);
        result.put("data", data);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("total", data.size());
        
        return result;
    }

    
    public static boolean isConnected() {
        return mysql.isConnected();
    }

    
    public static boolean testConnection() {
        return mysql.testConnection();
    }

    
    public static String getDatabaseInfo() {
        return mysql.getDatabaseInfo();
    }

    
    public interface SQLOperation {
        void execute(Connection conn) throws SQLException;
    }

    
    private static boolean isConnectionError(SQLException e) {
        String message = e.getMessage().toLowerCase();
        return message.contains("communications") || 
               message.contains("connection") || 
               message.contains("timeout") ||
               message.contains("broken pipe") ||
               message.contains("wait_timeout") ||
               message.contains("connection is closed");
    }


    public static void createBillTable() {
        String sql = "CREATE TABLE IF NOT EXISTS bill (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "out_trade_no VARCHAR(64) NOT NULL," +
                "trade_no VARCHAR(64)," +
                "type VARCHAR(16)," +
                "name VARCHAR(128)," +
                "money VARCHAR(16)," +
                "status VARCHAR(32)," +
                "payurl TEXT," +
                "qrcode TEXT," +
                "param TEXT," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        executeUpdate(sql);
        }


        String sql = "INSERT INTO bill (out_trade_no, trade_no, type, name, money, status, payurl, qrcode, param) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql,
            bill.getOrDefault("out_trade_no", ""),
            bill.getOrDefault("trade_no", ""),
            bill.getOrDefault("type", ""),
            bill.getOrDefault("name", ""),
            bill.getOrDefault("money", ""),
            bill.getOrDefault("status", ""),
            bill.getOrDefault("payurl", ""),
            bill.getOrDefault("qrcode", ""),
            bill.getOrDefault("param", "")
        );
    }


        StringBuilder sql = new StringBuilder("SELECT * FROM bill WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (outTradeNo != null && !outTradeNo.isEmpty()) {
            sql.append(" AND out_trade_no = ?");
            params.add(outTradeNo);
    }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY id DESC");
        return executeQuery(sql.toString(), params.toArray());
    }


        StringBuilder sql = new StringBuilder("SELECT * FROM bill WHERE 1=1");
        List<Object> params = new ArrayList<>();
        

        sql.append(" AND (param LIKE ? OR out_trade_no LIKE ?)");
        params.add("%" + username + "%");
        params.add("%" + username + "%");
        
        if (outTradeNo != null && !outTradeNo.isEmpty()) {
            sql.append(" AND out_trade_no = ?");
            params.add(outTradeNo);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY id DESC");
        return executeQuery(sql.toString(), params.toArray());
    }
}
