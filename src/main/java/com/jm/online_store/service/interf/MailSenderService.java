package com.jm.online_store.service.interf;

public interface MailSenderService {

    void send(String emailTo, String subject, String message);
}
