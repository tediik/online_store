package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.MailSenderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Data
public class MailSenderServiceImpl implements MailSenderService {

    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void send(String emailTo, String subject, String message, String emailType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        log.info("{} email was sent from {} to {} with subject {} and message {}",emailType, username, emailTo, subject, mailMessage);
        mailSender.send(mailMessage);
    }
}
