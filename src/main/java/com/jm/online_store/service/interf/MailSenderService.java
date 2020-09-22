package com.jm.online_store.service.interf;

import javax.mail.MessagingException;

public interface MailSenderService {

    void send(String emailTo, String subject, String message, String emailType);

    void sendHtmlMessage(String emailTo, String subject, String htmlBody, String emailType)  throws MessagingException;
}
