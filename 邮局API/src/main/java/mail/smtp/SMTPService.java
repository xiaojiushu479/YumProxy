package mail.smtp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import mail.Mail;

public class SMTPService {
    
    private String smtpHost;
    private int smtpPort;
    private String smtpUser;
    private String smtpPass;
    private String smtpFromName;
    
    private Properties properties;
    private Session session;
    
    public SMTPService() {

        this("127.0.0.1", 25, "", "", "");
    }
    
    public SMTPService(String smtpHost, int smtpPort, String smtpUser, String smtpPass, String smtpFromName) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUser = smtpUser;
        this.smtpPass = smtpPass;
        this.smtpFromName = smtpFromName;
        initializeSMTP();
    }
    
    private void initializeSMTP() {
        properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        

        if (smtpUser != null && !smtpUser.isEmpty()) {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "false");
            properties.put("mail.smtp.ssl.trust", smtpHost);
            

            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPass);
                }
            });
        } else {

            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtp.starttls.enable", "false");
            properties.put("mail.smtp.ssl.trust", smtpHost);
            

            session = Session.getInstance(properties);
        }
        

        boolean debug = "true".equalsIgnoreCase(System.getenv("SMTP_DEBUG"));
        session.setDebug(debug);
    }
    
    public boolean sendEmail(String from, String to, String subject, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);
            
            Transport.send(message);

            return true;
            
        } catch (MessagingException e) {

            e.printStackTrace();
            return false;
        }
    }
    
    public boolean sendEmailWithHTML(String from, String to, String subject, String htmlContent) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            
            Transport.send(message);

            return true;
            
        } catch (MessagingException e) {

            e.printStackTrace();
            return false;
        }
    }
} 
