package com.jm.online_store.service.impl;

import com.jm.online_store.model.PriceChangeNotifications;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.PriceChangeNotificationRepository;
import com.jm.online_store.service.interf.PriceChangeNotificationsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class PriceChangeNotificationsServiceImpl implements PriceChangeNotificationsService {

    private final PriceChangeNotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    public void addPriceChangesNotification(Product product, double oldPrice, double newPrice) {
        Set<String> emails = product.getPriceChangeSubscribers();
        for (String email : emails) {
            notificationRepository.save(new PriceChangeNotifications(oldPrice, newPrice, product.getId(),
                    userService.findByEmail(email).get().getId(), LocalDateTime.now()));
        }
    }

    @Override
    public void deletePriceChangesNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public List<PriceChangeNotifications> getCustomerPriceChangeNotifications(Long id) {
        return notificationRepository.findByCustomerId(id);
    }

}
