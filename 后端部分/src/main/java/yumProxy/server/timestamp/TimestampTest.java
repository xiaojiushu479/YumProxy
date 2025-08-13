package yumProxy.server.timestamp;

import yumProxy.YumProxy;
import yumProxy.net.mysql.MySQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampTest extends JFrame {
    private JTextField usernameField;
    private JTextField daysField;
    private JTextArea resultArea;
    private JButton activateButton;
    private JButton queryButton;
    private JButton extendButton;
    private JButton deactivateButton;
    private JButton deleteButton;
    private JButton checkActiveButton;
    private JButton cleanupButton;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public TimestampTest() {
        setTitle("闁哄啫鐖煎Λ鍧楀箣瀹曞浂鍚€闁荤偛妫楀▍鎺懨圭€ｎ厾妲哥€规悶鍎遍崣?);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        initDatabase();
    }
    
    private void initDatabase() {
        try {
            MySQL mysql = MySQL.getInstance();
            mysql.connect();
            if (!mysql.isConnected()) {
                showResult("闁轰胶澧楀畵浣规償閹惧湱绠鹃柟鎭掑劚閵囨垹鎷归妷顖滅＜");
                return;
            }
            showResult("闁轰胶澧楀畵浣规償閹惧湱绠鹃柟鎭掑劜閸ㄦ岸宕濋悤鍌滅＜");
            TimestampManager.initTable();
            showResult("闁哄啫鐖煎Λ鍧楀箣鐎圭姰鈧啴宕氬┑鍡╂綏闁告牗鐗曢悾顒勫箣閹板墎纾?);
        } catch (Exception e) {
            showResult("闁轰胶澧楀畵浣规償閹惧啿鐏ュ┑顔碱儏鐎靛弶寰勬潏顐バ? " + e.getMessage());
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("闁告瑥鍊归弳鐔告綇閹惧啿寮?));
        
        inputPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        
        inputPanel.add(new JLabel("閻忓繐绻戝鍌炲极?"));
        daysField = new JTextField();
        inputPanel.add(daysField);
        
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃堝箰婢舵劖灏?));
        
        activateButton = new JButton("婵犵鍋撴繛鑼跺吹鐢櫣鈧?);
        queryButton = new JButton("闁哄被鍎撮妤呮偝閳轰緡鍟€");
        extendButton = new JButton("缂備緡鍘藉﹢锟犳偝閳轰緡鍟€");
        deactivateButton = new JButton("闁稿绮庨弫銈夋偝閳轰緡鍟€");
        deleteButton = new JButton("闁告帞濞€濞呭酣鎮抽埡渚囧晙");
        checkActiveButton = new JButton("婵☆偀鍋撻柡灞诲劜缁哄搫煤?);
        cleanupButton = new JButton("婵炴挸鎳愰幃濠冩交閸ャ劍鍩?);
        
        buttonPanel.add(activateButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(extendButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(checkActiveButton);
        buttonPanel.add(cleanupButton);
        

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃傜磼閹惧浜?));
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        

            @Override
            public void actionPerformed(ActionEvent e) {
                activatePlayer();
            }
        });
        
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryPlayer();
            }
        });
        
        extendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extendPlayer();
            }
        });
        
        deactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deactivatePlayer();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });
        
        checkActiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkActive();
            }
        });
        
        cleanupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanupExpired();
            }
        });
        

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }
    
    private void activatePlayer() {
        String username = usernameField.getText().trim();
        String daysStr = daysField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        if (daysStr.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儳姣堥柡鍐煐閺嗙喖鏁?);
            return;
        }
        
        try {
            int hours = Integer.parseInt(daysStr);
            if (hours <= 0) {
                showResult("閻忓繐绻戝鍌炲极閺夎法绠戝銈堫嚙閵囧洦绂?闁?);
                return;
            }
            
            boolean success = TimestampManager.activatePlayer(username, hours);
            if (success) {
                showResult("闁绘壕鏅涢宥呪攽閳ь剙煤缂佹ê鐏囬柛? " + username + ", 閻忓繐绻戝鍌炲极? " + hours);
            } else {
                showResult("闁绘壕鏅涢宥呪攽閳ь剙煤鐠囧眰浜奸悹? " + username);
            }
        } catch (NumberFormatException e) {
            showResult("閻忓繐绻戝鍌炲极閻楀牏澹愮€殿喖绻橀弫濠勬嫚椤栥倗纾?);
        }
    }
    
    private void queryPlayer() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        TimestampInfo info = TimestampManager.getPlayerTimestamp(username);
        if (info != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("闁绘壕鏅涢宥嗙┍閳╁啩绱栭柡灞诲劥椤曟骞嬮幇顒€顫?\n");
            sb.append("闁活潿鍔嶉崺娑㈠触? ").append(info.username).append("\n");

            String expiresAtStr = "闁?;
            if (info.activatedAt != null) {
                activatedAtStr = info.activatedAt.toInstant()
                    .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (info.expiresAt != null) {
                expiresAtStr = info.expiresAt.toInstant()
                    .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            sb.append("婵犵鍋撴繛鑼帛濡炲倿姊? ").append(activatedAtStr).append("\n");
            sb.append("闁告帞澧楀﹢锟犲籍閸洘锛? ").append(expiresAtStr).append("\n");
            sb.append("闁哄嫷鍨伴幆浣糕攽閳ь剙煤? ").append(info.isActive ? "闁? : "闁?).append("\n");
            sb.append("闁哄嫷鍨伴幆浣规交閸ャ劍鍩? ").append(info.isExpired ? "闁? : "闁?).append("\n");
            showResult(sb.toString());
        } else {
            showResult("闁绘壕鏅涢宥嗙▔瀹ュ懐鎽犻柛? " + username);
        }
    }
    
    private void extendPlayer() {
        String username = usernameField.getText().trim();
        String daysStr = daysField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        if (daysStr.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儳姣堥柡鍐煐閺嗙喖鏁?);
            return;
        }
        
        try {
            int hours = Integer.parseInt(daysStr);
            if (hours <= 0) {
                showResult("閻忓繐绻戝鍌炲极閺夎法绠戝銈堫嚙閵囧洦绂?闁?);
                return;
            }
            
            boolean success = TimestampManager.extendPlayerTime(username, hours);
            if (success) {
                showResult("闁绘壕鏅涢宥囩磼椤撶喐鍩傞柟瀛樺姇婵? " + username + ", 缂備緡鍘藉﹢锛勪焊韫囨梹顦ч柡? " + hours);
            } else {
                showResult("闁绘壕鏅涢宥囩磼椤撶喐鍩傚鎯扮簿鐟? " + username);
            }
        } catch (NumberFormatException e) {
            showResult("閻忓繐绻戝鍌炲极閻楀牏澹愮€殿喖绻橀弫濠勬嫚椤栥倗纾?);
        }
    }
    
    private void deactivatePlayer() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        boolean success = TimestampManager.deactivatePlayer(username);
        if (success) {
            showResult("闁绘壕鏅涢宥夊磻濠婂懏鏆忛柟瀛樺姇婵? " + username);
        } else {
            showResult("闁绘壕鏅涢宥夊磻濠婂懏鏆忓鎯扮簿鐟? " + username);
        }
    }
    
    private void deletePlayer() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "缁绢収鍠栭悾鍓ф啺娴ｇ鐏╅梻鍕╁€楃敮铏光偓?" + username + " 闁汇劌瀚鍥亹閺囩偞鍋嬮柨?, 
            "缁绢収鍠涢濠氬礆閻樼粯鐝?, 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean success = TimestampManager.deletePlayer(username);
            if (success) {
                showResult("闁绘壕鏅涢宥囨媼閺夎法绉块柛鎺斿█濞呭酣骞嬮幇顒€顫? " + username);
            } else {
                showResult("闁绘壕鏅涢宥囨媼閺夎法绉块柛鎺斿█濞呭孩寰勬潏顐バ? " + username);
            }
        }
    }
    
    private void checkActive() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        
        boolean isActive = TimestampManager.isPlayerActive(username);
        showResult("闁绘壕鏅涢宥呪攽閳ь剙煤閼姐倕笑闁诡兛鐒﹂ˉ鍛村蓟? " + username + "\n缂備焦鎸婚悘? " + (isActive ? "婵犵鍋撴繛? : "闁哄牜浜濈缓鍝劽?));
    }
    
    private void cleanupExpired() {
        int count = TimestampManager.cleanupExpiredPlayers();
        showResult("婵炴挸鎳愰幃濠冩交閸ャ劍鍩傞柣婧炬櫅椤斿秶鈧懓鏈崹姘舵晬鐏炶棄顨涢柛婵嗙Х椤斿洩銇愰弴鐔告: " + count);
    }
    
    private void showResult(String message) {

        String timestamp = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Shanghai")).format(formatter);
        resultArea.append("[" + timestamp + "] " + message + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TimestampTest().setVisible(true);
            }
        });
    }
} 
