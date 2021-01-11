package com.jm.online_store.service.interf;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

public interface ReceiveEmailSubscribeConfirmation extends Runnable {
    public void run();
}
