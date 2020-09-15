package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.MailSenderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
        log.info("{} email was sent from {} to {} with subject {} and message {}", emailType, username, emailTo, subject, mailMessage);
        mailSender.send(mailMessage);
    }

    @Override
    public void sendHtmlMessage(String emailTo, String subject, String htmlBody, String emailType) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessage.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        log.info("{} email was sent from {} to {} with subject {} and message {}", emailType, username, emailTo, subject, mimeMessage);
        mailSender.send(mimeMessage);
    }
}
