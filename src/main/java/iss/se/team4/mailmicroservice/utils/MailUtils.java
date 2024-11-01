package iss.se.team4.mailmicroservice.utils;

import iss.se.team4.mailmicroservice.exception.SendMailException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class MailUtils {
    private static Session session;
    private static String user;

    private MimeMessage msg;
    private String text;
    private String html;
    private List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

    private MailUtils() {

    }

    public static Properties defaultConfig(Boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", null != debug ? debug.toString() : "false");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.port", "465");
        return props;
    }
    /**
     * smtp qq
     *
     * @param debug enable debug
     * @return
     */
    public static Properties SMTP_QQ(boolean debug) {
        Properties props = defaultConfig(debug);
        props.put("mail.smtp.host", "smtp.qq.com");
        return props;
    }
    /**
     * smtp 163
     *
     * @param debug enable debug
     * @return
     */
    public static Properties SMTP_163(Boolean debug) {
        Properties props = defaultConfig(debug);
        props.put("mail.smtp.host", "smtp.163.com");
        return props;
    }
    /**
     * config username and password
     *
     * @param props    email property config
     * @param username email auth username
     * @param password email auth password
     */
    public static void config(Properties props, final String username, final String password) {
        props.setProperty("username", username);
        props.setProperty("password", password);
        config(props);
    }

    public static void config(Properties props) {
        final String username = props.getProperty("username");
        final String password = props.getProperty("password");
        user = username;
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * set email subject
     *
     * @param subject subject title
     */
    public static MailUtils subject(String subject) throws SendMailException {
        MailUtils mailUtils = new MailUtils();
        mailUtils.msg = new MimeMessage(session);
        try {
            mailUtils.msg.setSubject(subject, "UTF-8");
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return mailUtils;
    }

    /**
     * set email from
     *
     * @param nickName from nickname
     */
    public MailUtils from(String nickName) throws SendMailException {
        return from(nickName, user);
    }

    /**
     * set email nickname and from user
     *
     * @param nickName from nickname
     * @param from     from email
     */
    public MailUtils from(String nickName, String from) throws SendMailException {
        try {
            String encodeNickName = MimeUtility.encodeText(nickName);
            msg.setFrom(new InternetAddress(encodeNickName + " <" + from + ">"));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return this;
    }
    public MailUtils to(String to) throws SendMailException {
        try {
            return addRecipient(to, Message.RecipientType.TO);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }
    private MailUtils addRecipients(String[] recipients, Message.RecipientType type) throws MessagingException {
        String result = Arrays.asList(recipients).toString().replace("(^\\[|\\]$)", "").replace(", ", ",");
        msg.setRecipients(type, InternetAddress.parse(result));
        return this;
    }

    private MailUtils addRecipient(String recipient, Message.RecipientType type) throws MessagingException {
        msg.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")));
        return this;
    }

    public MailUtils text(String text) {
        this.text = text;
        return this;
    }

    public MailUtils html(String html) {
        this.html = html;
        return this;
    }

    public MailUtils attach(File file) throws SendMailException {
        attachments.add(createAttachment(file, null));
        return this;
    }

    public MailUtils attach(File file, String fileName) throws SendMailException {
        attachments.add(createAttachment(file, fileName));
        return this;
    }

    public MailUtils attachURL(URL url, String fileName) throws SendMailException {
        attachments.add(createURLAttachment(url, fileName));
        return this;
    }

    private MimeBodyPart createAttachment(File file, String fileName) throws SendMailException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(file);
        try {
            attachmentPart.setDataHandler(new DataHandler(fds));
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fds.getName()) : MimeUtility.encodeText(fileName));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return attachmentPart;
    }

    private MimeBodyPart createURLAttachment(URL url, String fileName) throws SendMailException {
        MimeBodyPart attachmentPart = new MimeBodyPart();

        DataHandler dataHandler = new DataHandler(url);
        try {
            attachmentPart.setDataHandler(dataHandler);
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fileName) : MimeUtility.encodeText(fileName));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return attachmentPart;
    }

    public void send() throws SendMailException {
        if (text == null && html == null) {
            throw new IllegalArgumentException("At least one context has to be provided: Text or Html");
        }

        MimeMultipart cover;
        boolean usingAlternative = false;
        boolean hasAttachments = attachments.size() > 0;

        try {
            if (text != null && html == null) {
                // TEXT ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(textPart());
            } else if (text == null && html != null) {
                // HTML ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(htmlPart());
            } else {
                // HTML + TEXT
                cover = new MimeMultipart("alternative");
                cover.addBodyPart(textPart());
                cover.addBodyPart(htmlPart());
                usingAlternative = true;
            }

            MimeMultipart content = cover;
            if (usingAlternative && hasAttachments) {
                content = new MimeMultipart("mixed");
                content.addBodyPart(toBodyPart(cover));
            }

            for (MimeBodyPart attachment : attachments) {
                content.addBodyPart(attachment);
            }

            msg.setContent(content);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            throw new SendMailException(e);
        }
    }

    private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(cover);
        return wrap;
    }

    private MimeBodyPart textPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(text);
        return bodyPart;
    }

    private MimeBodyPart htmlPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(html, "text/html; charset=utf-8");
        return bodyPart;
    }

}
