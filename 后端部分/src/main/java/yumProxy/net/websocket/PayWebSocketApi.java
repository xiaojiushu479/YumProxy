package yumProxy.net.websocket;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import yumProxy.net.pay.EPayClient;
import yumProxy.net.mysql.MySQLUtils;
import yumProxy.net.httpAPI.AuthValidator;
import yumProxy.server.timestamp.TimestampManager;
import yumProxy.net.websocket.WebSocketServer;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PayWebSocketApi {

    // 闂佹澘绉堕悿鍡涘矗閸屾稒娈堕柨娑樼墣椤曨剟寮界憴鍕ウ閻庡湱鍋ゅ顖炲箚閸涱厼鏋屽ǎ鍥跺枟閺佸ジ鏁?    private static final String API_URL = "https://888.seven-cloud.cn/xpay/epay/submit.php";
    private static final String API_MAPI_URL = "https://888.seven-cloud.cn/xpay/epay/mapi.php";
    private static final String PID = "10202";
    private static final String KEY = "zPXjSIVGeBJNFYoXzxWO";


    private static final String BASE_URL = "https://www.yumproxy.top";
    private static final String NOTIFY_URL = BASE_URL + "/api/pay/notify";
    private static final String RETURN_URL = BASE_URL + "/api/pay/return";

    private static final EPayClient client = new EPayClient(API_URL, API_MAPI_URL, PID, KEY);


    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);




    private static final Map<String, java.util.concurrent.ScheduledFuture<?>> orderCheckTasks = new ConcurrentHashMap<>();


        String outTradeNo;
        String username;
        WebSocket client;
        long createTime;
        int checkCount;
        boolean completed;

        OrderTracker(String outTradeNo, String username, WebSocket client) {
            this.outTradeNo = outTradeNo;
            this.username = username;
            this.client = client;
            this.createTime = System.currentTimeMillis();
            this.checkCount = 0;
            this.completed = false;
        }
    }

    private static void log(String msg) {

    }

    
    public static void handlePayCreate(WebSocket conn, JSONObject data) {
        try {

            String type = data.getString("type");
            String outTradeNo = data.getString("out_trade_no");
            String name = data.getString("name");
            String money = data.getString("money");


            String username = data.getString("username");
            String notifyUrl = data.getString("notify_url");
            String returnUrl = data.getString("return_url");
            String clientip = data.getString("clientip");
            String device = data.getString("device");
            String channelId = data.getString("channel_id");


                username = extractUsernameFromOrderImproved(outTradeNo, param);

            }



            Map<String, Object> result = new HashMap<>();


                    outTradeNo == null || outTradeNo.trim().isEmpty() ||
                    name == null || name.trim().isEmpty() ||
                    money == null || money.trim().isEmpty()) {

                result.put("code", 0);
                result.put("msg", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜?);
                sendResponse(conn, "pay_create", result);
                return;
            }


            if (!"alipay".equals(type) && !"wxpay".equals(type)) {

                result.put("code", 0);
                result.put("msg", "濞戞挸绉甸弫顕€骞愭担鐑樼暠闁衡偓椤栨瑧甯涢柡鍌滄嚀缁憋繝鏁嶇仦鑲╃煂闁衡偓椤栨稑鐦產lipay闁告粌鐦峹pay");
                sendResponse(conn, "pay_create", result);
                return;
            }




            params.put("type", type);
            params.put("out_trade_no", outTradeNo);
            params.put("name", name);
            params.put("money", money);


                params.put("param", param);
            }


            if (notifyUrl != null && !notifyUrl.trim().isEmpty()) {
                params.put("notify_url", notifyUrl);
            } else {
                params.put("notify_url", NOTIFY_URL);
            }

            if (returnUrl != null && !returnUrl.trim().isEmpty()) {
                params.put("return_url", returnUrl);
            } else {
                params.put("return_url", RETURN_URL);
            }


            if (clientip != null && !clientip.trim().isEmpty()) {
                params.put("clientip", clientip);
            }


            if (device != null && !device.trim().isEmpty()) {
                params.put("device", device);
            } else {
                params.put("device", "pc");
            }


            if (channelId != null && !channelId.trim().isEmpty()) {
                params.put("channel_id", channelId);
            }


            String payResult = client.createOrder(params);








            }


            Map<String, String> resultMap = parseJsonToMap(payResult);


            Map<String, String> bill = new HashMap<>(params);
            bill.put("trade_no", resultMap.getOrDefault("trade_no", ""));
            bill.put("status", resultMap.getOrDefault("code", "0"));
            bill.put("payurl", resultMap.getOrDefault("payurl", ""));
            bill.put("qrcode", resultMap.getOrDefault("qrcode", ""));

            try {
                MySQLUtils.insertBill(bill);

            } catch (Exception e) {

            }


            result.put("code", code);
            result.put("msg", resultMap.getOrDefault("msg", ""));
            result.put("trade_no", resultMap.getOrDefault("trade_no", ""));
            result.put("payurl", resultMap.getOrDefault("payurl", ""));
            result.put("qrcode", resultMap.getOrDefault("qrcode", ""));
            result.put("urlscheme", resultMap.getOrDefault("urlscheme", ""));
            result.put("money", resultMap.getOrDefault("money", money));
            result.put("out_trade_no", outTradeNo);


            result.put("type", type);
            result.put("name", name);


            result.put("notify_url", params.get("notify_url"));
            result.put("return_url", params.get("return_url"));


                startOrderTracking(outTradeNo, username, conn);
                result.put("auto_tracking", true);
                result.put("tracking_message", "閻犱降鍨瑰畷鐔兼偐閼哥鍋撴担鍝ユ闁煎浜滄慨鈺冩崉閻斿鍤嬮柨娑樻湰閺侇喗绂掑Ο铏规殮闁瑰瓨鍔曢幃妤佸濮橆偄鐦滈柛鏂诲姂閳ь剚姘ㄩ悡?);
            }

            sendResponse(conn, "pay_create", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "pay_create", errorResult);
        }
    }

    
    private static void startOrderTracking(String outTradeNo, String username, WebSocket client) {




        OrderTracker tracker = new OrderTracker(outTradeNo, username, client);
        orderTrackers.put(outTradeNo, tracker);


            try {
                checkOrderStatus(outTradeNo);
            } catch (Exception e) {

            }
        }, 30, 30, TimeUnit.SECONDS);


        orderCheckTasks.put(outTradeNo, task);
    }

    
    private static void stopOrderTracking(String outTradeNo) {

        java.util.concurrent.ScheduledFuture<?> task = orderCheckTasks.remove(outTradeNo);
        if (task != null) {
            task.cancel(false);

        }


        if (tracker != null) {
            tracker.completed = true;

        }
    }

    
    private static void checkOrderStatus(String outTradeNo) {
        OrderTracker tracker = orderTrackers.get(outTradeNo);
        if (tracker == null || tracker.completed) {
            return;
        }


            if (processedOrders.containsKey(outTradeNo)) {

                stopOrderTracking(outTradeNo);
                return;
            }
        }

        tracker.checkCount++;


        try {

            Map<String, String> orderMap = parseJsonToMap(orderInfo);

            String status = orderMap.get("status");
            if ("1".equals(status)) {




                synchronized (processedOrders) {
                    if (!processedOrders.containsKey(outTradeNo)) {
                        processedOrders.put(outTradeNo, true);
                        handlePaymentSuccess(tracker, orderMap);
                    } else {

                    }
                }

                stopOrderTracking(outTradeNo);
            } else if (tracker.checkCount >= 20) {

                sendPaymentTimeout(tracker);
                stopOrderTracking(outTradeNo);
            }
        } catch (Exception e) {

            if (tracker.checkCount >= 20) {
                stopOrderTracking(outTradeNo);
            }
        }
    }

    
    private static void handlePaymentSuccess(OrderTracker tracker, Map<String, String> orderMap) {
        try {

            String param = orderMap.get("param");


                int hours = yumProxy.net.pay.EPayClient.calculateHoursFromMoney(money);
                if (hours > 0) {
                    boolean activated = TimestampManager.activatePlayer(tracker.username, hours);
                    if (activated) {



                        if (info != null) {

                            String expiresAtStr = "null";
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

                                    ", 婵犵鍋撴繛鑼帛濡炲倿姊? " + activatedAtStr +
                                    ", 闁告帞澧楀﹢锟犲籍閸洘锛? " + expiresAtStr +
                                    ", 闁哄嫷鍨伴幆浣糕攽閳ь剙煤? " + info.isActive);
                        }
                    } else {

                    }
                }
            }


            sendPaymentSuccessNotification(tracker, orderMap);

        } catch (Exception e) {

        }
    }

    
    private static void sendPaymentSuccessNotification(OrderTracker tracker, Map<String, String> orderMap) {
        try {
            JSONObject notification = new JSONObject();
            notification.put("type", "payment_success");
            notification.put("out_trade_no", tracker.outTradeNo);
            notification.put("username", tracker.username);
            notification.put("order_info", orderMap);
            notification.put("timestamp", System.currentTimeMillis());


            if (money != null) {
                int hours = yumProxy.net.pay.EPayClient.calculateHoursFromMoney(money);
                notification.put("hours", hours);
            }


                tracker.client.send(notification.toJSONString());

            } else {

            }




        } catch (Exception e) {

        }
    }

    
    private static void sendPaymentTimeout(OrderTracker tracker) {
        try {
            JSONObject notification = new JSONObject();
            notification.put("type", "payment_timeout");
            notification.put("out_trade_no", tracker.outTradeNo);
            notification.put("username", tracker.username);
            notification.put("message", "閻犱降鍨瑰畷鐔煎绩椤栨瑧甯涢悺鎺戞噺濡炲倿鏁嶅畝鍐惧殲闂佹彃绉甸弻濠囧矗閹达絾宕抽柡鈧娆戝笡");
            notification.put("timestamp", System.currentTimeMillis());

            if (tracker.client != null && tracker.client.isOpen()) {
                tracker.client.send(notification.toJSONString());

            }

        } catch (Exception e) {

        }
    }

    
    public static void handlePayNotify(WebSocket conn, JSONObject data) {
        try {
            Map<String, String> params = new HashMap<>();
            for (String key : data.keySet()) {
                params.put(key, data.getString(key));
            }




            Map<String, Object> result = new HashMap<>();


            String tradeNo = params.get("trade_no");
            String outTradeNo = params.get("out_trade_no");
            String type = params.get("type");
            String name = params.get("name");
            String money = params.get("money");
            String tradeStatus = params.get("trade_status");
            String sign = params.get("sign");
            String signType = params.get("sign_type");
            String param = params.get("param");


                    type == null || name == null || money == null ||
                    tradeStatus == null || sign == null) {

                result.put("code", 400);
                result.put("msg", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜?);
                result.put("valid", false);
                result.put("response", "fail");
                sendResponse(conn, "pay_notify", result);
                return;
            }


            synchronized (processedOrders) {
                if (processedOrders.containsKey(outTradeNo)) {

                    result.put("code", 200);
                    result.put("msg", "閻犱降鍨瑰畷鐔奉啅閹绘帩妲遍柣?);
                    result.put("valid", true);
                    result.put("response", "success");
                    sendResponse(conn, "pay_notify", result);
                    return;
                }
            }


            boolean valid = client.verifyNotify(params);

            if (valid) {




                    synchronized (processedOrders) {
                        if (processedOrders.containsKey(outTradeNo)) {

                            result.put("code", 200);
                            result.put("msg", "閻犱降鍨瑰畷鐔奉啅閹绘帩妲遍柣?);
                            result.put("valid", true);
                            result.put("response", "success");
                            sendResponse(conn, "pay_notify", result);
                            return;
                        }
                        processedOrders.put(outTradeNo, true);
                    }


                    if (username != null && !username.isEmpty()) {

                        if (hours > 0) {

                            if (activated) {





                            } else {

                            }
                        }
                    }


                    stopOrderTracking(outTradeNo);


                    OrderTracker tracker = orderTrackers.get(outTradeNo);
                    sendPaymentSuccessNotification(tracker, params);

                    result.put("code", 200);
                    result.put("msg", "success");
                    result.put("valid", true);
                    result.put("response", "success");
                    Map<String, String> orderInfo = new HashMap<>();
                    orderInfo.put("pid", pid);
                    orderInfo.put("trade_no", tradeNo);
                    orderInfo.put("out_trade_no", outTradeNo);
                    orderInfo.put("type", type);
                    orderInfo.put("name", name);
                    orderInfo.put("money", money);
                    orderInfo.put("trade_status", tradeStatus);
                    orderInfo.put("param", param);
                    result.put("order_info", orderInfo);
                } else {

                    result.put("code", 200);
                    result.put("msg", "闁衡偓椤栨瑧甯涢柣妯垮煐閳ь兛妞掔粭澶愬及椤栨稑鐏囬柛?);
                    result.put("valid", false);
                    result.put("response", "success");
                }
            } else {

                result.put("code", 400);
                result.put("msg", "缂佹稒鍎抽幃鏇燁殽瀹€鍐濠㈡儼绮剧憴?);
                result.put("valid", false);
                result.put("response", "fail");
            }

            sendResponse(conn, "pay_notify", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", e.getMessage());
            errorResult.put("valid", false);
            errorResult.put("response", "fail");
            sendResponse(conn, "pay_notify", errorResult);
        }
    }

    
    public static String handleHttpPayNotify(Map<String, String> params) {
        try {




            String tradeNo = params.get("trade_no");
            String outTradeNo = params.get("out_trade_no");
            String type = params.get("type");
            String name = params.get("name");
            String money = params.get("money");
            String tradeStatus = params.get("trade_status");
            String sign = params.get("sign");
            String signType = params.get("sign_type");
            String param = params.get("param");


                    type == null || name == null || money == null ||
                    tradeStatus == null || sign == null) {

                return "fail";
            }


            boolean valid = client.verifyNotify(params);

            if (valid) {




                    if (username != null && !username.isEmpty()) {

                        if (hours > 0) {


                        }
                    }


                        Map<String, String> updateParams = new HashMap<>();
                        updateParams.put("out_trade_no", outTradeNo);
                        updateParams.put("trade_no", tradeNo);
                        updateParams.put("status", "1");
                        updateParams.put("money", money);
                        updateParams.put("type", type);
                        updateParams.put("name", name);
                        updateParams.put("param", param);



                    } catch (Exception e) {

                    }


                    OrderTracker tracker = orderTrackers.get(outTradeNo);
                    if (tracker != null) {
                        orderTrackers.remove(outTradeNo);
                        tracker.completed = true;



                    }


                    notification.put("type", "payment_success");
                    notification.put("out_trade_no", outTradeNo);
                    notification.put("username", username);
                    notification.put("order_info", params);
                    notification.put("timestamp", System.currentTimeMillis());
                    notification.put("source", "http_notify");

                    WebSocketServer.broadcastToAll(notification);

                    return "success";
                } else {

                    return "success";
                }
            } else {

                return "fail";
            }

        } catch (Exception e) {

            return "fail";
        }
    }

    
    public static String handleHttpPayReturn(Map<String, String> params) {
        try {




            String tradeNo = params.get("trade_no");
            String outTradeNo = params.get("out_trade_no");
            String type = params.get("type");
            String name = params.get("name");
            String money = params.get("money");
            String tradeStatus = params.get("trade_status");
            String sign = params.get("sign");
            String signType = params.get("sign_type");
            String param = params.get("param");


                    type == null || name == null || money == null ||
                    tradeStatus == null || sign == null) {

                return "闁告瑥鍊归弳鐔兼煥濞嗘帩鍤?;
            }


            boolean valid = client.verifyNotify(params);

            if (valid) {






                    StringBuilder html = new StringBuilder();
                    html.append("<!DOCTYPE html>");
                    html.append("<html><head><title>闁衡偓椤栨瑧甯涢柟瀛樺姇婵?/title></head><body>");
                    html.append("<h1>闁衡偓椤栨瑧甯涢柟瀛樺姇婵盯鏁?/h1>");
                    html.append("<p>閻犱降鍨瑰畷鐔煎矗? ").append(outTradeNo).append("</p>");
                    html.append("<p>闁哄嫭鎸婚弫顔界濡鍚傞柛妤佹礀瑜? ").append(tradeNo).append("</p>");
                    html.append("<p>闁衡偓椤栨瑧甯涢梺鍙夊灴椤? ").append(money).append("闁?/p>");
                    html.append("<p>闁衡偓椤栨瑧甯涢柡鍌滄嚀缁? ").append(type).append("</p>");
                    if (username != null && !username.isEmpty()) {
                        html.append("<p>闁活潿鍔嶉崺娑㈠触? ").append(username).append("</p>");
                    }
                    html.append("<p>闁衡偓椤栨瑧甯涢柡鍐ㄧ埣濡? ").append(
                            java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Shanghai"))
                                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    ).append("</p>");
                    html.append("<script>");
                    html.append("setTimeout(function() { window.close(); }, 5000);");
                    html.append("</script>");
                    html.append("</body></html>");

                    return html.toString();
                } else {

                    return "闁衡偓椤栨瑧甯涘鎯扮簿鐟欙箓鏁嶅畝鈧慨鎼佸箑? " + tradeStatus;
                }
            } else {

                return "缂佹稒鍎抽幃鏇燁殽瀹€鍐濠㈡儼绮剧憴?;
            }

        } catch (Exception e) {

            return "濠㈣泛瀚幃濠冨緞鏉堫偉袝: " + e.getMessage();
        }
    }

    
    public static void handlePayForm(WebSocket conn, JSONObject data) {
        try {

            String type = data.getString("type");
            String outTradeNo = data.getString("out_trade_no");
            String name = data.getString("name");
            String money = data.getString("money");


            String notifyUrl = data.getString("notify_url");
            String returnUrl = data.getString("return_url");
            String clientip = data.getString("clientip");
            String device = data.getString("device");
            String channelId = data.getString("channel_id");



            Map<String, Object> result = new HashMap<>();


                    outTradeNo == null || outTradeNo.trim().isEmpty() ||
                    name == null || name.trim().isEmpty() ||
                    money == null || money.trim().isEmpty()) {

                result.put("code", 0);
                result.put("msg", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜?);
                sendResponse(conn, "pay_form", result);
                return;
            }


            if (!"alipay".equals(type) && !"wxpay".equals(type)) {

                result.put("code", 0);
                result.put("msg", "濞戞挸绉甸弫顕€骞愭担鐑樼暠闁衡偓椤栨瑧甯涢柡鍌滄嚀缁憋繝鏁嶇仦鑲╃煂闁衡偓椤栨稑鐦產lipay闁告粌鐦峹pay");
                sendResponse(conn, "pay_form", result);
                return;
            }




            params.put("type", type);
            params.put("out_trade_no", outTradeNo);
            params.put("name", name);
            params.put("money", money);


                params.put("param", param);
            }


            if (notifyUrl != null && !notifyUrl.trim().isEmpty()) {
                params.put("notify_url", notifyUrl);
            } else {
                params.put("notify_url", NOTIFY_URL);
            }

            if (returnUrl != null && !returnUrl.trim().isEmpty()) {
                params.put("return_url", returnUrl);
            } else {
                params.put("return_url", RETURN_URL);
            }


            if (clientip != null && !clientip.trim().isEmpty()) {
                params.put("clientip", clientip);
            }


            if (device != null && !device.trim().isEmpty()) {
                params.put("device", device);
            } else {
                params.put("device", "pc");
            }


            if (channelId != null && !channelId.trim().isEmpty()) {
                params.put("channel_id", channelId);
            }



            result.put("code", 200);
            result.put("msg", "success");
            result.put("pay_url", payUrl);
            result.put("out_trade_no", outTradeNo);
            result.put("type", type);
            result.put("name", name);
            result.put("money", money);
            result.put("notify_url", params.get("notify_url"));
            result.put("return_url", params.get("return_url"));
            result.put("device", params.get("device"));
            if (param != null && !param.trim().isEmpty()) {
                result.put("param", param);
            }

            sendResponse(conn, "pay_form", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "pay_form", errorResult);
        }
    }

    
    public static void handleStopTracking(WebSocket conn, JSONObject data) {
        try {
            String outTradeNo = data.getString("out_trade_no");
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");



            Map<String, Object> result = new HashMap<>();


            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "stop_tracking", result);
                return;
            }

            OrderTracker tracker = orderTrackers.get(outTradeNo);
            if (tracker != null) {
                orderTrackers.remove(outTradeNo);
                tracker.completed = true;

                result.put("code", 200);
                result.put("msg", "success");
                result.put("message", "閻犱降鍨瑰畷鐔烘崉閻斿鍤嬬€瑰憡褰冩禒鐘差潰?);
                result.put("out_trade_no", outTradeNo);

            } else {
                result.put("code", 404);
                result.put("msg", "Order not found");
                result.put("message", "閻犱降鍨瑰畷鐔哥▔瀹ュ懏韬悹铏瑰枙闁叉粓宕氬Δ鍕┾偓鍐╃▔?);
                result.put("out_trade_no", outTradeNo);
            }

            sendResponse(conn, "stop_tracking", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "stop_tracking", errorResult);
        }
    }

    
    public static void handleGetTrackingStatus(WebSocket conn, JSONObject data) {
        try {
            String username = data.getString("username");
            String userKey = data.getString("user_key");
            String superKey = data.getString("super_key");



            Map<String, Object> result = new HashMap<>();


            if (!AuthValidator.hasPermission(username, userKey, superKey)) {

                result.put("code", 403);
                result.put("msg", "Forbidden");
                result.put("message", "User key or super key required");
                sendResponse(conn, "get_tracking_status", result);
                return;
            }

            List<Map<String, Object>> trackingOrders = new ArrayList<>();

            for (OrderTracker tracker : orderTrackers.values()) {

                if (!AuthValidator.isAdmin(superKey) && !username.equals(tracker.username)) {
                    continue;
                }

                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("out_trade_no", tracker.outTradeNo);
                orderInfo.put("username", tracker.username);
                orderInfo.put("create_time", tracker.createTime);
                orderInfo.put("check_count", tracker.checkCount);
                orderInfo.put("completed", tracker.completed);
                orderInfo.put("elapsed_time", System.currentTimeMillis() - tracker.createTime);

                trackingOrders.add(orderInfo);
            }

            result.put("code", 200);
            result.put("msg", "success");
            result.put("tracking_orders", trackingOrders);
            result.put("total_count", trackingOrders.size());

            sendResponse(conn, "get_tracking_status", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("msg", "Internal Server Error");
            errorResult.put("message", e.getMessage());
            sendResponse(conn, "get_tracking_status", errorResult);
        }
    }

    
    private static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
        }
        return sb.toString();
    }

    
    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    
    public static void handleMerchantInfo(WebSocket conn, JSONObject data) {
        try {


            Map<String, Object> result = new HashMap<>();

            String merchantInfo = client.queryMerchantInfo();
            Map<String, String> infoMap = parseJsonToMap(merchantInfo);

            result.put("code", Integer.parseInt(infoMap.getOrDefault("code", "0")));
            result.put("msg", infoMap.getOrDefault("msg", ""));
            result.put("merchant_info", infoMap);

            sendResponse(conn, "merchant_info", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "merchant_info", errorResult);
        }
    }

    
    public static void handleOrderQuery(WebSocket conn, JSONObject data) {
        try {
            String outTradeNo = data.getString("out_trade_no");
            String tradeNo = data.getString("trade_no");



            Map<String, Object> result = new HashMap<>();

            String orderInfo = client.queryOrder(outTradeNo, tradeNo);
            Map<String, String> orderMap = parseJsonToMap(orderInfo);

            result.put("code", Integer.parseInt(orderMap.getOrDefault("code", "0")));
            result.put("msg", orderMap.getOrDefault("msg", ""));
            result.put("order_info", orderMap);

            sendResponse(conn, "order_query", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "order_query", errorResult);
        }
    }

    
    public static void handleOrderList(WebSocket conn, JSONObject data) {
        try {
            String limitStr = data.getString("limit");
            String pageStr = data.getString("page");

            int limit = 20, page = 1;
            if (limitStr != null && !limitStr.trim().isEmpty()) {
                try {
                    limit = Integer.parseInt(limitStr);
                } catch (Exception ignored) {
                }
            }
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (Exception ignored) {
                }
            }



            Map<String, Object> result = new HashMap<>();

            String ordersInfo = client.queryOrders(limit, page);
            Map<String, String> ordersMap = parseJsonToMap(ordersInfo);

            result.put("code", Integer.parseInt(ordersMap.getOrDefault("code", "0")));
            result.put("msg", ordersMap.getOrDefault("msg", ""));
            result.put("orders_info", ordersMap);
            result.put("limit", limit);
            result.put("page", page);

            sendResponse(conn, "order_list", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "order_list", errorResult);
        }
    }

    
    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {

        }
        return map;
    }

    
    private static String extractUsernameFromOrder(String outTradeNo, String param) {

        if (param != null && !param.trim().isEmpty()) {
            return param.trim();
        }


        if (outTradeNo != null && outTradeNo.contains("_")) {
            String[] parts = outTradeNo.split("_");
            if (parts.length > 0) {
                return parts[0];
            }
        }

        return null;
    }


    
    public static void handleApiPayCreate(WebSocket conn, JSONObject data) {
        try {

            String type = data.getString("type");
            String outTradeNo = data.getString("out_trade_no");
            String name = data.getString("name");
            String money = data.getString("money");


            String username = data.getString("username");
            String notifyUrl = data.getString("notify_url");
            String returnUrl = data.getString("return_url");
            String clientip = data.getString("clientip");
            String device = data.getString("device");
            String channelId = data.getString("channel_id");



            Map<String, Object> result = new HashMap<>();


                    outTradeNo == null || outTradeNo.trim().isEmpty() ||
                    name == null || name.trim().isEmpty() ||
                    money == null || money.trim().isEmpty()) {

                result.put("code", 0);
                result.put("msg", "闁告瑥鍊归弳鐔虹磽閸濆嫨浜?);
                sendResponse(conn, "api_pay_create", result);
                return;
            }


            if (!"alipay".equals(type) && !"wxpay".equals(type)) {

                result.put("code", 0);
                result.put("msg", "濞戞挸绉甸弫顕€骞愭担鐑樼暠闁衡偓椤栨瑧甯涢柡鍌滄嚀缁憋繝鏁嶇仦鑲╃煂闁衡偓椤栨稑鐦產lipay闁告粌鐦峹pay");
                sendResponse(conn, "api_pay_create", result);
                return;
            }




            params.put("type", type);
            params.put("out_trade_no", outTradeNo);
            params.put("name", name);
            params.put("money", money);


                params.put("param", param);
            }


            if (notifyUrl != null && !notifyUrl.trim().isEmpty()) {
                params.put("notify_url", notifyUrl);
            } else {
                params.put("notify_url", NOTIFY_URL);
            }

            if (returnUrl != null && !returnUrl.trim().isEmpty()) {
                params.put("return_url", returnUrl);
            } else {
                params.put("return_url", RETURN_URL);
            }


            if (clientip != null && !clientip.trim().isEmpty()) {
                params.put("clientip", clientip);
            }


            if (device != null && !device.trim().isEmpty()) {
                params.put("device", device);
            } else {
                params.put("device", "pc");
            }


            if (channelId != null && !channelId.trim().isEmpty()) {
                params.put("channel_id", channelId);
            }


            String payResult = client.createOrder(params);



            Map<String, String> resultMap = parseJsonToMap(payResult);


            Map<String, String> bill = new HashMap<>(params);
            bill.put("trade_no", resultMap.getOrDefault("trade_no", ""));
            bill.put("status", resultMap.getOrDefault("code", "0"));
            bill.put("payurl", resultMap.getOrDefault("payurl", ""));
            bill.put("qrcode", resultMap.getOrDefault("qrcode", ""));

            try {
                MySQLUtils.insertBill(bill);

            } catch (Exception e) {

            }


            result.put("code", code);
            result.put("msg", resultMap.getOrDefault("msg", ""));
            result.put("trade_no", resultMap.getOrDefault("trade_no", ""));
            result.put("payurl", resultMap.getOrDefault("payurl", ""));
            result.put("qrcode", resultMap.getOrDefault("qrcode", ""));
            result.put("urlscheme", resultMap.getOrDefault("urlscheme", ""));
            result.put("money", resultMap.getOrDefault("money", money));


                startOrderTracking(outTradeNo, username, conn);
                result.put("auto_tracking", true);
                result.put("tracking_message", "閻犱降鍨瑰畷鐔兼偐閼哥鍋撴担鍝ユ闁煎浜滄慨鈺冩崉閻斿鍤嬮柨娑樻湰閺侇喗绂掑Ο铏规殮闁瑰瓨鍔曢幃妤佸濮橆偄鐦滈柛鏂诲姂閳ь剚姘ㄩ悡?);
            }

            sendResponse(conn, "api_pay_create", result);

        } catch (Exception e) {

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 0);
            errorResult.put("msg", e.getMessage());
            sendResponse(conn, "api_pay_create", errorResult);
        }
    }

    
    private static void sendResponse(WebSocket conn, String action, Map<String, Object> data, String requestId) {
        JSONObject response = new JSONObject();
        response.put("type", "api_response");
        response.put("action", action);
        response.put("status", "success");
        response.put("data", data);
        response.put("request_id", requestId);
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toJSONString());
    }

    
    private static void sendResponse(WebSocket conn, String action, Map<String, Object> data) {
        sendResponse(conn, action, data, null);
    }

    
    private static String extractUsernameFromOrderImproved(String outTradeNo, String param) {


            try {
                if (param.startsWith("{") && param.endsWith("}")) {
                    com.alibaba.fastjson.JSONObject paramJson = com.alibaba.fastjson.JSONObject.parseObject(param);
                    String usernameFromJson = paramJson.getString("username");
                    if (usernameFromJson != null && !usernameFromJson.trim().isEmpty()) {
                        return usernameFromJson.trim();
                    }
                }

            } catch (Exception e) {

                return param.trim();
            }
        }


        if (outTradeNo != null) {

            if (outTradeNo.contains("@")) {
                return outTradeNo.split("@")[0];
            }

            if (outTradeNo.contains("_")) {
                String[] parts = outTradeNo.split("_");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    return parts[0];
                }
            }
        }


    }

    
    private static void updateOrderStatus(String outTradeNo, Map<String, String> paymentInfo) {
        try {
            String updateSql = "UPDATE bills SET status = ?, trade_no = ?, updated_at = NOW() WHERE out_trade_no = ?";
            MySQLUtils.executeUpdate(updateSql, "1", paymentInfo.get("trade_no"), outTradeNo);

        } catch (Exception e) {

        }
    }

    
    private static void recordUserActivation(String username, String outTradeNo, Map<String, String> paymentInfo, int hours) {
        try {
            String insertSql = "INSERT INTO user_payment_history (username, out_trade_no, trade_no, " +
                             "payment_amount, hours_added, payment_method, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            MySQLUtils.executeUpdate(insertSql, 
                username, 
                outTradeNo, 
                paymentInfo.get("trade_no"),
                paymentInfo.get("money"),
                hours,
                paymentInfo.get("type")
            );

        } catch (Exception e) {

        }
    }
}
