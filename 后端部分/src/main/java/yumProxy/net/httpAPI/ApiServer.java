package yumProxy.net.httpAPI;

import com.sun.net.httpserver.HttpServer;
import yumProxy.utils.PortUtils;

import java.net.InetSocketAddress;

public class ApiServer {
    public static void start() {
        try {
            int port = yumProxy.net.Config.ServiceConfig.getHttpPort();
            

            if (!PortUtils.checkAndWaitForPort(port)) {

                return;
            }
            

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/keyused", (exchange) -> CorsHandler.handleRequest(exchange, KeyApiServer::handleKeyUsed));
            server.createContext("/api/key/create", (exchange) -> CorsHandler.handleRequest(exchange, KeyApiServer::handleKeyCreate));
            server.createContext("/api/key/query", (exchange) -> CorsHandler.handleRequest(exchange, KeyApiServer::handleKeyQuery));
            server.createContext("/api/key/delete", (exchange) -> CorsHandler.handleRequest(exchange, KeyApiServer::handleKeyDelete));
            server.createContext("/api/user", (exchange) -> CorsHandler.handleRequest(exchange, UserApiServer::handleUser));
            server.createContext("/api/email/send", (exchange) -> CorsHandler.handleRequest(exchange, EmailApiServer::handleSend));
            server.createContext("/api/email/verify", (exchange) -> CorsHandler.handleRequest(exchange, EmailApiServer::handleVerify));
            server.createContext("/api/timestamp", (exchange) -> CorsHandler.handleRequest(exchange, TimestampApiServer::handleTimestamp));
            server.createContext("/api/timestamp/user", (exchange) -> CorsHandler.handleRequest(exchange, TimestampApiServer::handleUserTimestamp));
    
            server.createContext("/api/bill/query", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handleBillQuery));
            server.createContext("/api/bill/get", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handleBillGet));
            server.createContext("/api/bill/list", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handleBillList));
            server.createContext("/api/merchant/info", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handleMerchantInfo));
            server.createContext("/api/pay/create", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handlePayCreate));
            server.createContext("/api/pay/notify", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handlePayNotify));
            server.createContext("/api/pay/return", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handlePayReturn));
            server.createContext("/api/pay/form", (exchange) -> CorsHandler.handleRequest(exchange, PayApiServer::handlePayForm));
            

            server.createContext("/api/whitelist/validate", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleValidate));
            server.createContext("/api/whitelist/add", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleAdd));
            server.createContext("/api/whitelist/remove", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleRemove));

            server.createContext("/api/whitelist/search", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleSearch));
            server.createContext("/api/whitelist/stats", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleStats));
            server.createContext("/api/whitelist/health", (exchange) -> CorsHandler.handleRequest(exchange, WhitelistApiServer::handleHealth));
            
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    

} 
