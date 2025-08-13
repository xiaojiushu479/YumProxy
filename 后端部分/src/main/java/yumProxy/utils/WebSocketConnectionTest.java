package yumProxy.utils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;


public class WebSocketConnectionTest {
    
    private static final String[] TEST_URLS = {
        "ws:
        "wss:
        "ws:
        "wss:
        "ws:
    };
    
    public static void main(String[] args) {


        
        for (int i = 0; i < TEST_URLS.length; i++) {

        }
        

        
        for (String url : TEST_URLS) {
            testConnection(url);
            

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
        }
        

    }
    
    private static void testConnection(String url) {

        
        try {
            URI serverUri = URI.create(url);
            WebSocketClient client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {



                    close();
                }
                
                @Override
                public void onMessage(String message) {

                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (code == 1000) {

                    } else {

                        
                        if (reason != null && reason.contains("301")) {

                        }
                    }
                }
                
                @Override
                public void onError(Exception ex) {

                    
                    if (ex.getMessage().contains("301")) {

                    } else if (ex.getMessage().contains("Connection refused")) {

                    }
                }
            };
            

            client.setConnectionLostTimeout(5);
            

            boolean connected = client.connectBlocking();
            
            if (!connected) {

            }
            
        } catch (Exception e) {

        }
        

    }
} 
