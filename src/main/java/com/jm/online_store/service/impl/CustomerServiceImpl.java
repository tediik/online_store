package com.jm.online_store.service.impl;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {
    private final UserService userService;
    private final ConfirmationTokenRepository confirmTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderServiceImpl mailSenderService;
    private final CustomerRepository customerRepository;
    @Value("${spring.server.url}")
    private String urlActivate;

    @Override
    @Transactional
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            return false;
        }
        if (!ValidationUtils.isValidPassword(newPassword)) {
            return false;
        }
        customer.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    @Override
    @Transactional
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void changeCustomerMail(Customer customer, String newMail) {
        String address = customer.getAuthorities().toString().contains("ROLE_CUSTOMER") ? "/customer" : "/authority";

        ConfirmationToken confirmationToken = new ConfirmationToken(customer.getId(), customer.getEmail());
        confirmTokenRepository.save(confirmationToken);

        String message = String.format(
                "Здравствуйте, %s! \n" +
                        "Вы запросили изменение адреса электронной почты. Подтвердите, пожалуйста, по ссылке: " +
                        urlActivate + address + "/activatenewmail/%s",
                customer.getEmail(),
                confirmationToken.getConfirmationToken()
        );
        mailSenderService.send(customer.getEmail(), "Activation code", message, "email address validation");
        customer.setEmail(newMail);
    }

    @Override
    @Transactional
    public void addCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(UserNotFoundException::new);
        customer.setDayOfWeekForStockSend(null);
        updateCustomer(customer);
    }

    @Override
    @Transactional
    public List<Customer> findByDayOfWeekForStockSend(byte dayNumber) {
        List<Customer> customers = customerRepository.findByDayOfWeekForStockSend(Customer.DayOfWeekForStockSend.values()[dayNumber - 1]);
        if (customers.isEmpty()) {
            throw new UserNotFoundException();
        }
        return customers;
    }

    @Override
    @Transactional
    public boolean checkCustomerStatus(String email, String password) {
        Set<Role> roles = userService.findByEmail(email).get().getRoles();
        if (roles.size() > 1) {
            return false;
        }
        for (Role role : roles) {
            if (role.getName().equals("ROLE_CUSTOMER")) {
                Customer customer = customerRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
                if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
                    if (customer.getStatus() != null) {
                        if (!customer.getStatus().isAfter(LocalDateTime.now().minusDays(30))) {
                            deleteByID(customer.getId());
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void changeUserStatusToLocked(Long id) {
        Customer customerStatusChange = getCurrentLoggedInUser();
        customerStatusChange.setStatus(LocalDateTime.now());
        updateCustomer(customerStatusChange);
    }

    @Override
    @Transactional
    public Customer updateCustomerProfile(Customer customer) {
        Customer updateCustomer = customerRepository.findById(customer.getId()).orElseThrow(UserNotFoundException::new);
        updateCustomer.setFirstName(customer.getFirstName());
        updateCustomer.setLastName(customer.getLastName());
        updateCustomer.setBirthdayDate(customer.getBirthdayDate());
        updateCustomer.setUserGender(customer.getUserGender());
        updateCustomer.setDayOfWeekForStockSend(customer.getDayOfWeekForStockSend());
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return customerRepository.findByEmail(auth.getName()).orElseThrow(UserNotFoundException::new);
    }

    @Override
    @Transactional
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void restoreCustomer(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        customer.setStatus(null);
        updateCustomer(customer);
    }

    @Override
    @Transactional
    public boolean isExist(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            return false;
        }
        if (customer.get().getStatus() == null) {
            return true;
        }
        if (!customer.get().getStatus().isAfter(LocalDateTime.now().minusDays(30))) {
            deleteByID(customer.get().getId());
            return false;
        }
        return true;
    }


    @Override
    @Transactional
    public void deleteByID(Long id) {
        customerRepository.deleteById(id);
    }

}
