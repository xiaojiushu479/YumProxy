package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import yumProxy.utils.PortUtils;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    
    private static WebSocketServer instance;
    private static final Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private static final Map<String, String> clientInfo = new ConcurrentHashMap<>();
    
    public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }
    
    public static WebSocketServer getInstance() {
        if (instance == null) {
            int port = yumProxy.net.Config.ServiceConfig.getWebSocketPort();
            instance = new WebSocketServer(port);
        }
        return instance;
    }
    
    public static void startServer() {
        try {
            WebSocketServer server = getInstance();
            

            if (!PortUtils.checkAndWaitForPort(server.getPort())) {
                yumProxy.YumProxy.log("缂佹棏鍨拌ぐ?" + server.getPort() + " 濞寸姴绉堕崝褏鎮銏犵獥闁汇埄鐓夌槐婵嬪触椤栨艾袟濠㈡儼绮剧憴?);
                return;
            }
            
            server.start();
            yumProxy.YumProxy.log("WebSocket闁哄牆绉存慨鐔煎闯閵娿儲鍎欓柛鏂诲妽閸ㄦ岸宕濋悤鍌滅闁烩晜鍨甸幆澶岀博椤栨艾缍? " + server.getPort());
        } catch (Exception e) {
            yumProxy.YumProxy.log("WebSocket闁哄牆绉存慨鐔煎闯閵娿儲鍎欓柛鏂诲妼閵囨垹鎷? " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        clients.put(clientId, conn);
        clientInfo.put(clientId, "閺夆晝鍋炵敮鎾籍閸洘锛? " + System.currentTimeMillis());
        
        yumProxy.YumProxy.log("WebSocket閻庡箍鍨洪崺娑氱博椤栨繄绠鹃柟? " + clientId);
        

        JSONObject welcomeMsg = new JSONObject();
        welcomeMsg.put("type", "welcome");
        welcomeMsg.put("message", "婵炲棎鍨肩换瀣交閻愭潙澶嶉柛鎺旀珡umProxy WebSocket闁哄牆绉存慨鐔煎闯?);
        welcomeMsg.put("timestamp", System.currentTimeMillis());
        conn.send(welcomeMsg.toJSONString());
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        clients.remove(clientId);
        clientInfo.remove(clientId);
        
        yumProxy.YumProxy.log("WebSocket閻庡箍鍨洪崺娑氱博椤栨稒鐒界€殿喒鍋撻弶鈺冨仦鐢? " + clientId + ", 闁告鍠庡ú? " + reason);
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        

        if (!yumProxy.net.Config.ServiceConfig.isWebSocketMessageEnabled()) {
            yumProxy.YumProxy.log("WebSocket婵炴垵鐗婃导鍛緞閸曨厽鍊炵€规瓕灏欓々锕傛偨椤帞绀夐煫鍥╂櫕閺嗘劙寮堕妷銊ユ " + clientId + " 闁汇劌瀚粔鐑藉箒?);
            

            JSONObject disableMsg = new JSONObject();
            disableMsg.put("type", "service_disabled");
            disableMsg.put("message", "WebSocket婵炴垵鐗婃导鍛緞閸曨厽鍊炵€规瓕灏欓々锕傛偨?);
            disableMsg.put("timestamp", System.currentTimeMillis());
            conn.send(disableMsg.toJSONString());
            return;
        }
        
        try {
            JSONObject jsonMessage = JSON.parseObject(message);
            String type = jsonMessage.getString("type");
            
            yumProxy.YumProxy.log("闁衡偓鐠哄搫鐓傞柡澶堝劥閸?" + clientId + " 闁汇劌瀚粔鐑藉箒? " + type);
            
            switch (type) {
                case "ping":
                    handlePing(conn);
                    break;
                case "echo":
                    handleEcho(conn, jsonMessage);
                    break;
                case "broadcast":
                    handleBroadcast(conn, jsonMessage);
                    break;
                case "get_clients":
                    handleGetClients(conn);
                    break;
                case "private_message":
                    handlePrivateMessage(conn, jsonMessage);
                    break;
                case "api_request":
                    WebSocketApiHandler.handleApiRequest(conn, message);
                    break;
                default:
                    handleUnknownMessage(conn, jsonMessage);
                    break;
            }
        } catch (Exception e) {
            yumProxy.YumProxy.log("濠㈣泛瀚幃濂bSocket婵炴垵鐗婃导鍛村籍鐠哄搫姣夐梺? " + e.getMessage());
            

            JSONObject errorMsg = new JSONObject();
            errorMsg.put("type", "error");
            errorMsg.put("message", "婵炴垵鐗婃导鍛村冀閻撳海纭€闂佹寧鐟ㄩ銈夊箣閺嵮屾П闁荤偛妫楅妵鎴犳嫻?);
            errorMsg.put("original_message", message);
            conn.send(errorMsg.toJSONString());
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

        yumProxy.YumProxy.log("闁衡偓鐠哄搫鐓傚ù婊冪焷缁绘﹢宕氶懜鐢敌ラ柟顓у灲缁辨繈姊归崹顔碱唺: " + message.remaining());
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn != null) {
            String clientId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
            yumProxy.YumProxy.log("WebSocket閻庡箍鍨洪崺娑氱博?" + clientId + " 闁告瑦鍨归弫鎾绘煥濞嗘帩鍤? " + ex.getMessage());
        } else {
            yumProxy.YumProxy.log("WebSocket闁哄牆绉存慨鐔煎闯閵娿儱绲洪柣銏㈠枛閺佸﹦鎷? " + ex.getMessage());
        }
        ex.printStackTrace();
    }
    
    @Override
    public void onStart() {
        yumProxy.YumProxy.log("WebSocket闁哄牆绉存慨鐔煎闯閵娿儲鍎欓柛鏂诲妼閻ｎ剟骞?);
    }
    

    private void handlePing(WebSocket conn) {
        JSONObject response = new JSONObject();
        response.put("type", "pong");
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    

    private void handleEcho(WebSocket conn, JSONObject message) {
        JSONObject response = new JSONObject();
        response.put("type", "echo");
        response.put("data", message.get("data"));
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    

    private void handleBroadcast(WebSocket conn, JSONObject message) {
        String senderId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        
        JSONObject broadcastMsg = new JSONObject();
        broadcastMsg.put("type", "broadcast");
        broadcastMsg.put("sender", senderId);
        broadcastMsg.put("data", message.get("data"));
        broadcastMsg.put("timestamp", System.currentTimeMillis());
        
        String broadcastJson = broadcastMsg.toJSONString();
        

        for (WebSocket client : clients.values()) {
            if (client != conn) {
                client.send(broadcastJson);
            }
        }
        

        JSONObject confirmMsg = new JSONObject();
        confirmMsg.put("type", "broadcast_sent");
        confirmMsg.put("recipients", clients.size() - 1);
        confirmMsg.put("timestamp", System.currentTimeMillis());
        conn.send(confirmMsg.toJSONString());
    }
    

    private void handleGetClients(WebSocket conn) {
        JSONObject response = new JSONObject();
        response.put("type", "clients_list");
        response.put("total_clients", clients.size());
        
        JSONObject clientsInfo = new JSONObject();
        for (Map.Entry<String, String> entry : clientInfo.entrySet()) {
            clientsInfo.put(entry.getKey(), entry.getValue());
        }
        response.put("clients", clientsInfo);
        response.put("timestamp", System.currentTimeMillis());
        
        conn.send(response.toJSONString());
    }
    

    private void handlePrivateMessage(WebSocket conn, JSONObject message) {
        String targetId = message.getString("target");
        String data = message.getString("data");
        String senderId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
        
        WebSocket targetClient = clients.get(targetId);
        if (targetClient != null) {
            JSONObject privateMsg = new JSONObject();
            privateMsg.put("type", "private_message");
            privateMsg.put("sender", senderId);
            privateMsg.put("data", data);
            privateMsg.put("timestamp", System.currentTimeMillis());
            
            targetClient.send(privateMsg.toJSONString());
            

            JSONObject confirmMsg = new JSONObject();
            confirmMsg.put("type", "private_message_sent");
            confirmMsg.put("target", targetId);
            confirmMsg.put("timestamp", System.currentTimeMillis());
            conn.send(confirmMsg.toJSONString());
        } else {

            JSONObject errorMsg = new JSONObject();
            errorMsg.put("type", "error");
            errorMsg.put("message", "闁烩晩鍠楅悥锝団偓骞垮灪閸╂稓绮╅娆戠憹閻庢稒锚濠€? " + targetId);
            conn.send(errorMsg.toJSONString());
        }
    }
    

    private void handleUnknownMessage(WebSocket conn, JSONObject message) {
        JSONObject response = new JSONObject();
        response.put("type", "unknown_message");
        response.put("message", "闁哄牜浜為悡锟犳儍閸曨剛啸闁诡収鍨崇悮顐﹀垂? " + message.getString("type"));
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }
    

    public static void broadcastToAll(String message) {
        for (WebSocket client : clients.values()) {
            client.send(message);
        }
    }
    

    public static void broadcastToAll(JSONObject message) {
        String jsonMessage = message.toJSONString();
        for (WebSocket client : clients.values()) {
            client.send(jsonMessage);
        }
    }
    

    public static int getClientCount() {
        return clients.size();
    }
    

    public static Map<String, String> getAllClients() {
        return new HashMap<>(clientInfo);
    }
} 
