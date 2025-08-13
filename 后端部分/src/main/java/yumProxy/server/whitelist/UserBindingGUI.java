package yumProxy.server.whitelist;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import yumProxy.net.httpAPI.AuthValidator;


public class UserBindingGUI extends JFrame {
    

    private static final String[] WSS_URL_OPTIONS = {
        "ws:
        "wss:
        "ws:
        "wss:
        "ws:
    };
    private static String WSS_URL = WSS_URL_OPTIONS[0];
    private String userKey = "";
    

    private WebSocketClient webSocketClient;
    private final AtomicLong requestIdCounter = new AtomicLong(1);
    private final ConcurrentHashMap<String, PendingRequest> pendingRequests = new ConcurrentHashMap<>();
    

    private JTextField usernameField;
    private JTextField userKeyField;
    private JTable bindingTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel connectionLabel;
    
    public UserBindingGUI() {
        initializeGUI();
        connectWebSocket();
    }
    

    private static class PendingRequest {
        public final String action;
        public final long timestamp;
        
        public PendingRequest(String action) {
            this.action = action;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    private void initializeGUI() {
        setTitle("闁活潿鍔嶉崺?MC缂備焦鍨甸悾鍓х不閿涘嫭鍊炵€规悶鍎遍崣?- WSS闁绘鐗婂﹢?);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

            @Override
            public void windowClosing(WindowEvent e) {
                disconnectWebSocket();
                System.exit(0);
            }
        });
        

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        

        JPanel topPanel = new JPanel(new BorderLayout());
        

        connectionLabel = new JLabel("閺夆晝鍋炵敮鎾偐閼哥鍋? 婵繐绲藉﹢顏呮交閻愭潙澶?..");
        connectionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        statusPanel.add(connectionLabel);
        

        final JComboBox<String> urlComboBox = new JComboBox<String>(WSS_URL_OPTIONS);
        urlComboBox.setSelectedItem(WSS_URL);
        urlComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WSS_URL = (String) urlComboBox.getSelectedItem();
                logMessage("闁告帒娲﹀畷鏌ュ嫉瀹ュ懎顫ら柛锝冨妼濠€鎾锤閳? " + WSS_URL);
            }
        });
        statusPanel.add(urlComboBox);
        

        JButton connectButton = new JButton("闂佹彃绉甸弻濠冩交閻愭潙澶?);
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reconnectWebSocket();
            }
        });
        statusPanel.add(connectButton);
        
        JButton disconnectButton = new JButton("闁哄偆鍘肩槐鎴炴交閻愭潙澶?);
        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnectWebSocket();
            }
        });
        statusPanel.add(disconnectButton);
        

        JButton configButton = new JButton("閻犱礁澧介悿鍡欌偓闈涙閹?);
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showConfigDialog();
            }
        });
        statusPanel.add(configButton);
        
        topPanel.add(statusPanel, BorderLayout.NORTH);
        

        JPanel operationPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        operationPanel.setBorder(BorderFactory.createTitledBorder("缂備焦鍨甸悾楣冨箼瀹ュ嫮绋?));
        

        operationPanel.add(new JLabel("闁活潿鍔嶉崺娑氣偓闈涙閹?"));
        userKeyField = new JTextField();
        userKeyField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userKey = userKeyField.getText().trim();
                logMessage("闁活潿鍔嶉崺娑氣偓闈涙閹告粌顔忛崣澶嬬函闁?(闂傗偓閸喖顔? " + userKey.length() + ")");
            }
        });
        operationPanel.add(userKeyField);
        operationPanel.add(new JLabel("(闁活潿鍔嬬花鐞d/remove/query闁瑰灝绉崇紞?"));
        JButton testKeyButton = new JButton("婵炴潙顑堥惁顖溾偓闈涙閹?);
        testKeyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userKey = userKeyField.getText().trim();
                if (userKey.isEmpty()) {
                    showError("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查惁鎴︽煢?);
                } else {
                    logMessage("闁?闁活潿鍔嶉崺娑氣偓闈涙閹告粌顔忛懠棰濆晭缂傚喚鍣槐婵嬫⒐閸喖顔? " + userKey.length());
                }
            }
        });
        operationPanel.add(testKeyButton);
        

        usernameField = new JTextField();
        operationPanel.add(usernameField);
        
        JButton addButton = new JButton("婵烇綀顕ф慨鐐电磼閹存繄鏆?);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addBinding();
            }
        });
        operationPanel.add(addButton);
        
        JButton removeButton = new JButton("閻熸瑱缍佸▍搴ｇ磼閹存繄鏆?);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeBinding();
            }
        });
        operationPanel.add(removeButton);
        
        operationPanel.add(new JLabel("MC闁活潿鍔嶉崺娑㈠触?"));
        minecraftUsernameField = new JTextField();
        operationPanel.add(minecraftUsernameField);
        
        JButton validateButton = new JButton("濡ょ姴鐭侀惁澶愭儌閽樺鍊抽柛?);
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validateMinecraftUser(minecraftUsernameField.getText().trim());
            }
        });
        operationPanel.add(validateButton);
        
        JButton queryButton = new JButton("闁哄被鍎撮妤冪磼閹存繄鏆?);
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                queryBinding();
            }
        });
        operationPanel.add(queryButton);
        
        operationPanel.add(new JLabel("闁瑰吋绮庨崒銊╁礂閹惰姤鏆涢悹?"));
        searchField = new JTextField();
        operationPanel.add(searchField);
        
        JButton searchButton = new JButton("闁瑰吋绮庨崒?);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBindings();
            }
        });
        operationPanel.add(searchButton);
        
        JButton refreshButton = new JButton("闁告帡鏀遍弻濠囧礆濡ゅ嫨鈧?);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadBindingList();
            }
        });
        operationPanel.add(refreshButton);
        
        topPanel.add(operationPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        

        String[] columnNames = {"缂侇垵宕电划娲偨閵婏箑鐓曢柛?, "MC闁活潿鍔嶉崺娑㈠触?, "闂侇収鍠氶?, "缂佹稑顦辨?, "PID", "闁告帗绋戠紓鎾诲籍閸洘锛?};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bindingTable = new JTable(tableModel);
        bindingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bindingTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = bindingTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        usernameField.setText((String) tableModel.getValueAt(selectedRow, 0));
                        minecraftUsernameField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    }
                }
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(bindingTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 300));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("缂備焦鍨甸悾楣冨礂瀹曞洭鍏囬柛鎺擃殙閵?));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        

        logArea = new JTextArea(8, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃堝籍閵夈儳绠?));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);
        

        statusLabel = new JLabel("闁绘鍩栭埀? 閻忓繗浜崡?);
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        setSize(800, 700);
        setLocationRelativeTo(null);
        
        logMessage("闁活潿鍔嶉崺?MC缂備焦鍨甸悾鍓х不閿涘嫭鍊炵€规悶鍎遍崣鍨啅閹绘帗鍎欓柛?(WSS闁绘鐗婂﹢?");
    }
    

    private void connectWebSocket() {
        try {
            URI serverUri = URI.create(WSS_URL);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            connectionLabel.setText("閺夆晝鍋炵敮鎾偐閼哥鍋? 鐎规瓕灏换娑㈠箳?(" + WSS_URL + ")");
                            connectionLabel.setForeground(Color.GREEN);
                            logMessage("闁?WebSocket閺夆晝鍋炵敮鎾箣閹邦剙顫? " + WSS_URL);
                            setStatus("閺夆晝鍋炵敮鎾箣閹邦剙顫?);
                            loadBindingList();
                    });
                }
                
                @Override
                public void onMessage(String message) {
                    try {
                        JSONObject response = JSONObject.parseObject(message);
                        handleWebSocketMessage(response);
                    } catch (Exception e) {
                        logMessage("闁?閻熸瑱绲鹃悗钘夆槈閸喍绱栧鎯扮簿鐟? " + e.getMessage());
                    }
                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            connectionLabel.setText("閺夆晝鍋炵敮鎾偐閼哥鍋? 鐎圭寮堕弻鍥ь嚕閳?);
                            connectionLabel.setForeground(Color.RED);
                            

                            String detailedMessage = "闁?WebSocket閺夆晝鍋炵敮鎾棘椤撶偟纾?;
                            if (code == 1006) {
                                detailedMessage += " (閺夆晝鍋炵敮鏉戭嚕閸屾氨鍩楀☉鎿冨幗閺?";
                            } else if (code == 1002) {
                                detailedMessage += " (闁告绻楅鍛存煥濞嗘帩鍤?";
                            } else if (code != -1) {
                                detailedMessage += " (闂佹寧鐟ㄩ銈嗙閿濆洨鍨? " + code + ")";
                            }
                            
                            if (reason != null && !reason.isEmpty()) {
                                detailedMessage += " - " + reason;
                                

                                if (reason.contains("301") || reason.contains("Moved Permanently")) {
                                    detailedMessage += "\n妫ｅ啯瀵?鐎点倝缂氶? 鐟滅増鎸告晶鐕疪L閻炴凹鍋婇崳鍝モ偓瑙勮壘閹粓鏁嶅畝鍐惧殲閻忓繑绻嗛惁顖炲礆閸ャ劌搴婇柛?'ws:
                                }
                            }
                            
                            logMessage(detailedMessage);
                            setStatus("閺夆晝鍋炵敮鎾棘椤撶偟纾?);
                        }
                    });
                }
                
                @Override
                public void onError(Exception ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            connectionLabel.setText("閺夆晝鍋炵敮鎾偐閼哥鍋? 闂佹寧鐟ㄩ?);
                            connectionLabel.setForeground(Color.RED);
                            
                            String errorMessage = "闁?WebSocket闂佹寧鐟ㄩ? " + ex.getMessage();
                            

                            if (ex.getMessage().contains("301") || ex.getMessage().contains("Moved Permanently")) {
                                errorMessage += "\n妫ｅ啯瀵?鐎点倝缂氶? URL閻炴凹鍋婇崳鍝モ偓瑙勮壘閹粓鏁嶅畝鍐惧殲闂侇偄顦扮€?'ws:
                            } else if (ex.getMessage().contains("Connection refused")) {
                                errorMessage += "\n妫ｅ啯瀵?鐎点倝缂氶? 闁哄牆绉存慨鐔煎闯閵婏附寮撻柛婵嗙Т缁ㄦ煡鏁嶅畝鍐惧殲缁绢収鍠涢濠氬嫉瀹ュ懎顫ら柛锝冨妼閸戯繝宕ラ姘楁鐐跺煐椤ュ懘寮婚妷褜浼傞柛?;
                            } else if (ex.getMessage().contains("timeout")) {
                                errorMessage += "\n妫ｅ啯瀵?鐎点倝缂氶? 閺夆晝鍋炵敮瀵告惥閸涱喗顦ч柨娑樼焷椤曨剙螞閳ь剟寮婚妷褏绉圭紓浣圭矎缁绘盯骞掗妷锕€鐏楅悘蹇旂箚閻︻垶宕楅張鐢甸搨URL";
                            }
                            
                            logMessage(errorMessage);
                            setStatus("閺夆晝鍋炵敮鎾煥濞嗘帩鍤?);
                        }
                    });
                }
            };
            
            webSocketClient.connect();
        } catch (Exception e) {
            logMessage("闁?WebSocket閺夆晝鍋炵敮瀛樺緞鏉堫偉袝: " + e.getMessage());
        }
    }
    

    private void disconnectWebSocket() {
        try {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                webSocketClient.close();
                logMessage("妫ｅ啯鏁?闁归潧顑呮慨鈺呭棘椤撶偟纾籛ebSocket閺夆晝鍋炵敮?);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        connectionLabel.setText("閺夆晝鍋炵敮鎾偐閼哥鍋? 鐎圭寮堕弻鍥ь嚕閳?);
                        connectionLabel.setForeground(Color.RED);
                        setStatus("鐎圭寮堕弻鍥ь嚕閳?);
                    }
                });
            } else {
                logMessage("妫ｅ啯瀵?WebSocket閺夆晝鍋炵敮鏉戭啅閼碱剛鐥呴柡鍌ゅ幖缁?);
            }
        } catch (Exception e) {
            logMessage("闁?闁哄偆鍘肩槐鎴炴交閻愭潙澶嶉柡鍐硾閸ゎ參鏌? " + e.getMessage());
        }
    }
    

    private void reconnectWebSocket() {
        logMessage("妫ｅ啯鏁?鐎殿喒鍋撳┑顔碱儔閸ｆ悂寮幏宀€绠鹃柟渚珖ebSocket...");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                connectionLabel.setText("閺夆晝鍋炵敮鎾偐閼哥鍋? 闂佹彃绉甸弻濠冩交閻愭潙澶嶅☉?..");
                connectionLabel.setForeground(Color.ORANGE);
                setStatus("闂佹彃绉甸弻濠冩交閻愭潙澶嶅☉?..");
            }
        });
        

        disconnectWebSocket();
        

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logMessage("闁?闂佹彃绉风换娑氭偖椤惵ゅ幀闁? " + e.getMessage());
                }
            }
        }).start();
    }
    

    private void handleWebSocketMessage(final JSONObject response) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String requestId = response.getString("request_id");
                if (requestId != null) {
                    PendingRequest pending = pendingRequests.remove(requestId);
                    if (pending != null) {
                        handleApiResponse(pending.action, response);
                    }
                } else {

                }
            }
        });
    }
    

    private void handleApiResponse(String action, JSONObject response) {
        boolean success = response.getBooleanValue("success");
        String message = response.getString("message");
        
        switch (action) {
            case "add":
                if (success) {
                    logMessage("闁?婵烇綀顕ф慨鐐电磼閹存繄鏆伴柟瀛樺姇婵? " + message);
                    setStatus("婵烇綀顕ф慨鐐哄箣閹邦剙顫?);
                    loadBindingList();
                    usernameField.setText("");
                    minecraftUsernameField.setText("");
                } else {
                    logMessage("闁?婵烇綀顕ф慨鐐电磼閹存繄鏆板鎯扮簿鐟? " + message);
                    setStatus("婵烇綀顕ф慨鐐村緞鏉堫偉袝");
                }
                break;
                
            case "remove":
                if (success) {
                    logMessage("闁?閻熸瑱缍佸▍搴ｇ磼閹存繄鏆伴柟瀛樺姇婵? " + message);
                    setStatus("閻熸瑱缍佸▍搴ㄥ箣閹邦剙顫?);
                    loadBindingList();
                    usernameField.setText("");
                    minecraftUsernameField.setText("");
                } else {
                    logMessage("闁?閻熸瑱缍佸▍搴ｇ磼閹存繄鏆板鎯扮簿鐟? " + message);
                    setStatus("閻熸瑱缍佸▍搴㈠緞鏉堫偉袝");
                }
                break;
                
            case "validate":
                if (success) {
                    boolean exists = response.getBooleanValue("exists");
                    String minecraftId = response.getString("minecraft_id");
                    if (exists) {
                        logMessage("闁?濡ょ姴鐭侀惁澶愬箣閹邦剙顫? " + minecraftId + " 闁革负鍔庡▍褔宕ュ鍛濞?);
                    } else {
                        logMessage("闁?濡ょ姴鐭侀惁澶岀磼閹惧浜? " + minecraftId + " 濞戞挸绉村﹢顏堟儌閽樺鍊抽柛妤佹磻閼?);
                    }
                    setStatus("濡ょ姴鐭侀惁澶屸偓鐟版湰閸?);
                } else {
                    logMessage("闁?濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝: " + message);
                    setStatus("濡ょ姴鐭侀惁澶嬪緞鏉堫偉袝");
                }
                break;
                
            case "query":
                if (success) {
                    String username = response.getString("username");
                    String minecraftId = response.getString("minecraft_id");
                    boolean isBound = response.getBooleanValue("is_bound");
                    if (isBound) {
                        logMessage("闁?闁哄被鍎撮妤呭箣閹邦剙顫? " + username + " 缂備焦鍨甸悾楣冩儍閸戠嫝闁活潿鍔嶉崺娑㈠触瀹ュ嫯绀?" + minecraftId);
                    } else {
                        logMessage("闁?闁哄被鍎撮妤冪磼閹惧浜? " + username + " 闁哄牜浜炵划锔锯偓瑙勭搲C闁活潿鍔嶉崺娑㈠触?);
                    }
                    setStatus("闁哄被鍎撮妤冣偓鐟版湰閸?);
                } else {
                    logMessage("闁?闁哄被鍎撮妤佸緞鏉堫偉袝: " + message);
                    setStatus("闁哄被鍎撮妤佸緞鏉堫偉袝");
                }
                break;
                
            case "list":
                if (success) {
                    JSONArray bindings = response.getJSONArray("bindings");
                    updateBindingTable(bindings);
                    int total = response.getIntValue("total");
                    logMessage("妫ｅ啯鎯?缂備焦鍨甸悾楣冨礆濡ゅ嫨鈧啴宕濋悩鐑樼グ閻庣懓鏈崹? 闁?" + total + " 濞戞搩浜炵划锔锯偓瑙勮壘閸櫻呭寲?);
                    setStatus("闁告梻濮惧ù鍥┾偓鐟版湰閸?);
                } else {
                    logMessage("闁?闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝: " + message);
                    setStatus("闁告梻濮惧ù鍥ㄥ緞鏉堫偉袝");
                }
                break;
                
            case "search":
                if (success) {
                    JSONArray bindings = response.getJSONArray("bindings");
                    updateBindingTable(bindings);
                    int total = response.getIntValue("total");
                    String keyword = response.getString("keyword");
                    logMessage("妫ｅ啯鏁?闁瑰吋绮庨崒銊р偓鐟版湰閸? 闁稿繑濞婇弫顓犳嫚?\"" + keyword + "\" 闁瑰灚鍎抽崺?" + total + " 濞戞搩浜滅亸顕€鏌婂鍥╂尝闁?);
                    setStatus("闁瑰吋绮庨崒銊р偓鐟版湰閸?);
                } else {
                    logMessage("闁?闁瑰吋绮庨崒銊﹀緞鏉堫偉袝: " + message);
                    setStatus("闁瑰吋绮庨崒銊﹀緞鏉堫偉袝");
                }
                break;
                
            default:
                logMessage("闁衡偓鐠哄搫鐓傞柡鍫簽閻擄繝宕鍛畨: " + response.toJSONString());
        }
    }
    

    private String sendWebSocketRequest(String apiType, String action, JSONObject data) {
        if (webSocketClient == null || !webSocketClient.isOpen()) {
            logMessage("闁?WebSocket闁哄牜浜ｇ换娑㈠箳?);
            return null;
        }
        
        String requestId = String.valueOf(requestIdCounter.getAndIncrement());
        
        JSONObject request = new JSONObject();
        request.put("type", "api_request");
        request.put("api_type", apiType);
        request.put("action", action);
        request.put("data", data);
        request.put("request_id", requestId);
        

        
        webSocketClient.send(request.toJSONString());
        return requestId;
    }
    

    private void addBinding() {
        final String username = usernameField.getText().trim();
        final String minecraftUsername = minecraftUsernameField.getText().trim();
        
        if (username.isEmpty() || minecraftUsername.isEmpty()) {
            showError("閻犲洨鏌夌欢顓㈠礂閵壯囧厙缂備胶鍠撻弫銈夊箣瀹勭増鍊抽柛婊冾劆C闁活潿鍔嶉崺娑㈠触?);
            return;
        }
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("婵烇綀顕ф慨鐐寸▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("username", username);
                    data.put("minecraft_id", minecraftUsername);
                    data.put("request_username", username);
                    data.put("user_key", userKey);
                    
                    sendWebSocketRequest("whitelist", "add", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?婵烇綀顕ф慨鐐哄礄濞差亝鏅? " + e.getMessage());
                            setStatus("婵烇綀顕ф慨鐐哄礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

    private void removeBinding() {
        final String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showError("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ悷娆欑秮濞呭海绱掗幋婵堟毎闁汇劌瀚柈瀵哥磼閻旂儤鏆忛柟鏉戝槻閹?);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "缁绢収鍠栭悾鍓ф啺娴ｅ彨鎺旂磼閹寸姵鏆忛柟?\"" + username + "\" 闁汇劌鍤淐闁活潿鍔嶉崺娑㈠触瀹ュ懏鍋嬮柨?, 
            "缁绢収鍠涢鑽ゆ喆閿濆洨鎷?, 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("閻熸瑱绲跨划锔界▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("username", username);
                    data.put("request_username", username);
                    data.put("user_key", userKey);
                    
                    sendWebSocketRequest("whitelist", "remove", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?閻熸瑱绲跨划锕傚礄濞差亝鏅? " + e.getMessage());
                            setStatus("閻熸瑱绲跨划锕傚礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

        if (minecraftUsername.isEmpty()) {
            showError("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｅΔ鐘茬焷閻﹀鎯冮崙鐙㈤柣顫妽閸╂盯宕?);
            return;
        }
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("濡ょ姴鐭侀惁澶嬬▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("minecraft_id", minecraftUsername);
                    
                    sendWebSocketRequest("whitelist", "validate", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?濡ょ姴鐭侀惁澶愬礄濞差亝鏅? " + e.getMessage());
                            setStatus("濡ょ姴鐭侀惁澶愬礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

    private void queryBinding() {
        final String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showError("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ柡灞诲劥椤曟鎯冮崟顓㈠厙缂備胶鍠撻弫銈夊箣瀹勭増鍊?);
            return;
        }
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("闁哄被鍎撮妤佺▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("username", username);
                    data.put("request_username", username);
                    data.put("user_key", userKey);
                    
                    sendWebSocketRequest("whitelist", "query", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?闁哄被鍎撮妤呭礄濞差亝鏅? " + e.getMessage());
                            setStatus("闁哄被鍎撮妤呭礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

    private void searchBindings() {
        final String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBindingList();
            return;
        }
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("闁瑰吋绮庨崒銊︾▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("keyword", keyword);
                    data.put("super_key", AuthValidator.getCurrentSuperKey());
                    
                    sendWebSocketRequest("whitelist", "search", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?闁瑰吋绮庨崒銊╁礄濞差亝鏅? " + e.getMessage());
                            setStatus("闁瑰吋绮庨崒銊╁礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

    private void loadBindingList() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    setStatus("闁告梻濮惧ù鍥ㄧ▔?..");
                    
                    JSONObject data = new JSONObject();
                    data.put("super_key", AuthValidator.getCurrentSuperKey());
                    
                    sendWebSocketRequest("whitelist", "list", data);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            logMessage("闁?闁告梻濮惧ù鍥礄濞差亝鏅? " + e.getMessage());
                            setStatus("闁告梻濮惧ù鍥礄濞差亝鏅?);
                        }
                    });
                }
            }
        }).start();
    }
    

    private void updateBindingTable(JSONArray bindings) {

        tableModel.setRowCount(0);
        

            JSONObject binding = bindings.getJSONObject(i);
            Object[] row = {
                binding.getString("username"),
                binding.getString("minecraft_username"),
                binding.getString("email"),
                binding.getString("rank"),
                binding.get("pid"),
                binding.getString("create_time")
            };
            tableModel.addRow(row);
        }
    }
    

    private void logMessage(String message) {

        String timestamp = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Shanghai"))
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    private void setStatus(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                statusLabel.setText("闁绘鍩栭埀? " + status);
            }
        });
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "闂佹寧鐟ㄩ?, JOptionPane.ERROR_MESSAGE);
    }
    
    private void showConfigDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JTextField superKeyField = new JTextField(AuthValidator.getCurrentSuperKey());
        superKeyField.setEditable(false);
        
        panel.add(new JLabel("缂佺媴绱曢幃濠囧川濡櫣妲曢梺?(super_key):"));
        panel.add(superKeyField);
        panel.add(new JLabel("闁活潿鍔嶉崺娑氣偓闈涙閹?(user_key):"));
        panel.add(userKeyField);
        panel.add(new JLabel("閻犲洤鐡ㄥΣ?"));
        panel.add(new JLabel("<html>缂佺媴绱曢幃濠囧川濡櫣妲曢梺? 濞寸姴閽塷nfig闁煎浜滄慨鈺呮嚔瀹勬澘绲块柨娑樼灱閺併倖绂嶅鎭憇t/search闁瑰灝绉崇紞?br/>闁活潿鍔嶉崺娑氣偓闈涙閹? 闁归潧顑呮慨鈺傛綇閹惧啿寮抽柨娑樼灱閺併倖绂嶅鍍/remove/query闁瑰灝绉崇紞?/html>"));
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "闂佹澘绉堕悿鍡欌偓闈涙閹?, 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String newUserKey = userKeyField.getText().trim();
            
            if (!newUserKey.isEmpty()) {
                userKey = newUserKey;
                logMessage("闁活潿鍔嶉崺娑氣偓闈涙閹告粌顔忛懠棰濆晭缂?(闂傗偓閸喖顔? " + newUserKey.length() + ")");
                JOptionPane.showMessageDialog(
                    this,
                    "闁活潿鍔嶉崺娑氣偓闈涙閹告粌顔忛煫顓犵閻庢稒锕槐婵嬪矗椤栨瑤绨伴弶鈺傜椤㈡垿鎮介妸锕€鐓曢柟鍨С缂嶆梹绂嶉崱顓犵＜",
                    "闂佹澘绉堕悿鍡涘箣閹邦剙顫?,
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                logMessage("闁活潿鍔嶉崺娑氣偓闈涙閹告粍绋夐搹鍏夋晞闁挎稑鏈悡鍥ㄧ濞戞瑦鎯欏ù锝嗙矊閻ㄣ垽寮悩宕囥€婂ù锝堟硶閺?);
            }
            
            logMessage("缂佺媴绱曢幃濠囧川濡櫣妲曢梺濮愬劜濞奸潧鈹? Config缂侇垵宕电划?(闂傗偓閸喖顔? " + AuthValidator.getCurrentSuperKey().length() + ")");
        }
    }
    
    public static void main(String[] args) {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserBindingGUI().setVisible(true);
            }
        });
    }
} 
