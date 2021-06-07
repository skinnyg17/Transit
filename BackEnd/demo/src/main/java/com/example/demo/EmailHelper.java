package com.example.demo;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Email helper.
 */
public class EmailHelper {

    /**
     * The To user email.
     */
    String toUserEmail;
    /**
     * The Subject.
     */
    String subject;
    /**
     * The Body.
     */
    String body;

    String cc;

    /**
     * Instantiates a new Email helper.
     *
     * @param toUserEmail the to user email
     * @param subject     the subject
     * @param body        the body
     */
    public EmailHelper(String toUserEmail, String subject, String body){
        this.toUserEmail = toUserEmail;
        this.subject = subject;
        this.body = body;
    }

    public EmailHelper(String fromUserEmail, String toUserEmail, String subject, String body){
        this.toUserEmail = toUserEmail;
        this.subject = subject;
        this.body = body;
        this.cc=fromUserEmail;
    }

    /**
     * Send email.
     */
    public void sendEmail() {
        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
        emailExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        emailExecutor.shutdown();

    }

    /**
     * Uses SMTP and port 587 to send emails and javax mail authenticator
     */
    private void send(){
        final String username = "bigmovescompany.noreply@gmail.com";
        final String password = "securemoves54321";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("bigmovescompany.noreply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toUserEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWithCC(){
        final String username = "bigmovescompany.noreply@gmail.com";
        final String password = "securemoves54321";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("bigmovescompany.noreply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toUserEmail));
            message.setSubject(subject);
            message.setText(body);
            message.addRecipient(Message.RecipientType.CC,new InternetAddress(cc));


            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
