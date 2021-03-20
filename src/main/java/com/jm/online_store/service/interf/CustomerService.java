package com.jm.online_store.service.interf;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    void deleteAllBlockedWithThirtyDaysPassed();

    List<Customer> findAll();

    boolean changePassword(Long id, String oldPassword, String newPassword);

    Optional<Customer> findById(Long id);

    void addCustomer(Customer customer);

    void cancelSubscription(Long id);

    List<Customer> findByDayOfWeekForStockSend(String dayOfWeek);

    List<Customer> findSubscriberByEmail(String email);

    void changeCustomerStatusToLocked(Long id);

    void changeCustomerStatusToReadOnly(Long id);

    void changeCustomerProfileToDeletedProfileByID(long id);

    DayOfWeekForStockSend getCustomerDayOfWeekForStockSend(Customer customer);

    void updateCustomerDayOfWeekForStockSend(Customer customer, String dayOfWeekForStockSend);

    Customer getCurrentLoggedInUser();

    void updateCustomer(Customer customer);

    Customer restoreCustomer(String email);

    boolean isExist(String email);

    void deleteByID(Long id);

    Customer changeMail(String newMail);

    Customer getById(Long id);
}
