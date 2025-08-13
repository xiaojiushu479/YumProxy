package yumProxy.server.user;

import yumProxy.YumProxy;
import yumProxy.net.httpAPI.EmailApiServer;
import yumProxy.net.httpAPI.UserApiServer;

import javax.swing.*;
import java.awt.*;

import com.google.gson.Gson;

public class UserTest {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {

        SwingUtilities.invokeLater(UserTest::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("闁活潿鍔嶉崺娑樷枖閵娿儱鏂€/闁谎嗩嚙缂嶅秴霉鐎ｎ厾妲?);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();


        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(10, 1, 5, 5));
        JTextField regUserField = new JTextField();
        JPasswordField regPassField = new JPasswordField();
        JTextField regEmailField = new JTextField();
        JTextField regCodeField = new JTextField();
        JButton sendCodeBtn = new JButton("闁告瑦鍨块埀顑跨窔閻涙瑧鎷犳担铏瑰灣");
        JButton registerBtn = new JButton("婵炲鍔岄崬?);
        JButton queryRankPidBtn = new JButton("闁哄被鍎撮妤呮偨閵婏箑鐓昉ID闁告粌顒╝nk");
        JButton queryFullInfoBtn = new JButton("闁哄被鍎撮妤冣偓鐟版湰閺嗭綁鎮介妸锕€鐓曞ǎ鍥ｅ墲娴?);
        JButton deleteUserBtn = new JButton("闁告帞濞€濞呭酣鎮介妸锕€鐓?);
        JTextArea regResult = new JTextArea(3, 40);
        regResult.setEditable(false);
        registerPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        registerPanel.add(regUserField);
        registerPanel.add(new JLabel("閻庨潧妫涢悥?"));
        registerPanel.add(regPassField);
        registerPanel.add(new JLabel("闂侇収鍠氶?"));
        registerPanel.add(regEmailField);
        registerPanel.add(sendCodeBtn);
        registerPanel.add(new JLabel("闂侇収鍠氶鍫燁殽瀹€鍐闁?"));
        registerPanel.add(regCodeField);
        registerPanel.add(registerBtn);
        registerPanel.add(queryRankPidBtn);
        registerPanel.add(queryFullInfoBtn);
        registerPanel.add(deleteUserBtn);
        registerPanel.add(new JScrollPane(regResult));


        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(6, 1, 5, 5));
        JTextField loginUserField = new JTextField();
        JPasswordField loginPassField = new JPasswordField();
        JButton loginBtn = new JButton("闁谎嗩嚙缂?);
        JTextArea loginResult = new JTextArea(3, 40);
        loginResult.setEditable(false);
        loginPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        loginPanel.add(loginUserField);
        loginPanel.add(new JLabel("閻庨潧妫涢悥?"));
        loginPanel.add(loginPassField);
        loginPanel.add(loginBtn);
        loginPanel.add(new JScrollPane(loginResult));


        JPanel userInfoPanel = createUserInfoPanel();
        

        JPanel banManagePanel = createBanManagePanel();

        tabbedPane.addTab("婵炲鍔岄崬?, registerPanel);
        tabbedPane.addTab("闁谎嗩嚙缂?, loginPanel);
        tabbedPane.addTab("闁活潿鍔嶉崺娑欑┍閳╁啩绱?, userInfoPanel);
        tabbedPane.addTab("閻忓繋鑳堕々锔剧不閿涘嫭鍊?, banManagePanel);
        frame.add(tabbedPane, BorderLayout.CENTER);


        sendCodeBtn.addActionListener(e -> {
            String email = regEmailField.getText().trim();
            if (email.isEmpty()) {
                regResult.setText("閻犲洨鏌夌欢顓㈠礂閵夆晛浠忕紒?);
                return;
            }

            String processedEmail = processEmailInput(email);

            boolean ok = EmailApiServer.emailManager != null && EmailApiServer.emailManager.sendCode(processedEmail);
            regResult.setText(ok ? "濡ょ姴鐭侀惁澶愭儘娴ｇ鍤掗柛娆愬灴閳ь兛绶ょ槐婵堟嫚闁垮鍙€闁衡偓閸洖浠忕紒? : "濡ょ姴鐭侀惁澶愭儘娴ｇ绲洪梺顐＄閵囨垹鎷?);
        });

        registerBtn.addActionListener(e -> {
            String username = regUserField.getText().trim();
            String password = new String(regPassField.getPassword());
            String email = regEmailField.getText().trim();
            String code = regCodeField.getText().trim();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || code.isEmpty()) {
                regResult.setText("閻犲洤鍢查敐鐐哄礃濞嗘劕顣查柡鍫濐槹閺佺偤宕樼仦闂寸箚闁?);
                return;
            }
            boolean ok = UserApiServer.getUserManager().register(username, password, email, code);
            if (ok) {

                String sql = "SELECT rank, pid FROM users WHERE username = ?";
                java.util.List<java.util.Map<String, Object>> res = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
                if (!res.isEmpty()) {
                    String rank = String.valueOf(res.get(0).get("rank"));
                    Object pidObj = res.get(0).get("pid");
                    int pid;
                    if (pidObj instanceof Number) {
                        pid = ((Number)pidObj).intValue();
                    } else {
                        pid = Integer.parseInt(String.valueOf(pidObj));
                    }
                    regResult.setText("婵炲鍔岄崬浠嬪箣閹邦剙顫燶nRank: " + rank + "\nPID: " + pid);
                } else {
                    regResult.setText("婵炲鍔岄崬浠嬪箣閹邦剙顫犻柨娑樺缁查箖寮甸鍛弨闁告帞娈檃nk/pid");
                }
            } else {
                regResult.setText("婵炲鍔岄崬鑺ュ緞鏉堫偉袝闁挎稑鐭侀顒€螞閳ь剟寮婚妷鈺冨矗閻犲洣鑳堕悥婊堝箣閺嶎偅鏆忛柟鏉戝槻閹洟寮伴姘剨鐎瑰憡褰冮悺銊╁捶?);
            }
        });

        queryRankPidBtn.addActionListener(e -> {
            String username = regUserField.getText().trim();
            if (username.isEmpty()) {
                regResult.setText("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ柡灞诲劥椤曟鎯冮崟顓熸殢闁规潙鍢查幃?);
                return;
            }
            String sql = "SELECT rank, pid FROM users WHERE username = ?";
            java.util.List<java.util.Map<String, Object>> res = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
            if (!res.isEmpty()) {
                String rank = String.valueOf(res.get(0).get("rank"));
                Object pidObj = res.get(0).get("pid");
                int pid;
                if (pidObj instanceof Number) {
                    pid = ((Number)pidObj).intValue();
                } else {
                    pid = Integer.parseInt(String.valueOf(pidObj));
                }
                regResult.setText("闁活潿鍔嶉崺? " + username + "\nRank: " + rank + "\nPID: " + pid);
            } else {
                regResult.setText("闁哄牜浜濋悡锟犲礆閹峰矈鍤夐柣顫妽閸?);
            }
        });

        queryFullInfoBtn.addActionListener(e -> {
            String username = regUserField.getText().trim();
            if (username.isEmpty()) {
                regResult.setText("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ柡灞诲劥椤曟鎯冮崟顓熸殢闁规潙鍢查幃?);
                return;
            }
            

            String userInfo = queryUserFullInfo(username);
            regResult.setText(userInfo);
        });

        deleteUserBtn.addActionListener(e -> {
            String username = regUserField.getText().trim();
            if (username.isEmpty()) {
                regResult.setText("閻犲洨鏌夌欢顓㈠礂閵夘煈娲ｉ柛鎺斿█濞呭酣鎯冮崟顓熸殢闁规潙鍢查幃?);
                return;
            }
            String sql = "DELETE FROM users WHERE username = ?";
            int result = yumProxy.net.mysql.MySQLUtils.executeUpdate(sql, username);
            if (result > 0) {
                regResult.setText("闁活潿鍔嶉崺?" + username + " 闁告帞濞€濞呭酣骞嬮幇顒€顫?);
            } else {
                regResult.setText("闁活潿鍔嶉崺?" + username + " 闁告帞濞€濞呭孩寰勬潏顐バ曢柟瀛樼墧缁楀鈧稒锚濠€?);
            }
        });

        loginBtn.addActionListener(e -> {
            String username = loginUserField.getText().trim();
            String password = new String(loginPassField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                loginResult.setText("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃鏇㈠椽鐏炵晫妲曢柣?);
                return;
            }
            boolean ok = UserApiServer.getUserManager().login(username, password);
            if (ok) {

                String sql = "SELECT rank, pid FROM users WHERE username = ?";
                java.util.List<java.util.Map<String, Object>> res = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
                if (!res.isEmpty()) {
                    String rank = String.valueOf(res.get(0).get("rank"));
                    Object pidObj = res.get(0).get("pid");
                    int pid;
                    if (pidObj instanceof Number) {
                        pid = ((Number)pidObj).intValue();
                    } else {
                        pid = Integer.parseInt(String.valueOf(pidObj));
                    }
                    loginResult.setText("闁谎嗩嚙缂嶅秹骞嬮幇顒€顫燶nRank: " + rank + "\nPID: " + pid);
                } else {
                    loginResult.setText("闁谎嗩嚙缂嶅秹骞嬮幇顒€顫犻柨娑樺缁查箖寮甸鍛弨闁告帞娈檃nk/pid");
                }
            } else {
                loginResult.setText("闁谎嗩嚙缂嶅秵寰勬潏顐バ曢柨娑樼灱閺併倝骞嬪畡鐗堝€抽柟瀛樼墪閻︽垿鎯嶆笟鈧弫濠勬嫚?);
            }
        });

        frame.setVisible(true);
    }
    
    
    private static String processEmailInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String trimmed = input.trim();
        

            return trimmed;
        }
        

        if (trimmed.matches("^\\d+$")) {
            return trimmed + "@qq.com";
        }
        

    }
    
    
    private static String queryUserFullInfo(String username) {
        try {

            String sql = "SELECT * FROM users WHERE username = ?";
            java.util.List<java.util.Map<String, Object>> userResult = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
            
            if (userResult.isEmpty()) {
                return "闁?闁哄牜浜濇竟姗€宕氶幍顔芥殢闁? " + username;
            }
            
            java.util.Map<String, Object> user = userResult.get(0);
            StringBuilder info = new StringBuilder();
            info.append("妫ｅ啯鎯?闁活潿鍔嶉崺娑氣偓鐟版湰閺嗭絾绌遍埄鍐х礀\n");
            info.append("闁抽€涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁氶柍閫涜閺€锝夊煘娴ｅ弶鏁歕n");
            

            info.append("妫ｅ啯鍣?闁活潿鍔嶉崺娑㈠触? ").append(user.get("username")).append("\n");
            info.append("妫ｅ啯鎲?闂侇収鍠氶? ").append(user.get("email")).append("\n");
            info.append("妫ｅ啫姹?缂佹稑顦辨? ").append(user.get("rank")).append("\n");
            info.append("妫ｅ啫鏅?PID: ").append(user.get("pid")).append("\n");
            

            String minecraftUsername = (String) user.get("minecraft_username");
            if (minecraftUsername != null && !minecraftUsername.trim().isEmpty()) {
                info.append("妫ｅ啫绠?MC闁活潿鍔嶉崺娑㈠触? ").append(minecraftUsername).append("\n");
            } else {
                info.append("妫ｅ啫绠?MC闁活潿鍔嶉崺娑㈠触? 闁哄牜浜炵划锔锯偓瑙勭摗n");
            }
            

            Object createTime = user.get("create_time");
            if (createTime != null) {
                info.append("妫ｅ啯鎯?闁告帗绋戠紓鎾诲籍閸洘锛? ").append(createTime).append("\n");
            }
            
            Object lastLogin = user.get("last_login");
            if (lastLogin != null) {
                info.append("妫ｅ啯娅?闁哄牃鍋撻柛姘捣濞呫儴銇? ").append(lastLogin).append("\n");
            }
            

            if (isActive != null) {
                boolean active = Boolean.parseBoolean(String.valueOf(isActive));
                info.append("闁?閻犳劧闄勯崺娑㈡偐閼哥鍋? ").append(active ? "婵犵鍋撴繛? : "闁哄牜浜濈缓鍝劽?).append("\n");
            }
            
            Object isVerified = user.get("is_verified");
            if (isVerified != null) {
                boolean verified = Boolean.parseBoolean(String.valueOf(isVerified));
                info.append("妫ｅ啯鎳?闂侇収鍠氶鍫燁殽瀹€鍐: ").append(verified ? "鐎瑰憡鐓￠悰娆戞嫚? : "闁哄牜浜悰娆戞嫚?).append("\n");
            }
            

            info.append("\n妫ｅ啯鏂€ 閻庨潧妫濋幐婊勭┍閳╁啩绱?\n");
            String keyInfo = queryUserKeyInfo(username);
            info.append(keyInfo);
            

            String timestampInfo = queryUserTimestampInfo(username);
            info.append(timestampInfo);
            
            return info.toString();
            
        } catch (Exception e) {
            return "闁?闁哄被鍎撮妤呮偨閵婏箑鐓曞ǎ鍥ｅ墲娴煎懘寮捄鍝勬瘔闂? " + e.getMessage();
        }
    }
    
    
    private static String queryUserKeyInfo(String username) {
        try {

            String sql = "SELECT * FROM user_keys WHERE username = ?";
            java.util.List<java.util.Map<String, Object>> keyResult = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
            
            if (keyResult.isEmpty()) {
                return "  妫ｅ啯鎲?闁活潿鍔嶉崺娑氣偓闈涙閹? 闁哄牜浜為弫鎾诲箣閹单";
            }
            
            StringBuilder keyInfo = new StringBuilder();
            for (java.util.Map<String, Object> key : keyResult) {
                String keyValue = (String) key.get("user_key");
                Object createTime = key.get("create_time");
                Object isActive = key.get("is_active");
                
                keyInfo.append("  妫ｅ啯鎲?閻庨潧妫濋幐? ").append(keyValue.substring(0, Math.min(8, keyValue.length()))).append("...\n");
                keyInfo.append("  妫ｅ啯鎯?闁告帗绋戠紓鎾诲籍閸洘锛? ").append(createTime).append("\n");
                keyInfo.append("  闁?闁绘鍩栭埀? ").append(Boolean.parseBoolean(String.valueOf(isActive)) ? "婵犵鍋撴繛? : "闁哄牜浜濈缓鍝劽?).append("\n");
            }
            
            return keyInfo.toString();
        } catch (Exception e) {
            return "  闁?闁哄被鍎撮妤冣偓闈涙閹告粍绌遍埄鍐х礀闁告垶妞介弫? " + e.getMessage() + "\n";
        }
    }
    
    
    private static String queryUserTimestampInfo(String username) {
        try {

            java.util.List<java.util.Map<String, Object>> timestampResult = yumProxy.net.mysql.MySQLUtils.executeQuery(sql, username);
            
            if (timestampResult.isEmpty()) {
                return "  闁?闁哄啫鐖煎Λ鍧楀箣閾忣偅绠涢柛? 闁哄牜浜濈缓鍝劽虹猾鐖?;
            }
            
            StringBuilder timestampInfo = new StringBuilder();
            for (java.util.Map<String, Object> timestamp : timestampResult) {
                Object activatedAt = timestamp.get("activated_at");
                Object expiresAt = timestamp.get("expires_at");
                Object isActive = timestamp.get("is_active");
                Object isExpired = timestamp.get("is_expired");
                
                timestampInfo.append("  闁?闁哄牆绉存慨鐔兼偐閼哥鍋? ");
                if (Boolean.parseBoolean(String.valueOf(isActive))) {
                    timestampInfo.append("婵犵鍋撴繛?);
                } else {
                    timestampInfo.append("闁哄牜浜濈缓鍝劽?);
                }
                
                if (Boolean.parseBoolean(String.valueOf(isExpired))) {
                    timestampInfo.append(" (鐎规瓕灏换鍐嫉?");
                }
                timestampInfo.append("\n");
                
                if (activatedAt != null) {
                    timestampInfo.append("  妫ｅ啯鎯?婵犵鍋撴繛鑼帛濡炲倿姊? ").append(activatedAt).append("\n");
                }
                if (expiresAt != null) {
                    timestampInfo.append("  闁?閺夆晛娲﹀﹢锟犲籍閸洘锛? ").append(expiresAt).append("\n");
                }
            }
            
            return timestampInfo.toString();
        } catch (Exception e) {
            return "  闁?闁哄被鍎撮妤呭籍閸洘锛熼柟鏉戝帠娣囧﹪骞侀姘瘔闂? " + e.getMessage() + "\n";
        }
    }
    
    
    private static JPanel createUserInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("闁活潿鍔嶉崺娑欑┍閳╁啩绱栭柡灞诲劥椤?));
        
        JTextField usernameField = new JTextField();
        JButton queryBasicBtn = new JButton("闁哄被鍎撮妤呭春閻戞ɑ鎷卞ǎ鍥ｅ墲娴?);
        JButton queryDetailBtn = new JButton("闁哄被鍎撮妤冩嫚閿斿墽鐭庡ǎ鍥ｅ墲娴?);
        JButton queryAllUsersBtn = new JButton("闁哄被鍎撮妤呭箥閳ь剟寮垫径灞炬殢闁?);
        
        inputPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        inputPanel.add(usernameField);
        inputPanel.add(queryBasicBtn);
        inputPanel.add(queryDetailBtn);
        inputPanel.add(queryAllUsersBtn);
        inputPanel.add(new JLabel(""));
        

        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("闁哄被鍎撮妤冪磼閹惧浜?));
        

        queryBasicBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                resultArea.setText("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃?);
                return;
            }
            
            try {
                UserManager userManager = UserApiServer.getUserManager();
                java.util.Map<String, Object> userInfo = userManager.getUserInfo(username);
                
                if (userInfo != null) {
                    StringBuilder info = new StringBuilder();
                    info.append("=== 闁糕晝鍎ゅ﹢浼存偨閵婏箑鐓曞ǎ鍥ｅ墲娴?===\n");
                    info.append("闁活潿鍔嶉崺娑㈠触? ").append(userInfo.get("username")).append("\n");
                    info.append("闂侇収鍠氶? ").append(userInfo.get("email")).append("\n");
                    info.append("缂佹稑顦辨? ").append(userInfo.get("rank")).append("\n");
                    info.append("PID: ").append(userInfo.get("pid")).append("\n");
                    info.append("婵炲鍔岄崬浠嬪籍閸洘锛? ").append(userInfo.get("create_time")).append("\n");
                    resultArea.setText(info.toString());
                } else {
                    resultArea.setText("闁?闁活潿鍔嶉崺娑欑▔瀹ュ懐鎽犻柛? " + username);
                }
            } catch (Exception ex) {
                resultArea.setText("闁?闁哄被鍎撮妤佸緞鏉堫偉袝: " + ex.getMessage());
            }
        });
        
        queryDetailBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                resultArea.setText("閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃?);
                return;
            }
            
            String detailInfo = queryUserFullInfo(username);
            resultArea.setText(detailInfo);
        });
        
        queryAllUsersBtn.addActionListener(e -> {
            try {
                StringBuilder info = new StringBuilder();
                info.append("=== 闁圭鍋撻柡鍫濐槺閺併倝骞嬪畡鏉跨仚閻?===\n\n");
                

                java.util.List<java.util.Map<String, Object>> users = 
                    yumProxy.net.mysql.MySQLUtils.executeQuery(sql);
                
                if (users.isEmpty()) {
                    info.append("闁哄棗鍊瑰Λ銈夋偨閵婏箑鐓曢柡浣哄瀹撲箺n");
                } else {
                    info.append(String.format("%-15s %-25s %-10s %-5s %s\n", 
                               "闁活潿鍔嶉崺娑㈠触?, "闂侇収鍠氶?, "缂佹稑顦辨?, "PID", "婵炲鍔岄崬浠嬪籍閸洘锛?));
                    for (int i = 0; i < 80; i++) info.append("-");
                    info.append("\n");
                    
                    for (java.util.Map<String, Object> user : users) {
                        info.append(String.format("%-15s %-25s %-10s %-5s %s\n",
                            user.get("username"),
                            user.get("email"),
                            user.get("rank"),
                            user.get("pid"),
                            user.get("create_time")
                        ));
                    }
                    info.append("\n闁诡剚妲掗? ").append(users.size()).append(" 濞戞搩浜為弫銈夊箣?);
                }
                
                resultArea.setText(info.toString());
            } catch (Exception ex) {
                resultArea.setText("闁?闁哄被鍎撮妤呮偨閵婏箑鐓曢柛鎺擃殙閵嗗啯寰勬潏顐バ? " + ex.getMessage());
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    
    private static JPanel createBanManagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        

        JPanel operationPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        operationPanel.setBorder(BorderFactory.createTitledBorder("閻忓繋鑳堕々锕傚箼瀹ュ嫮绋?));
        
        JTextField usernameField = new JTextField();
        JTextField reasonField = new JTextField("閺夆晜绻傚鐣岀矆閹冮殬閻熸瑥瀚崹?);
        JTextField durationField = new JTextField("1440");
        JCheckBox permanentBox = new JCheckBox("婵﹢鏅茬粻娆戜焊娴ｄ警娲?);
        
        JButton banBtn = new JButton("閻忓繋鑳堕々锕傛偨閵婏箑鐓?);
        JButton unbanBtn = new JButton("閻熸瑱绲介惃婵嬫偨閵婏箑鐓?);
        JButton checkStatusBtn = new JButton("婵☆偀鍋撻柡灞诲劤婵悂骞€?);
        JButton listBannedBtn = new JButton("閻忓繋鑳堕々锕傚礆濡ゅ嫨鈧?);
        JButton cleanExpiredBtn = new JButton("婵炴挸鎳愰幃濠冩交閸ャ劍鍩?);
        JButton banLogsBtn = new JButton("閻忓繋鑳堕々锕傚籍閵夈儳绠?);
        JButton comprehensiveTestBtn = new JButton("缂備胶鍘ч幃搴∶圭€ｎ厾妲?);
        
        operationPanel.add(new JLabel("闁活潿鍔嶉崺娑㈠触?"));
        operationPanel.add(usernameField);
        operationPanel.add(banBtn);
        
        operationPanel.add(new JLabel("閻忓繋鑳堕々锕傚储閻旈攱绀?"));
        operationPanel.add(reasonField);
        operationPanel.add(unbanBtn);
        
        operationPanel.add(new JLabel("闁哄啫鐖奸弳?闁告帒妫濋幐?:"));
        operationPanel.add(durationField);
        operationPanel.add(checkStatusBtn);
        
        operationPanel.add(permanentBox);
        operationPanel.add(listBannedBtn);
        operationPanel.add(cleanExpiredBtn);
        

        testPanel.add(comprehensiveTestBtn);
        testPanel.setBorder(BorderFactory.createTitledBorder("闁煎浜滄慨鈺呭礌閺嶃劎銈撮悹?));
        

        JTextArea resultArea = new JTextArea(18, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("闁瑰灝绉崇紞鏃傜磼閹惧浜?));
        

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(banLogsBtn);
        

        permanentBox.addActionListener(e -> {
            durationField.setEnabled(!permanentBox.isSelected());
        });
        
        banBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String reason = reasonField.getText().trim();
            String durationStr = durationField.getText().trim();
            
            if (username.isEmpty()) {
                resultArea.setText("闁?閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃?);
                return;
            }
            
            if (reason.isEmpty()) {
                reason = "缂佺媴绱曢幃濠囧川濡櫣娈辩紒?;
            }
            
            try {
                Integer duration = null;
                if (!permanentBox.isSelected()) {
                    duration = Integer.parseInt(durationStr);
                    if (duration <= 0) {
                        resultArea.setText("闁?閻忓繋鑳堕々锕傚籍閸洘姣愰煫鍥ф嚇閵嗗繑寰勮缁?");
                        return;
                    }
                }
                
                UserBanManager.BanResult result = UserBanManager.banUser(username, reason, "TEST_ADMIN", duration);
                
                if (result.success) {
                    StringBuilder info = new StringBuilder();
                    info.append("闁?閻忓繋鑳堕々锕傚箣閹邦剙顫燶n");
                    info.append("闁活潿鍔嶉崺? ").append(username).append("\n");
                    info.append("闁告鍠庡ú? ").append(reason).append("\n");
                    if (duration == null) {
                        info.append("缂侇偉顕ч悗? 婵﹢鏅茬粻娆戜焊娴ｄ警娲n");
                    } else {
                        info.append("缂侇偉顕ч悗? 濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲n");
                        info.append("闁哄啫鐖奸弳? ").append(duration).append(" 闁告帒妫濋幐鎻琻");
                        if (result.bannedUntil != null) {
                            info.append("闁告帞澧楀﹢? ").append(result.bannedUntil).append("\n");
                        }
                    }
                    resultArea.setText(info.toString());
                } else {
                    resultArea.setText("闁?閻忓繋鑳堕々锔藉緞鏉堫偉袝: " + result.message);
                }
            } catch (NumberFormatException ex) {
                resultArea.setText("闁?閻忓繋鑳堕々锕傚籍閸洘姣愰煫鍥ф嚇閵嗗繘寮伴娑欐閻?);
            } catch (Exception ex) {
                resultArea.setText("闁?閻忓繋鑳堕々锕傚箼瀹ュ嫮绋婂鎯扮簿鐟? " + ex.getMessage());
            }
        });
        
        unbanBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                resultArea.setText("闁?閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃?);
                return;
            }
            
            try {
                UserBanManager.BanResult result = UserBanManager.unbanUser(username, "TEST_ADMIN");
                
                if (result.success) {
                    resultArea.setText("闁?閻熸瑱绲介惃婵嬪箣閹邦剙顫? " + username + "\n" + result.message);
                } else {
                    resultArea.setText("闁?閻熸瑱绲介惃婵囧緞鏉堫偉袝: " + result.message);
                }
            } catch (Exception ex) {
                resultArea.setText("闁?閻熸瑱绲介惃婵嬪箼瀹ュ嫮绋婂鎯扮簿鐟? " + ex.getMessage());
            }
        });
        
        checkStatusBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                resultArea.setText("闁?閻犲洨鏌夌欢顓㈠礂閵壯勬殢闁规潙鍢查幃?);
                return;
            }
            
            try {
                UserBanManager.BanStatus status = UserBanManager.checkBanStatus(username);
                
                StringBuilder info = new StringBuilder();
                info.append("=== 閻忓繋鑳堕々锕傛偐閼哥鍋撴担鍦弨閻?===\n");
                info.append("闁活潿鍔嶉崺? ").append(username).append("\n");
                info.append("闁绘鍩栭埀? ").append(status.isBanned ? "鐎瑰憡褰冮惃婵堢矉? : "婵繐绲介悥?).append("\n");
                info.append("閻犲洤鐡ㄥΣ? ").append(status.message).append("\n");
                
                if (status.isBanned) {
                    info.append("闁告鍠庡ú? ").append(status.reason).append("\n");
                    info.append("闁圭瑳鍡╂斀闁? ").append(status.bannedBy).append("\n");
                    if (status.bannedAt != null) {
                        info.append("閻忓繋鑳堕々锕傚籍閸洘锛? ").append(status.bannedAt).append("\n");
                    }
                    if (status.bannedUntil != null) {
                        info.append("闁告帞澧楀﹢锟犲籍閸洘锛? ").append(status.bannedUntil).append("\n");
                        info.append("閻忓繋鑳堕々锔剧尵鐠囪尙鈧? 濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲n");
                    } else {
                        info.append("閻忓繋鑳堕々锔剧尵鐠囪尙鈧? 婵﹢鏅茬粻娆戜焊娴ｄ警娲n");
                    }
                }
                
                resultArea.setText(info.toString());
            } catch (Exception ex) {
                resultArea.setText("闁?闁哄被鍎撮妤佸緞鏉堫偉袝: " + ex.getMessage());
            }
        });
        
        listBannedBtn.addActionListener(e -> {
            try {
                java.util.List<java.util.Map<String, Object>> bannedUsers = UserBanManager.getBannedUsers();
                
                StringBuilder info = new StringBuilder();
                info.append("=== 鐟滅増鎸告晶鐘典焊娴ｄ警娲ｉ柣顫妽閸╂盯宕氬Δ鍕┾偓?===\n\n");
                
                if (bannedUsers.isEmpty()) {
                    info.append("闁哄棗鍊瑰Λ銈囦焊娴ｄ警娲ｉ柣顫妽閸╂矐n");
                } else {
                    info.append(String.format("%-15s %-15s %-20s %s\n", 
                               "闁活潿鍔嶉崺娑㈠触?, "閻忓繋鑳堕々锔剧尵鐠囪尙鈧?, "闁圭瑳鍡╂斀闁?, "闁告鍠庡ú?));
                    for (int i = 0; i < 70; i++) info.append("-");
                    info.append("\n");
                    
                    for (java.util.Map<String, Object> user : bannedUsers) {
                        info.append(String.format("%-15s %-15s %-20s %s\n",
                            user.get("username"),
                            user.get("ban_type"),
                            user.get("banned_by"),
                            user.get("ban_reason")
                        ));
                    }
                    info.append("\n闁诡剚妲掗? ").append(bannedUsers.size()).append(" 濞戞搩浜滈惃婵堢矉娴ｇ儤鏆忛柟?);
                }
                
                resultArea.setText(info.toString());
            } catch (Exception ex) {
                resultArea.setText("闁?闁兼儳鍢茶ぐ鍥╀焊娴ｄ警娲ｉ柛鎺擃殙閵嗗啯寰勬潏顐バ? " + ex.getMessage());
            }
        });
        
        cleanExpiredBtn.addActionListener(e -> {
            try {
                int cleanedCount = UserBanManager.cleanExpiredBans();
                resultArea.setText("闁?婵炴挸鎳愰幃濠勨偓鐟版湰閸ㄦ瓡n婵炴挸鎳愰幃濠冪?" + cleanedCount + " 濞戞搩浜ｇ换鍐嫉閻旂儤鐣卞☉鎾崇摠濡炲倻浜告担渚矗");
            } catch (Exception ex) {
                resultArea.setText("闁?婵炴挸鎳愰幃濠冨緞鏉堫偉袝: " + ex.getMessage());
            }
        });
        
        banLogsBtn.addActionListener(e -> {
            try {
                java.util.List<java.util.Map<String, Object>> logs = UserBanManager.getBanLogs(null, 20);
                
                StringBuilder info = new StringBuilder();
                info.append("=== 闁哄牃鍋撻弶?0闁哄鈧磭娈辩紒鍌欑劍濡晞绠?===\n\n");
                
                if (logs.isEmpty()) {
                    info.append("闁哄棗鍊瑰Λ銈囦焊娴ｄ警娲ｉ柡鍐﹀劚缁绘摶n");
                } else {
                    info.append(String.format("%-12s %-10s %-15s %-15s %s\n", 
                               "闁活潿鍔嶉崺娑㈠触?, "闁瑰灝绉崇紞?, "闁圭瑳鍡╂斀闁?, "闁哄啫鐖煎Λ?, "闁告鍠庡ú?));
                    for (int i = 0; i < 80; i++) info.append("-");
                    info.append("\n");
                    
                    for (java.util.Map<String, Object> log : logs) {
                        info.append(String.format("%-12s %-10s %-15s %-15s %s\n",
                            log.get("username"),
                            log.get("action_type"),
                            log.get("banned_by"),
                            log.get("created_at"),
                            log.get("reason")
                        ));
                    }
                }
                
                resultArea.setText(info.toString());
            } catch (Exception ex) {
                resultArea.setText("闁?闁兼儳鍢茶ぐ鍥籍閵夈儳绠跺鎯扮簿鐟? " + ex.getMessage());
            }
        });
        
        comprehensiveTestBtn.addActionListener(e -> {
            resultArea.setText("妫ｅ啯鏁?鐎殿喒鍋撳┑顔碱儐婢х晫鎮板畝鈧幃锝夊触閸繄娈辩紒鍌欒兌闁绱掗悢鍝ャ偞閻?..\n\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
            

            SwingUtilities.invokeLater(() -> {
                runComprehensiveBanTest(resultArea);
            });
        });
        

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(operationPanel, BorderLayout.NORTH);
        topPanel.add(testPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    
    private static void runComprehensiveBanTest(JTextArea resultArea) {
        StringBuilder result = new StringBuilder();
        result.append("=== 缂備胶鍘ч幃搴ｄ焊娴ｄ警娲ｇ紒顖濆吹缁搫霉鐎ｎ厾妲搁柟韬插劚閹?===\n\n");
        
        String testUser = "test_ban_user_" + System.currentTimeMillis();
        String adminUser = "TEST_ADMIN";
        boolean allTestsPassed = true;
        
        try {

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 闁告帗绋戠紓鎾趁圭€ｎ厾妲搁柣顫妽閸╂矐n");
            appendResult(resultArea, result.toString());
            

            yumProxy.net.mysql.MySQLUtils.executeUpdate(createUserSql, 
                testUser, testUser + "@test.com", "user", 99999);
            result.append("   闁?婵炴潙顑堥惁顖炴偨閵婏箑鐓曢柛鎺撶☉缂傛捇骞嬮幇顒€顫? ").append(testUser).append("\n\n");
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵炴潙顑堥惁顖氼潩闂€鎰暯閻忓繋鑳堕々顩俷");
            appendResult(resultArea, result.toString());
            
            UserBanManager.BanResult banResult = UserBanManager.banUser(testUser, "缂備胶鍘ч幃搴∶圭€ｎ厾妲?婵﹢鏅茬粻娆戜焊娴ｄ警娲?, adminUser, null);
            if (banResult.success) {
                result.append("   闁?婵﹢鏅茬粻娆戜焊娴ｄ警娲ｉ柟瀛樺姇婵矐n");
            } else {
                result.append("   闁?婵﹢鏅茬粻娆戜焊娴ｄ警娲ｅ鎯扮簿鐟? ").append(banResult.message).append("\n");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            appendResult(resultArea, result.toString());
            
            UserBanManager.BanStatus status = UserBanManager.checkBanStatus(testUser);
            if (status.isBanned) {
                result.append("   闁?闁绘鍩栭埀顑跨劍椤ュ懘寮婚妷锔诲妧缁? 闁活潿鍔嶉崺娑橆啅閼奸娼堕悘蹇庤兌椤╊泜n");
                result.append("   妫ｅ啯鎯?閻忓繋鑳堕々锔界┍閳╁啩绱? ").append(status.reason).append("\n");
            } else {
                result.append("   闁?闁绘鍩栭埀顑跨劍椤ュ懘寮婚妷銉ｄ杭閻? 闁活潿鍔嶉崺娑欐償閺冨浂鍤夐悶姘煎亜閻ㄦ繄绮嬫担椋庣ɑ闁绘鍩栭埀顑跨劍濡绮堥悜姗嗗妧閻㈩垳鐝昻");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵☆偀鍋撻柡灞诲劚閻ㄦ繄绮嬫担绋跨仚閻炴稈鏅縩");
            appendResult(resultArea, result.toString());
            
            java.util.List<java.util.Map<String, Object>> bannedUsers = UserBanManager.getBannedUsers();
            boolean userInList = bannedUsers.stream()
                .anyMatch(user -> testUser.equals(user.get("username")));
            
            if (userInList) {
                result.append("   闁?閻忓繋鑳堕々锕傚礆濡ゅ嫨鈧啫螞閳ь剟寮婚妷锔诲妧缁? 闁活潿鍔嶉崺娑㈠礄閾忕懓绠涢柛锔哄妼閻ㄦ繄绮嬫担绋跨仚閻炴稏鍔嬮懙鎱璶");
            } else {
                result.append("   闁?閻忓繋鑳堕々锕傚礆濡ゅ嫨鈧啫螞閳ь剟寮婚妷銉ｄ杭閻? 闁活潿鍔嶉崺娑㈠嫉椤忓嫬姣夐柣婊勬緲濠€顏嗕焊娴ｄ警娲ｉ柛鎺擃殙閵嗗啯绋夐悹顡?);
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵炴潙顑堥惁顖滄喆閿濆懐娈遍柛鏃傚枙閸忔n");
            appendResult(resultArea, result.toString());
            
            UserBanManager.BanResult unbanResult = UserBanManager.unbanUser(testUser, adminUser);
            if (unbanResult.success) {
                result.append("   闁?閻熸瑱绲介惃婵嬪箣閹邦剙顫燶n");
            } else {
                result.append("   闁?閻熸瑱绲介惃婵囧緞鏉堫偉袝: ").append(unbanResult.message).append("\n");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            appendResult(resultArea, result.toString());
            
            UserBanManager.BanStatus statusAfterUnban = UserBanManager.checkBanStatus(testUser);
            if (!statusAfterUnban.isBanned) {
                result.append("   闁?閻熸瑱绲介惃婵嬫偐閼哥鍋撴担璇℃⒕闁哄被鍎查婊呮兜? 闁活潿鍔嶉崺娑橆啅閸欏鍒掑璺虹У椤掓粎鏁粩鍞?);
            } else {
                result.append("   闁?閻熸瑱绲介惃婵嬫偐閼哥鍋撴担璇℃⒕闁哄被鍎遍妵鎴犳嫻? 闁活潿鍔嶉崺娑欑瀹ュ拋妲卞ù婊冮閻ㄦ繄绮嬫担鍝勑﹂柟顑跨n");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵炴潙顑堥惁顖涚▔鐎涙ɑ顦ч悘蹇庤兌椤?(5闁告帒妫濋幐?\n");
            appendResult(resultArea, result.toString());
            
            UserBanManager.BanResult tempBanResult = UserBanManager.banUser(testUser, "缂備胶鍘ч幃搴∶圭€ｎ厾妲?濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲?, adminUser, 5);
            if (tempBanResult.success) {
                result.append("   闁?濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲ｉ柟瀛樺姇婵矐n");
                if (tempBanResult.bannedUntil != null) {
                    result.append("   妫ｅ啯鎯?闁告帞澧楀﹢锟犲籍閸洘锛? ").append(tempBanResult.bannedUntil).append("\n");
                }
            } else {
                result.append("   闁?濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲ｅ鎯扮簿鐟? ").append(tempBanResult.message).append("\n");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵☆偀鍋撻柡灞诲劚閻ㄦ繄绮嬫担瑙勶級闊洦顣痭");
            appendResult(resultArea, result.toString());
            
            java.util.List<java.util.Map<String, Object>> logs = UserBanManager.getBanLogs(testUser, 5);
            if (logs.size() >= 3) {
                for (java.util.Map<String, Object> log : logs) {
                    result.append("   妫ｅ啯鎯?").append(log.get("action_type")).append(": ").append(log.get("reason")).append("\n");
                }
            } else {
                result.append("   闁?閻忓繋鑳堕々锕傚籍閵夈儳绠舵俊顐熷亾闁哄被鍎遍妵鎴犳嫻? 闁哄啨鍎辩换鏃堝极娴兼潙娅ゅ☉鎾崇Х閸?(").append(logs.size()).append("/3)\n");
                allTestsPassed = false;
            }
            appendResult(resultArea, result.toString());
            

            result.append("妫ｅ啯鎲?婵縿鍎甸?: 婵炴挸鎳愰幃濠偯圭€ｎ厾妲搁柡浣哄瀹撲箺n");
            appendResult(resultArea, result.toString());
            

            UserBanManager.unbanUser(testUser, adminUser);
            

            String deleteUserSql = "DELETE FROM users WHERE username = ?";
            yumProxy.net.mysql.MySQLUtils.executeUpdate(deleteUserSql, testUser);
            

            yumProxy.net.mysql.MySQLUtils.executeUpdate(deleteLogsSql, testUser);
            
            result.append("   闁?婵炴潙顑堥惁顖炲极閻楀牆绁︽繛鎾虫噽閹﹦鈧懓鏈崹姝昻\n");
            appendResult(resultArea, result.toString());
            

            result.append("=== 婵炴潙顑堥惁顖炲箑閼姐倗娉?===\n");
            if (allTestsPassed) {
                result.append("妫ｅ啫绔?闁圭鍋撻柡鍫濐槹缁佸鎷犻弴鈶╁亾濮樺磭绠栭柨娑楃閻ㄦ繄绮嬫担娲厙缂備胶鍠曠换宥囨偘鐏炵虎鍔€閻㈩垳鐝昻");
                result.append("闁?婵﹢鏅茬粻娆戜焊娴ｄ警娲ｉ柛鏃傚枙閸忔ê顫㈤敐鍛煑\n");
                result.append("闁?濞戞挸鐡ㄥ鍌滀焊娴ｄ警娲ｉ柛鏃傚枙閸忔ê顫㈤敐鍛煑\n");
                result.append("闁?闁绘鍩栭埀顑跨劍椤ュ懘寮婚妷銉ヮ潬闁煎疇濮ら婊呮暜缁斿攳");
                result.append("闁?閻忓繋鑳堕々锕傚礆濡ゅ嫨鈧啴宕濋悢璇插幋婵繐绲介悥绂眓");
                result.append("闁?閻熸瑱绲介惃婵嬪礉閻旇鍘存慨婵撶到閻栫Ρn");
                result.append("闁?闁哄啨鍎辩换鏃傛媼閺夎法绉块柛鏃傚枙閸忔ê顫㈤敐鍛煑\n");
            } else {
                result.append("闁宠法濯寸粭?闂侇喓鍔岄崹搴∶圭€ｎ厾妲稿鎯扮簿鐟欙箓鏁嶅畝鍐惧殲婵☆偀鍋撻柡灞诲劙缁楀倹娼绘导瀛樻櫓閻犲浂鍨穱濠囧箒閻ュ獧");
            }
            
            result.append("\n婵炴潙顑堥惁顖溾偓鐟版湰閸ㄦ岸寮崼鏇燂紵: ").append(new java.util.Date()).append("\n");
            
        } catch (Exception e) {
            result.append("\n闁?婵炴潙顑堥惁顖炲箥瑜戦、鎴﹀礄濞差亝鏅? ").append(e.getMessage()).append("\n");
            result.append("闂佹寧鐟ㄩ銈囨嫚閿旇棄鍓? ").append(e.getClass().getSimpleName()).append("\n");
            allTestsPassed = false;
        }
        

        

            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });
    }
    
    
    private static void appendResult(JTextArea resultArea, String text) {
        SwingUtilities.invokeLater(() -> {
            resultArea.setText(text);
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
            resultArea.repaint();
        });
        

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 
