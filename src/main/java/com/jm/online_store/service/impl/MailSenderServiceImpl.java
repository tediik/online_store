package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.MailSenderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@Data
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void send(String emailTo, String subject, String message, String emailType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        log.info("{} email was sent from {} to {} with subject {} and message {}",emailType, username, emailTo, subject, mailMessage);
        try {
            mailSender.send(mailMessage);
        } catch (MailSendException ex) {
            log.debug("Warning! Message rejected under suspicion of SPAM!");
        }
    }

    /**
     * Method which prepares and sends email with HTML body format
     * @param emailTo - {@link String} String with email address of recipient
     * @param subject - {@link String} String with subject of email message
     * @param htmlBody - {@link} String with body in HTML format
     * @param emailType - {@link} String email type, actually for logging information about sent email it can be for example:
     *                          - stock distribution email
     *                          - news distribution email
     *                          - e t.c.
     * @throws MessagingException - can throw MessagingException
     */
    @Override
    public void sendHtmlMessage(String emailTo, String subject, String htmlBody, String emailType) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessage.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        log.info("{} Html-email was sent from {} to {} with subject {} and message {}", emailType, username, emailTo, subject, mimeMessage);
        mailSender.send(mimeMessage);
    }
}
