package yumProxy.net.Config;

import com.sun.net.httpserver.HttpServer;
import yumProxy.utils.PortUtils;

import java.net.InetSocketAddress;

public class ConfigSettingServer {
    public static void start() {
        try {
            int port = yumProxy.net.Config.ServiceConfig.getConfigPort();
            

            if (!PortUtils.checkAndWaitForPort(port)) {

                return;
            }
            

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/config/smtp", ex -> {
                try {
                    if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
                        ConfigServer.handleSmtpGet(ex);
                    } else if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                        ConfigServer.handleSmtpPost(ex);
                    } else {
                        ex.sendResponseHeaders(405, -1);
                    }
                } catch (Exception e) {
                    ex.sendResponseHeaders(500, -1);
                }
            });
            server.createContext("/config/setting", ex -> {
                try {
                    if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
                        ConfigServer.handleConfigPage(ex);
                    } else if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                        ConfigServer.handleConfigSettingPost(ex);
                    } else {
                        ex.sendResponseHeaders(405, -1);
                    }
                } catch (Exception e) {
                    ex.sendResponseHeaders(500, -1);
                }
            });
            
                                server.createContext("/api/config/reset", ex -> {
                        try {
                            if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                                ConfigServer.handleConfigReset(ex);
                            } else {
                                ex.sendResponseHeaders(405, -1);
                            }
                        } catch (Exception e) {
                            ex.sendResponseHeaders(500, -1);
                        }
                    });
                    
                    server.createContext("/api/config/restart", ex -> {
                        try {
                            if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                                ConfigServer.handleConfigRestart(ex);
                            } else {
                                ex.sendResponseHeaders(405, -1);
                            }
                        } catch (Exception e) {
                            ex.sendResponseHeaders(500, -1);
                        }
                    });
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {

        }
    }
    

} 
