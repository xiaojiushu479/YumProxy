package yumProxy.server.key;

import yumProxy.YumProxy;
import yumProxy.net.mysql.MySQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class KeyTest extends JFrame {
    private JTextField prefixField;
    private JTextField countField;
    private JTextField timeField;
    private JTextField keyField;
    private JTextField usernameField;
    private JTextArea resultArea;
    private JButton generateButton;
    private JButton useKeyButton;
    private JButton queryKeysButton;
    private JButton deleteKeyButton;
    
    public KeyTest() {
        setTitle("闁告せ鈧磭妲曠紒鐙呯磿閹﹪宕抽妸锔俱偞閻犲洦娲栨导鎰板礂?);
        setSize(700, 600);
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
        } catch (Exception e) {
            showResult("闁轰胶澧楀畵浣规償閹惧啿鐏ュ┑顔碱儏鐎靛弶寰勬潏顐バ? " + e.getMessage());
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("闁告瑥鍊归弳鐔告綇閹惧啿寮?));
        
        inputPanel.add(new JLabel("闁告挸绉剁槐?"));
        prefixField = new JTextField("TEST");
        inputPanel.add(prefixField);
        
        inputPanel.add(new JLabel("闁汇垻鍠愰崹姘跺极娴兼潙娅?"));
        countField = new JTextField("1");
        inputPanel.add(countField);
        
        inputPanel.add(new JLabel("闁哄啫鐖煎Λ?閻忓繐绻戝?:"));
        timeField = new JTextField("24");
        inputPanel.add(timeField);
        
        inputPanel.add(new JLabel("閻庣懓鏈弳锝夊础閳ュ磭妲?"));
        keyField = new JTextField();
        inputPanel.add(keyField);
        
        inputPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃堝箰婢舵劖灏?));
        
        generateButton = new JButton("闁汇垻鍠愰崹姘跺础閳ュ磭妲?);
        useKeyButton = new JButton("濞达綀娉曢弫銈夊础閳ュ磭妲?);
        queryKeysButton = new JButton("闁哄被鍎撮妤呭础閳ュ磭妲?);
        deleteKeyButton = new JButton("闁告帞濞€濞呭酣宕￠垾宕囨");
        
        buttonPanel.add(generateButton);
        buttonPanel.add(useKeyButton);
        buttonPanel.add(queryKeysButton);
        buttonPanel.add(deleteKeyButton);
        

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃傜磼閹惧浜?));
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        

            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeys();
            }
        });
        
        useKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useKey();
            }
        });
        
        queryKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryKeys();
            }
        });
        
        deleteKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteKey();
            }
        });
        

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }
    
    private void generateKeys() {
        String prefix = prefixField.getText().trim();
        String countStr = countField.getText().trim();
        String timeStr = timeField.getText().trim();
        
        if (prefix.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儱顤呯紓鍌楀亾闁?);
            return;
        }
        
        if (countStr.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬櫢闁瑰瓨鍔栭弳鐔兼煂韫囥儳纾?);
            return;
        }
        
        if (timeStr.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夛附顦ч梻鍌濇彧缁?);
            return;
        }
        
        try {
            int count = Integer.parseInt(countStr);
            int time = Integer.parseInt(timeStr);
            
            if (count <= 0) {
                showResult("闁汇垻鍠愰崹姘跺极娴兼潙娅ら煫鍥ф嚇閵嗗繑寰勮缁?闁?);
                return;
            }
            
            if (time < 0) {
                showResult("闁哄啫鐖煎Λ鎸庣▔瀹ュ牆鍘村☉鎾荤細缁€瀣极鐢喚纾?);
                return;
            }
            
            KeyManager keyManager = new KeyManager();
            List<Key> keys = keyManager.distributeKeys(prefix, count, time);
            
            StringBuilder sb = new StringBuilder();
            sb.append("闁汇垻鍠愰崹姘跺础閳ュ磭妲曢柟瀛樺姇婵盯鏁嶇仦鐐闂? ").append(count).append("\n");
            sb.append("闁告挸绉剁槐? ").append(prefix).append("\n");
            sb.append("闁哄啫鐖煎Λ? ").append(time).append("閻忓繐绻戝淇搉");
            sb.append("闁告せ鈧磭妲曢柛鎺擃殙閵?\n");
            
            for (int i = 0; i < keys.size(); i++) {
                Key key = keys.get(i);
                sb.append(i + 1).append(". ").append(key.toString()).append(" (闁哄啫鐖煎Λ? ").append(key.getTime()).append("閻忓繐绻戝?\n");
            }
            
            showResult(sb.toString());
            

                keyField.setText(keys.get(0).toString());
            }
            
        } catch (NumberFormatException e) {
            showResult("闁轰椒鍗抽崳娲箣閺嶃劍顦ч梻鍌氱摠閻楃顕ｈ箛娑欐櫓閻犲浂鍨界槐?);
        }
    }
    
    private void useKey() {
        String fullKey = keyField.getText().trim();
        String username = usernameField.getText().trim();
        
        if (fullKey.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儳鏆氶柡浣规綑瀹曡京鈧潧妫寸槐?);
            return;
        }
        
        if (username.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈡晬?);
            return;
        }
        

        try {
            String[] parts = fullKey.split("-", 2);
            if (parts.length != 2) {
                showResult("闁告せ鈧磭妲曢柡宥囧帶缁憋繝鏌ㄥ▎鎺濆殩闁挎稐鐒﹂悧绋款嚕韫囨挾瀹夊☉? 闁告挸绉剁槐?闁告せ鈧磭妲?);
                return;
            }
            
            String prefix = parts[0];
            String code = parts[1];
            

            List<java.util.Map<String, Object>> results = yumProxy.net.mysql.MySQLUtils.executeQuery(checkSql, prefix, code);
            
            if (results.isEmpty()) {
                showResult("闁告せ鈧磭妲曞☉鎾崇Т閻°劑宕? " + fullKey);
                return;
            }
            

            String checkUsedSql = "SELECT id FROM used_keys WHERE prefix = ? AND code = ?";
            List<java.util.Map<String, Object>> usedResults = yumProxy.net.mysql.MySQLUtils.executeQuery(checkUsedSql, prefix, code);
            
            if (!usedResults.isEmpty()) {
                showResult("闁告せ鈧磭妲曠€规瓕寮撴繛鍥偨? " + fullKey);
                return;
            }
            

            int timeHours = ((Number) results.get(0).get("time_hours")).intValue();
            

            String delSql = "DELETE FROM `keys` WHERE prefix = ? AND code = ?";
            yumProxy.net.mysql.MySQLUtils.executeUpdate(delSql, prefix, code);
            
            String insertUsedSql = "INSERT INTO used_keys (prefix, code) VALUES (?, ?)";
            yumProxy.net.mysql.MySQLUtils.executeUpdate(insertUsedSql, prefix, code);
            

            if (timeHours > 0) {
                boolean activated = yumProxy.server.timestamp.TimestampManager.activatePlayer(username, timeHours);
                if (activated) {
                    showResult("闁告せ鈧磭妲曞ù锝堟硶閺併倝骞嬮幇顒€顫犻柨娑楃n闁告せ鈧磭妲? " + fullKey + "\n闁活潿鍔嶉崺娑㈠触? " + username + "\n婵犵鍋撴繛鑼帛濡炲倿姊? " + timeHours + "閻忓繐绻戝?);
                } else {
                    showResult("闁告せ鈧磭妲曞ù锝堟硶閺併倝骞嬮幇顒€顫犻柨娑樺缁查箖寮崼鏇燂紵闁圭娅曠缓鍝劽虹拠灞備杭閻犳劑鍎荤槐鎶僴闁告せ鈧磭妲? " + fullKey + "\n闁活潿鍔嶉崺娑㈠触? " + username);
                }
            } else {
                showResult("闁告せ鈧磭妲曞ù锝堟硶閺併倝骞嬮幇顒€顫犻柨娑楃n闁告せ鈧磭妲? " + fullKey + "\n闁活潿鍔嶉崺娑㈠触? " + username + "\n闁哄啫鐖煎Λ? 0閻忓繐绻戝鍌炴晬閸喐锟ラ柡鍐ㄧ埣濡潡鏁?);
            }
            
        } catch (Exception e) {
            showResult("濞达綀娉曢弫銈夊础閳ュ磭妲曞鎯扮簿鐟? " + e.getMessage());
        }
    }
    
    private void queryKeys() {
        String prefix = prefixField.getText().trim();
        
        if (prefix.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儱顤呯紓鍌楀亾闁?);
            return;
        }
        
        try {
            String sql = "SELECT prefix, code, time_hours, create_time FROM `keys` WHERE prefix = ? ORDER BY create_time DESC LIMIT 10";
            List<java.util.Map<String, Object>> results = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, prefix);
            
            if (results.isEmpty()) {
                showResult("婵炲备鍓濆﹢渚€骞嶉幆褍鐓傞柛鎾崇Ф缁辨垶绋?'" + prefix + "' 闁汇劌瀚畷杈┾偓?);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("闁哄被鍎撮妤冪磼閹惧浜?(闁告挸绉剁槐? ").append(prefix).append("):\n");
            sb.append("闁稿繗椴告竟姗€宕?").append(results.size()).append(" 濞戞搩浜滃畷杈┾偓闈涙懌n\n");
            
            for (int i = 0; i < results.size(); i++) {
                java.util.Map<String, Object> row = results.get(i);
                String code = (String) row.get("code");
                int timeHours = ((Number) row.get("time_hours")).intValue();
                String createTime = row.get("create_time").toString();
                
                sb.append(i + 1).append(". ").append(prefix).append("-").append(code);
                sb.append(" (闁哄啫鐖煎Λ? ").append(timeHours).append("閻忓繐绻戝? 闁告帗绋戠紓? ").append(createTime).append(")\n");
            }
            
            showResult(sb.toString());
            
        } catch (Exception e) {
            showResult("闁哄被鍎撮妤呭础閳ュ磭妲曞鎯扮簿鐟? " + e.getMessage());
        }
    }
    
    private void deleteKey() {
        String fullKey = keyField.getText().trim();
        
        if (fullKey.isEmpty()) {
            showResult("閻犲洨鏌夌欢顓㈠礂閵夈儳鏆氶柡浣规綑瀹曡京鈧潧妫寸槐?);
            return;
        }
        

        int result = JOptionPane.showConfirmDialog(this, 
            "缁绢収鍠栭悾鍓ф啺娴ｇ鐏╅梻鍕╁€曞畷杈┾偓?" + fullKey + " 闁告碍顨愮槐?, 
            "缁绢収鍠涢濠氬礆閻樼粯鐝?, 
            JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            String[] parts = fullKey.split("-", 2);
            if (parts.length != 2) {
                showResult("闁告せ鈧磭妲曢柡宥囧帶缁憋繝鏌ㄥ▎鎺濆殩闁挎稐鐒﹂悧绋款嚕韫囨挾瀹夊☉? 闁告挸绉剁槐?闁告せ鈧磭妲?);
                return;
            }
            
            String prefix = parts[0];
            String code = parts[1];
            

            List<java.util.Map<String, Object>> results = yumProxy.net.mysql.MySQLUtils.executeQuery(checkSql, prefix, code);
            
            if (results.isEmpty()) {
                showResult("闁告せ鈧磭妲曞☉鎾崇Т閻°劑宕? " + fullKey);
                return;
            }
            

            String checkUsedSql = "SELECT id FROM used_keys WHERE prefix = ? AND code = ?";
            List<java.util.Map<String, Object>> usedResults = yumProxy.net.mysql.MySQLUtils.executeQuery(checkUsedSql, prefix, code);
            
            if (!usedResults.isEmpty()) {
                showResult("闁告せ鈧磭妲曠€规瓕寮撴繛鍥偨椤帞绀夐柡鍐У绾爼宕氶悩缁樼彑: " + fullKey);
                return;
            }
            

            String deleteSql = "DELETE FROM `keys` WHERE prefix = ? AND code = ?";
            int affected = yumProxy.net.mysql.MySQLUtils.executeUpdate(deleteSql, prefix, code);
            
            if (affected > 0) {
                showResult("闁告せ鈧磭妲曢柛鎺斿█濞呭酣骞嬮幇顒€顫? " + fullKey);

            } else {
                showResult("闁告せ鈧磭妲曢柛鎺斿█濞呭孩寰勬潏顐バ? " + fullKey);
            }
            
        } catch (Exception e) {
            showResult("闁告帞濞€濞呭酣宕￠垾宕囨濠㈡儼绮剧憴? " + e.getMessage());
        }
    }
    
    private void showResult(String message) {

        resultArea.append("[" + java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Shanghai")).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + message + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KeyTest().setVisible(true);
            }
        });
    }
} 
