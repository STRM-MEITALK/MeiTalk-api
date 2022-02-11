package com.meitalk.api.service;

import com.meitalk.api.config.EncryptAes128;
import com.meitalk.api.model.global.SendAuthMailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Value("${aws.ses.username}")
    private String sesUsername;
    @Value("${aws.ses.password}")
    private String sesPassword;
    @Value("${aws.ses.host}")
    private String sesHost;
    @Value("${aws.smtp.port}")
    private String smtpPort;
    @Value("${base.uri}")
    private String baseUri;
    @Autowired
    EncryptAes128 encryptAes128;

    public Session createProps() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return Session.getDefaultInstance(props);
    }

    public String getHtml(String template, Context context) {
        return templateEngine.process(template, context);
    }

    @Async
    public void sendAuthEmail(SendAuthMailDto req) throws IOException, MessagingException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Context context = new Context();
        context.setVariable("userKey", req.getUserKey());
        context.setVariable("userMail", req.getUserMail());
        context.setVariable("authUri", baseUri + "/api/global/auth-mail/success");
        context.setVariable("baseUri", baseUri);
        context.setVariable("encodeMail", encryptAes128.AES128Encoder(req.getUserMail()));
        Session session = createProps();
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("noreply@meitalktv.com", "MEI TALK"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(req.getUserMail()));
        msg.setSubject("Verify Email");
        msg.setContent(this.getHtml("auth_mail", context), "text/html");
        Transport transport = session.getTransport();
        try {
            transport.connect(sesHost, sesUsername, sesPassword);
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            log.error("send auth email => ses error");
            e.printStackTrace();
        } finally {
            log.info("auth mail send success!");
            transport.close();
        }
    }
}
