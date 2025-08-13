package yumProxy;

import yumProxy.net.mysql.MySQL;
import yumProxy.server.key.KeyManager;
import yumProxy.server.user.UserManager;
import yumProxy.server.user.EmailManager;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.net.httpAPI.ApiServer;
import yumProxy.net.httpAPI.EmailApiServer;
import yumProxy.net.httpAPI.UserApiServer;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.net.Config.ConfigSettingServer;
import yumProxy.net.Config.ConfigManager;
import yumProxy.net.websocket.WebSocketServer;

public class YumProxy {


    public String name = "YumProxy";
    public String dev = "Empty";
    public String devQQ = "(redacted)";
    public String version = "0.1.1";
    public int versionID = 2;

    public MySQL sql;
    public KeyManager keyManager;
    public UserManager userManager;
    public EmailManager emailManager;
    public static YumProxy instance;

    public static void log(String msg) {

    }

    public static void main(String[] args) {
        YumProxy.instance = new YumProxy();


        MySQL mysql = MySQL.getInstance();
        mysql.connect();
        if (!mysql.isConnected()) {

            return;
        }

        ConfigManager.initConfig(YumProxy.instance);


        YumProxy.instance.keyManager = new KeyManager();


        YumProxy.instance.userManager = UserApiServer.getUserManager();
        YumProxy.instance.userManager.setEmailManager(YumProxy.instance.emailManager);
        EmailApiServer.emailManager = YumProxy.instance.emailManager;


        TimestampManager.initTable();

        

        yumProxy.server.user.UserBanManager.initBanSystem();

        


        yumProxy.utils.DatabaseFixer.fixAllTables();




        yumProxy.server.user.UserKeyManager.generateKeysForAllUsers();
        

        yumProxy.net.Config.ServiceConfig.printConfig();
        

        if (yumProxy.net.Config.ServiceConfig.isHttpEnabled()) {

            ApiServer.start();
        } else {

        }
        
        MySQLUtils.createBillTable();
        


        ConfigSettingServer.start();
        
        if (yumProxy.net.Config.ServiceConfig.isWebSocketEnabled()) {

            WebSocketServer.startServer();
        } else {

        }
        

        yumProxy.net.websocket.EmailWebSocketApi.setEmailManager(YumProxy.instance.emailManager);

    }
}
