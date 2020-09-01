package com.jm.online_store.service.impl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class MailSenderServiceImplTest {

    @Autowired
    private MailSenderServiceImpl senderService;
    private JavaMailSenderImpl javaMailSender;
    private GreenMail greenMail;

    @BeforeEach
    public void before() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPort(3025);
        javaMailSender.setHost("localhost");
    }

    @Test
    void emailTest() {
        String from = "test@sender.com";
        String to = "test@receiver.com";
        String subject = "test subject";
        String text = "test message";
        senderService.setMailSender(javaMailSender);
        senderService.setUsername(from);
        senderService.send(to, subject, text, "");
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        try {
            assertEquals(subject, messages[0].getSubject());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        assertEquals(text, GreenMailUtil.getBody(messages[0]));
    }

    @After
    public void after() {
        greenMail.stop();
    }
}