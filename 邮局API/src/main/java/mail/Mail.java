package mail;

import mail.api.EmailHttpServer;

public class Mail {


    public String name = "YumProxy";
    public String dev = "XiaoJiuShu";
    public String devQQ = "761863726";
    public String version = "0.1.0";
    public int versionID = 1;

    public static Mail instance;
    private EmailHttpServer httpServer;

    public static void log(String msg) {

    }

    public static void main(String[] args) {
        Mail.instance = new Mail();
        instance.startServer();
    }
    
    public void startServer() {

        httpServer = new EmailHttpServer();
        httpServer.start();
        

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            if (httpServer != null) {
                httpServer.stop();
            }
        }));
    }
}
