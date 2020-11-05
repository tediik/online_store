package com.jm.online_store.service.interf;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.User;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();

    boolean changePassword(Long id, String oldPassword, String newPassword);

    Optional<Customer> findById(Long id);

    void changeCustomerMail(Customer customer, String newMail);

    void addCustomer(Customer customer);

    void cancelSubscription(Long id);

    List<Customer> findByDayOfWeekForStockSend(byte dayNumber);

    boolean checkCustomerStatus(String email, String password);

    void changeUserStatusToLocked(Long id);

    Customer updateCustomerProfile(Customer customer);

    Customer getCurrentLoggedInUser();

    void updateCustomer(Customer customer);

    void restoreCustomer(String email);

    boolean isExist(String email);

    void deleteByID(Long id);
}
