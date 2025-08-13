package yumProxy.net.pay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class EPayClient {
    private final String apiUrl;
    private final String apiMApiUrl;
    private final String pid;
    private final String key;

    public EPayClient(String apiUrl, String apiMApiUrl, String pid, String key) {
        this.apiUrl = apiUrl;
        this.apiMApiUrl = apiMApiUrl;
        this.pid = pid;
        this.key = key;
    }


    public String buildSign(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            String v = params.get(k);
            if (k.equals("sign") || k.equals("sign_type") || v == null || v.isEmpty()) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(k).append("=").append(v);
        }
        sb.append(key);
        return md5(sb.toString());
    }


    public static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String createOrder(Map<String, String> params) throws Exception {

        
        params.put("pid", pid);
        params.put("sign_type", "MD5");
        String sign = buildSign(params);
        params.put("sign", sign);
        


        
        String postData = buildPostData(params);

        
        URL url = new URL(apiMApiUrl);

        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = conn.getResponseCode();

        
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resp.append(line);
            }
        }
        
        String response = resp.toString();

        
        return response;
    }


    public boolean verifyNotify(Map<String, String> params) {
        if (!params.containsKey("sign")) return false;
        String sign = params.get("sign");
        String calcSign = buildSign(params);
        return sign != null && sign.equalsIgnoreCase(calcSign);
    }


    private String buildPostData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append("&");
            sb.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
        }
        return sb.toString();
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }


    public String queryMerchantInfo() throws Exception {
        String url = "https://888.seven-cloud.cn/xpay/epay/api.php?act=query&pid=" + pid + "&key=" + key;
        return httpGet(url);
    }


    public String queryOrder(String outTradeNo, String tradeNo) throws Exception {
        StringBuilder url = new StringBuilder("https://888.seven-cloud.cn/xpay/epay/api.php?act=order&pid=").append(pid).append("&key=").append(key);
        if (tradeNo != null && !tradeNo.isEmpty()) {
            url.append("&trade_no=").append(tradeNo);
        } else if (outTradeNo != null && !outTradeNo.isEmpty()) {
            url.append("&out_trade_no=").append(outTradeNo);
        }
        return httpGet(url.toString());
    }


    public String queryOrders(int limit, int page) throws Exception {
        String url = "https://888.seven-cloud.cn/xpay/epay/api.php?act=orders&pid=" + pid + "&key=" + key + "&limit=" + limit + "&page=" + page;
        return httpGet(url);
    }


    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resp.append(line);
            }
        }
        return resp.toString();
    }
    
    
    public static int calculateHoursFromMoney(String money) {
        try {
            double amount = Double.parseDouble(money);
            

                return 24;
            } else if (amount == 5) {
                return 72;
            } else if (amount == 9) {
                return 168;
            } else if (amount == 18) {
                return 720;
            } else {

                return 0;
            }
        } catch (NumberFormatException e) {

            return 0;
        }
    }
    
    
    public static boolean isSupportedAmount(String money) {
        try {
            double amount = Double.parseDouble(money);
            return amount == 2 || amount == 5 || amount == 9 || amount == 18;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    
    public static double[] getSupportedAmounts() {
        return new double[]{2, 5, 9, 18};
    }
    
    
    public static String getPlanName(String money) {
        try {
            double amount = Double.parseDouble(money);
            switch ((int) amount) {
                case 2: return "闁哄啨鍎卞畷?;
                case 5: return "濞戞挸顦妵澶愬础?;
                case 9: return "闁告稏鍔屽畷?;
                case 18: return "闁哄牆鐗嗗畷?;
                default: return "闁哄牜浜為悡鈩冪附濡ゅ拋妯€";
            }
        } catch (NumberFormatException e) {
            return "闁哄啰濮甸弲銉╂煂閹达富鏉?;
        }
    }
} 
