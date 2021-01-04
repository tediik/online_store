package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ConfirmReceiveEmail;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.ReceiveEmailSubscribeConfirmation;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import java.util.Optional;
import java.util.Properties;

/**
 * Метод проверяет почту на предмет подтверждения юзерами подписки на получение писем об изменении цены
 * (в ответ на запрошенное подтверждение).
 * Сверяет адресаты полученных непрочитанных писем с наличием их в БД и статусом ConfirmReceiveEmail.REQUESTED, при
 * совпадении - меняет статус на ConfirmReceiveEmail.CONFIRMED, после чего пользователь будет получать рассылку.
 * (Подтверждение запрашивается при нажатии "Подписаться на изменение цены" каждый раз, пока не будет получено согласие)
 */
@Service
@RequiredArgsConstructor
public class ReceiveEmailSubscribeConfirmationImpl implements ReceiveEmailSubscribeConfirmation {

    @Value("${spring.mail.username}")
    private String user;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host-imap}")
    private String host;

    private final UserService userService;

    @Scheduled(cron = "${check_new_emails_subscribe_confirmations.cron}")
    @Override
    public void run() {
        String email;
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        try {
            Store store = Session.getInstance(properties).getStore();
            store.connect(host, user, password);
            final Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message[] messages = inbox.search(unseenFlagTerm);
            for (Message m : messages) {
                Address[] froms = m.getFrom();
                m.setFlag(Flags.Flag.SEEN, true);
                email = ((InternetAddress) froms[0]).getAddress().trim();
                if (userService.findByEmail(email).isPresent() && userService.findByEmail(email).get().
                        getConfirmReceiveEmail().toString().equals("REQUESTED")) {
                    User user = userService.findByEmail(email).get();
                    user.setConfirmReceiveEmail(ConfirmReceiveEmail.CONFIRMED);
                    userService.updateUser(user);
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
