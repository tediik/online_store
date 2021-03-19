package com.jm.online_store.service.interf;

import com.jm.online_store.model.PriceChangeNotifications;
import com.jm.online_store.model.Product;

import java.util.List;

public interface PriceChangeNotificationsService {

    void addPriceChangesNotification(Product product, double oldPrice, double newPrice);

    void deletePriceChangesNotification(Long id);

    List<PriceChangeNotifications> getCustomerPriceChangeNotifications(Long id);

}
