package yumProxy.net.Config;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigServer {
    private static final String CONFIG_PATH = "Config/config.properties";
    private static final Gson gson = new Gson();



    public static void handleSmtpGet(HttpExchange exchange) throws IOException {
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "application/json; charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        for (String key : new String[]{"SMTP_HOST","SMTP_PORT","SMTP_USER","SMTP_PASS","SMTP_FROM_NAME"}) {
            result.put(key, ConfigManager.getConfig(key, ""));
        }
        String json = gson.toJson(result);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }


    public static void handleSmtpPost(HttpExchange exchange) throws IOException {
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "application/json; charset=UTF-8");
        String rawBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
            .lines().collect(java.util.stream.Collectors.joining("\n"));
        Map<String, Object> reqData = gson.fromJson(rawBody, Map.class);
        for (String key : new String[]{"SMTP_HOST","SMTP_PORT","SMTP_USER","SMTP_PASS","SMTP_FROM_NAME"}) {
            if (reqData.containsKey(key)) {
                ConfigManager.setConfig(key, reqData.get(key).toString());
            }
        }
        ConfigManager.saveConfig();
        ConfigManager.loadConfig();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "濞ｅ洦绻傞悺銊╁箣閹邦剙顫?);
        String json = gson.toJson(result);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }


    public static void handleConfigPage(HttpExchange exchange) throws IOException {
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "text/html; charset=UTF-8");
        

        String mailApiUrl = ServiceConfig.getMailApiUrl();
        String smtpUser = ServiceConfig.getSmtpUser();
        String smtpPass = ServiceConfig.getSmtpPass();
        String smtpFromName = ServiceConfig.getSmtpFromName();
        

        boolean wsEnabled = ServiceConfig.isWebSocketEnabled();
        boolean httpEnabled = ServiceConfig.isHttpEnabled();
        boolean wsMessageEnabled = ServiceConfig.isWebSocketMessageEnabled();
        boolean httpMessageEnabled = ServiceConfig.isHttpMessageEnabled();
        int wsPort = ServiceConfig.getWebSocketPort();
        int httpPort = ServiceConfig.getHttpPort();
        int configPort = ServiceConfig.getConfigPort();
        
        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <title>YumProxy 闂佹澘绉堕悿鍡欑不閿涘嫭鍊?/title>" +
                "  <meta charset='utf-8'>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }" +
                "    .container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "    h2 { color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; }" +
                "    .form-group { margin-bottom: 15px; }" +
                "    label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }" +
                "    input[type='text'], input[type='number'], input[type='password'] { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }" +
                "    input[type='checkbox'] { margin-right: 8px; }" +
                "    .checkbox-group { margin: 10px 0; }" +
                "    .checkbox-group label { display: inline; font-weight: normal; }" +
                "    button { background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }" +
                "    button:hover { background-color: #0056b3; }" +
                "    .status { padding: 10px; margin: 10px 0; border-radius: 4px; }" +
                "    .status.enabled { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }" +
                "    .status.disabled { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }" +
                "    .current-value { color: #007bff; font-weight: bold; }" +
                "    .section { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 4px; }" +
                "    .info-box { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 5px; padding: 15px; margin-top: 20px; }" +
                "    .info-box h4 { margin: 0 0 10px 0; color: #495057; }" +
                "    .info-box ul { margin: 0; padding-left: 20px; }" +
                "    .info-box li { margin-bottom: 5px; color: #6c757d; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <h1>YumProxy 闂佹澘绉堕悿鍡欑不閿涘嫭鍊?/h1>" +
                (exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("saved=1") ? "<div class='status enabled'>闁?闂佹澘绉堕悿鍡樼┍濠靛棛鎽犻柟瀛樺姇婵盯鏁?br>闁?婵炴垵鐗婃导鍛緞閸曨厽鍊炵€殿喒鍋撻柛蹇撳暱閸戯紕绮╃€ｎ亜绁柣銏㈠枑閺?br>闁?闁哄牆绉存慨鐔奉嚕閳ь剟宕楅崘鍙夊缂佹棏鍨拌ぐ娑㈡煀瀹ュ洨鏋傞梻鍥ｅ亾閻熸洑绶氶崳鎼佸触椤栨稒绠涢柛鏂哄墲婢х娀鎳楅悾灞炬櫢闁?/div>" : "") +
                "    <form id='configForm' method='post' action='/config/setting'>" +
                "      <div class='section'>" +
                "        <h2>妫ｅ啯鏆?闁哄牆绉存慨鐔煎箳瑜嶉崺?/h2>" +
                "        <div class='form-group'>" +
                "          <div class='checkbox-group'>" +
                "            <input type='checkbox' name='websocket_enabled' id='websocket_enabled' " + (wsEnabled ? "checked" : "") + ">" +
                "            <label for='websocket_enabled'>闁告凹鍨抽弫?WebSocket 闁哄牆绉存慨?<span style='color: #dc3545; font-size: 12px;'>(闂傚洠鍋撻梺鎻掔Т閹?</span></label>" +
                "            <span class='current-value'>鐟滅増鎸告晶鐘绘偐閼哥鍋? " + (wsEnabled ? "闁告凹鍨抽弫? : "缂佸倷鑳堕弫?) + "</span>" +
                "          </div>" +
                "          <label>WebSocket 缂佹棏鍨拌ぐ?</label>" +
                "          <input type='number' name='websocket_port' value='" + wsPort + "' min='1' max='65535'>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <div class='checkbox-group'>" +
                "            <input type='checkbox' name='websocket_message_enabled' id='websocket_message_enabled' " + (wsMessageEnabled ? "checked" : "") + ">" +
                "            <label for='websocket_message_enabled'>闁告凹鍨抽弫?WebSocket 婵炴垵鐗婃导鍛緞閸曨厽鍊?<span style='color: #28a745; font-size: 12px;'>(闁绘埈鍘藉ú鍧楀绩?</span></label>" +
                "            <span class='current-value'>鐟滅増鎸告晶鐘绘偐閼哥鍋? " + (wsMessageEnabled ? "闁告凹鍨抽弫? : "缂佸倷鑳堕弫?) + "</span>" +
                "          </div>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <div class='checkbox-group'>" +
                "            <input type='checkbox' name='http_enabled' id='http_enabled' " + (httpEnabled ? "checked" : "") + ">" +
                "            <label for='http_enabled'>闁告凹鍨抽弫?HTTP 闁哄牆绉存慨?<span style='color: #dc3545; font-size: 12px;'>(闂傚洠鍋撻梺鎻掔Т閹?</span></label>" +
                "            <span class='current-value'>鐟滅増鎸告晶鐘绘偐閼哥鍋? " + (httpEnabled ? "闁告凹鍨抽弫? : "缂佸倷鑳堕弫?) + "</span>" +
                "          </div>" +
                "          <label>HTTP 缂佹棏鍨拌ぐ?</label>" +
                "          <input type='number' name='http_port' value='" + httpPort + "' min='1' max='65535'>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <div class='checkbox-group'>" +
                "            <input type='checkbox' name='http_message_enabled' id='http_message_enabled' " + (httpMessageEnabled ? "checked" : "") + ">" +
                "            <label for='http_message_enabled'>闁告凹鍨抽弫?HTTP 婵炴垵鐗婃导鍛緞閸曨厽鍊?<span style='color: #28a745; font-size: 12px;'>(闁绘埈鍘藉ú鍧楀绩?</span></label>" +
                "            <span class='current-value'>鐟滅増鎸告晶鐘绘偐閼哥鍋? " + (httpMessageEnabled ? "闁告凹鍨抽弫? : "缂佸倷鑳堕弫?) + "</span>" +
                "          </div>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <label>闂佹澘绉堕悿鍡涘嫉瀹ュ懎顫ょ紒鏃戝灠瑜?</label>" +
                "          <input type='number' name='config_port' value='" + configPort + "' min='1' max='65535'>" +
                "        </div>" +
                "        <div class='info-box'>" +
                "          <h4>闂佹澘绉堕悿鍡欐嫚鐎涙ɑ顫?/h4>" +
                "          <ul>" +
                "            <li><span style='color: #28a745;'>缂備浇鍎绘竟濠囧冀閸モ晩鍔?/span> - 闁衡偓椤栨稑鐦柣鎴幗濞插潡寮ㄩ惂鍝ョ濞ｅ浂鍠楅弫濂稿触鎼达絿褰岄柛妤€纾弫鎾诲极?/li>" +
                "            <li><span style='color: #dc3545;'>缂佷勘鍨兼竟濠囧冀閸モ晩鍔?/span> - 闂傚洠鍋撻悷鏇氱窔閸ｆ悂宕ラ娑欑疀闁告柡鍓濇晶鐘绘嚄閻ｅ本鏅搁柡?/li>" +
                "            <li>婵炴垵鐗婃导鍛緞閸曨厽鍊炵€殿喒鍋撻柛蹇撳暱瑜板弶绂掗妷銉ф澖闁哄啳鍩栫敮鍫曞礆缁傛摨I閻犲洭鏀遍惇浼存儍閸曨偒妲遍柣?/li>" +
                "            <li>闁哄牆绉存慨鐔奉嚕閳ь剟宕楅搹顐粯闁告帟鍩栭弳锝嗙▔椤忓懏绠涢柛鏃撶磿濞堟垿宕ラ姘楅柣妯垮煐閳?/li>" +
                "          </ul>" +
                "        </div>" +
                "      </div>" +
                "      <div class='section'>" +
                "        <h2>妫ｅ啯鎲?闂侇収鍠曞▎顢I闂佹澘绉堕悿?/h2>" +
                "        <div class='form-group'>" +
                "          <label>闂侇収鍠曞▎顢I闁革附婢樺?</label>" +
                "          <input type='text' name='mail_api_url' value='" + mailApiUrl + "' placeholder='http://45.207.214.44:5000/send-email'>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <label>SMTP闁活潿鍔嶉崺娑㈠触?</label>" +
                "          <input type='text' name='smtp_user' value='" + smtpUser + "' placeholder='sent@yumproxy.top'>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <label>SMTP閻庨潧妫涢悥?</label>" +
                "          <input type='password' name='smtp_pass' value='" + smtpPass + "' placeholder='Aa11111.'>" +
                "        </div>" +
                "        <div class='form-group'>" +
                "          <label>闁告瑦鍨冲▎銏＄閻戞ɑ鈻旂紒鈧崫鍕€崇紒?</label>" +
                "          <input type='text' name='smtp_from_name' value='" + smtpFromName + "' placeholder='YumProxyTeam'>" +
                "        </div>" +
                "        <div class='info-box'>" +
                "          <h4>闂侇収鍠曞▎顢I閻犲洤鐡ㄥΣ?/h4>" +
                "          <ul>" +
                "            <li>闂侇収鍠曞▎顢I闁革附婢樺鍐晬濮橆剦妯嗛梺顔哄姂閸嬫牗绂掔捄鍝勭岛闂侇偂鐒﹀﹢鍥礉閿涘嫭鐣遍柛锔芥緲濞?/li>" +
                "            <li>SMTP閻犱降鍊涢惁澶嬬┍閳╁啩绱栭柨娑欑煯缁卞爼鏌呴幒鏃傝埗闂侇収鍠曞▎顢I闁汇劌瀚鑽ゆ嫚娴ｈ渹绻嗛柟?/li>" +
                "            <li>闁衡偓椤栨稑鐦柣鎴幗濞插潡寮ㄩ惂鍝ョ獥濞ｅ浂鍠楅弫濂稿触鎼达絿褰岄柛妤€纾弫鎾诲极?/li>" +
                "            <li>闂侇収鍠曞▎銏ゅ礃閸涱収鍟囬柨娑欐皑闁绱掗悢鍓佺獥闁煎浜滄慨鈺傚閻樼儵鍋撻幒鎴濈岛濞寸姵婀瑰Ч澶愬Υ娴ｈ鏆ù鐘虫构濮瑰濡存担宄扮槣濡増菧閳ь兛绀侀崬瀵糕偓鐟版贡閻℃垶绌遍埄鍐х礀</li>" +
                "          </ul>" +
                "        </div>" +
                "      </div>" +
                "      <button type='submit'>妫ｅ啯宕?濞ｅ洦绻傞悺銊╂煀瀹ュ洨鏋?/button>" +
                "      <button type='button' onclick='resetToDefault()'>妫ｅ啯鏁?闂佹彃绉堕悿鍡橆渶濡鍚?/button>" +
                "      <button type='button' onclick='restartServer()' style='background-color: #dc3545;'>妫ｅ啯鏁?闂佹彃绉撮幆搴ㄥ嫉瀹ュ懎顫?/button>" +
                "    </form>" +
                "    <script>" +
                "      function resetToDefault() {" +
                "        if (confirm('缁绢収鍠栭悾鍓ф啺娓氣偓閸ｅ摜绱旈鏄忕濮掓稒顭堥濠氭煀瀹ュ洨鏋傞柛姘殣缁?)) {" +
                "          fetch('/api/config/reset', { method: 'POST' })" +
                "            .then(response => response.json())" +
                "            .then(data => {" +
                "              alert('闂佹澘绉堕悿鍡楊啅閺屻儱娅㈢紓鍐惧枙鐠愮喐顪€濡鍚囬柛濠勩€嬬槐?);" +
                "              location.reload();" +
                "            })" +
                "            .catch(error => {" +
                "              alert('闂佹彃绉堕悿鍡樺緞鏉堫偉袝: ' + error);" +
                "            });" +
                "        }" +
                "      }" +
                "      function restartServer() {" +
                "        if (confirm('缁绢収鍠栭悾鍓ф啺娓氣偓閸ｆ悂宕ラ娑欑疀闁告柡鈧櫕鍋嬮柨娑氬枙缁绘牜浜搁崱妞剧不婵縿鍨圭紞瀣礈瀹ュ棙绠涢柛鏂衡偓瀹犲珯闂佹彃绉甸弻濠囧触椤栨艾袟闁?)) {" +
                "          fetch('/api/config/restart', { method: 'POST' })" +
                "            .then(response => response.json())" +
                "            .then(data => {" +
                "              if (data.success) {" +
                "                alert('闁哄牆绉存慨鐔兼煂瀹ュ懏鍎欓柟瀛樺姇婵盯鏁嶆笟鈧妴澶愭閵忕姷娈洪柛?缂佸甯掗幃妤呭礆闁垮鐓€...');" +
                "                setTimeout(() => location.reload(), 3000);" +
                "              } else {" +
                "                alert('闂佹彃绉撮幆搴㈠緞鏉堫偉袝: ' + data.message);" +
                "              }" +
                "            })" +
                "            .catch(error => {" +
                "              alert('闂佹彃绉撮幆搴㈠緞鏉堫偉袝: ' + error);" +
                "            });" +
                "        }" +
                "      }" +
                "    </script>" +
                "  </div>" +
                "</body>" +
                "</html>";
        byte[] resp = html.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }



        String body = new java.io.BufferedReader(new java.io.InputStreamReader(exchange.getRequestBody(), java.nio.charset.StandardCharsets.UTF_8))
            .lines().collect(java.util.stream.Collectors.joining("\n"));
        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(java.net.URLDecoder.decode(kv[0], "UTF-8"), java.net.URLDecoder.decode(kv[1], "UTF-8"));
            }
        }
        

        if (params.containsKey("websocket_enabled")) {
            ServiceConfig.setWebSocketEnabled(true);
        } else {
            ServiceConfig.setWebSocketEnabled(false);
        }
        
        if (params.containsKey("http_enabled")) {
            ServiceConfig.setHttpEnabled(true);
        } else {
            ServiceConfig.setHttpEnabled(false);
        }
        

        if (params.containsKey("websocket_message_enabled")) {
            ServiceConfig.setWebSocketMessageEnabled(true);
        } else {
            ServiceConfig.setWebSocketMessageEnabled(false);
        }
        
        if (params.containsKey("http_message_enabled")) {
            ServiceConfig.setHttpMessageEnabled(true);
        } else {
            ServiceConfig.setHttpMessageEnabled(false);
        }
        
        if (params.containsKey("websocket_port")) {
            try {
                int port = Integer.parseInt(params.get("websocket_port"));
                if (port >= 1 && port <= 65535) {
                    ServiceConfig.setWebSocketPort(port);
                }
            } catch (NumberFormatException e) {

        }
        
        if (params.containsKey("http_port")) {
            try {
                int port = Integer.parseInt(params.get("http_port"));
                if (port >= 1 && port <= 65535) {
                    ServiceConfig.setHttpPort(port);
                }
            } catch (NumberFormatException e) {

        }
        
        if (params.containsKey("config_port")) {
            try {
                int port = Integer.parseInt(params.get("config_port"));
                if (port >= 1 && port <= 65535) {
                    ServiceConfig.setConfigPort(port);
                }
            } catch (NumberFormatException e) {

        }
        

        if (params.containsKey("mail_api_url")) {
            ServiceConfig.setMailApiUrl(params.get("mail_api_url"));
            }
        
        if (params.containsKey("smtp_user")) {
            ServiceConfig.setSmtpUser(params.get("smtp_user"));
        }
        
        if (params.containsKey("smtp_pass")) {
            ServiceConfig.setSmtpPass(params.get("smtp_pass"));
        }
        
        if (params.containsKey("smtp_from_name")) {
            ServiceConfig.setSmtpFromName(params.get("smtp_from_name"));
        }
        

        exchange.getResponseHeaders().set("Location", "/config/setting?saved=1");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
    

        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "application/json; charset=UTF-8");
        
        try {

            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "闂佹澘绉堕悿鍡楊啅閺屻儱娅㈢紓鍐惧枙鐠愮喐顪€濡鍚囬柛?);
            
            String json = gson.toJson(result);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闂佹彃绉堕悿鍡涙煀瀹ュ洨鏋傚鎯扮簿鐟? " + e.getMessage());
            
            String json = gson.toJson(result);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        }
    }
    
    
    public static void handleConfigRestart(HttpExchange exchange) throws IOException {
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "application/json; charset=UTF-8");
        
        try {


            

            Thread restartThread = new Thread(() -> {
                try {

                    

                    

                    System.exit(0);
                    
                } catch (Exception e) {

                }
            });
            
            restartThread.setDaemon(true);
            restartThread.start();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "闁哄牆绉存慨鐔兼煂瀹ュ懏鍎欑€瑰憡褰冮幆搴ㄥ礉?);
            
            String json = gson.toJson(result);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "闂佹彃绉撮幆搴ㄥ嫉瀹ュ懎顫ゅ鎯扮簿鐟? " + e.getMessage());
            
            String json = gson.toJson(result);
            byte[] resp = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        }
    }
    

} 
