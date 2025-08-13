package mail.smtp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import mail.Mail;

public class SMTPManager {
    
    private static final int SMTP_THREAD_POOL_SIZE = 10;
    private final ExecutorService smtpExecutor;
    private final ConcurrentHashMap<String, SMTPService> smtpServiceCache;
    
    public SMTPManager() {
        this.smtpExecutor = Executors.newFixedThreadPool(SMTP_THREAD_POOL_SIZE);
        this.smtpServiceCache = new ConcurrentHashMap<>();

    }
    
    
    public CompletableFuture<Boolean> sendEmailAsync(String from, String to, String subject, String content) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SMTPService smtpService = getDefaultSMTPService();
                return smtpService.sendEmail(from, to, subject, content);
            } catch (Exception e) {

                return false;
            }
        }, smtpExecutor);
    }
    
    
    public CompletableFuture<Boolean> sendEmailWithHTMLAsync(String from, String to, String subject, String htmlContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SMTPService smtpService = getDefaultSMTPService();
                return smtpService.sendEmailWithHTML(from, to, subject, htmlContent);
            } catch (Exception e) {

                return false;
            }
        }, smtpExecutor);
    }
    
    
    public CompletableFuture<Boolean> sendEmailWithCustomSMTPAsync(String from, String to, String subject, String content,
                                                                  String smtpHost, int smtpPort, String smtpUser, String smtpPass, String smtpFromName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SMTPService smtpService = getOrCreateSMTPService(smtpHost, smtpPort, smtpUser, smtpPass, smtpFromName);
                return smtpService.sendEmail(from, to, subject, content);
            } catch (Exception e) {

                return false;
            }
        }, smtpExecutor);
    }
    
    
    public CompletableFuture<Boolean> sendEmailWithCustomSMTPHTMLAsync(String from, String to, String subject, String htmlContent,
                                                                      String smtpHost, int smtpPort, String smtpUser, String smtpPass, String smtpFromName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SMTPService smtpService = getOrCreateSMTPService(smtpHost, smtpPort, smtpUser, smtpPass, smtpFromName);
                return smtpService.sendEmailWithHTML(from, to, subject, htmlContent);
            } catch (Exception e) {

                return false;
            }
        }, smtpExecutor);
    }
    
    
    public boolean sendEmail(String from, String to, String subject, String content) {
        try {
            SMTPService smtpService = getDefaultSMTPService();
            return smtpService.sendEmail(from, to, subject, content);
        } catch (Exception e) {

            return false;
        }
    }
    
    
    public boolean sendEmailWithHTML(String from, String to, String subject, String htmlContent) {
        try {
            SMTPService smtpService = getDefaultSMTPService();
            return smtpService.sendEmailWithHTML(from, to, subject, htmlContent);
        } catch (Exception e) {

            return false;
        }
    }
    
    
    private SMTPService getDefaultSMTPService() {
        return smtpServiceCache.computeIfAbsent("default", k -> new SMTPService());
    }
    
    
    private SMTPService getOrCreateSMTPService(String smtpHost, int smtpPort, String smtpUser, String smtpPass, String smtpFromName) {
        String key = smtpHost + ":" + smtpPort + ":" + smtpUser;
        return smtpServiceCache.computeIfAbsent(key, k -> new SMTPService(smtpHost, smtpPort, smtpUser, smtpPass, smtpFromName));
    }
    
    
    public String getThreadPoolStatus() {
        if (smtpExecutor instanceof java.util.concurrent.ThreadPoolExecutor) {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) smtpExecutor;
            return String.format("婵炲弶妲掔粚顒傜棯鐠恒劉鏌? %d, 闁哄秶顭堢缓鍓х棯鐠恒劉鏌? %d, 闁哄牃鍋撳鍫嗗懎娈犵紒? %d, 闂傚啰鍠庨崹顏呭緞瑜嶉惃? %d",
                    tpe.getActiveCount(), tpe.getCorePoolSize(), tpe.getMaximumPoolSize(), tpe.getQueue().size());
        }
        return "缂佹崘娉曢埢鐓幮ч悩闈浶﹂柟? 閺夆晜鍔橀、鎴炵▔?;
    }
    
    
    public void shutdown() {

        

        smtpExecutor.shutdown();
        try {
            if (!smtpExecutor.awaitTermination(30, TimeUnit.SECONDS)) {

                smtpExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {

            smtpExecutor.shutdownNow();
        }
        

        smtpServiceCache.clear();
        

    }
} 
