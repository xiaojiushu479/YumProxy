package yumProxy.net.mysql;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class MySQL {
    private static MySQL instance;
    private String url;
    private String username;
    private String password;
    private Connection connection;
    private boolean isConnected = false;


    private MySQL() {
        loadConfig();
    }

    public static MySQL getInstance() {
        if (instance == null) {
            instance = new MySQL();
        }
        return instance;
    }


    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", "jdbc:mysql:
                this.username = props.getProperty("db.username", "root");
                this.password = props.getProperty("db.password", "");
            } else {

            this.url = "jdbc:mysql:
            this.username = "your_db_user";
            this.password = "your_db_password";
            }
        } catch (IOException e) {


            this.url = "jdbc:mysql:
            this.username = "your_db_user";
            this.password = "your_db_password";
        }
    }

    public void connect() {
        if (isConnected && connection != null) {
            try {
                if (!connection.isClosed() && isValidConnection()) {

                    return;
                }
            } catch (SQLException e) {

            }
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(this.url, username, password);
            if (connection != null && !connection.isClosed()) {

                isConnected = true;
            }
        } catch (ClassNotFoundException e) {

            throw new RuntimeException("MySQL濡炵懓宕慨鈺呭嫉椤忓懎顥濋柛?, e);
        } catch (SQLException e) {

            throw new RuntimeException("闁轰胶澧楀畵浣规償閹惧湱绠鹃柟鎭掑劚閵囨垹鎷?, e);
        }
    }


    private boolean isValidConnection() {
        if (connection == null) {
            return false;
        }
        try {

                stmt.execute("SELECT 1");
                return true;
            }
        } catch (SQLException e) {

            return false;
        }
    }

    public Connection getConnection() {
        if (connection == null || !isConnected || !isValidConnection()) {

            connect();
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                isConnected = false;

            } catch (SQLException e) {

                throw new RuntimeException("闁稿繑濞婂Λ鎾极閻楀牆绁﹂幖瀛樻崄缁绘盯骞掗妷銉ｄ杭閻?, e);
            }
        }
    }

    public boolean isConnected() {
        if (connection == null) {
            return false;
        }
        try {
            return !connection.isClosed() && isValidConnection();
        } catch (SQLException e) {
            return false;
        }
    }


    public boolean testConnection() {
        try {
            connect();
            return isConnected();
        } catch (Exception e) {

            return false;
        }
    }


        if (!isConnected()) {
            return "闁轰胶澧楀畵浣规償閹惧瓨寮撻弶鈺冨仦鐢?;
        }
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            return String.format("闁轰胶澧楀畵浣规償? %s, 闁绘鐗婂﹢? %s", 
                metaData.getDatabaseProductName(), 
                metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            return "闁兼儳鍢茶ぐ鍥极閻楀牆绁﹂幖瀛樻尫娣囧﹪骞侀姘ヤ杭閻? " + e.getMessage();
        }
    }
}
