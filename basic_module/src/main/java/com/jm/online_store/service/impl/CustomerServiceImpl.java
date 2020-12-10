package com.jm.online_store.service.impl;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    /**
     * Все клиенты.
     *
     * @return List<Customer>.
     */
    @Override
    @Transactional
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Проверка на валидность пароля и изменения пароля.
     *
     * @param id          клиента.
     * @param oldPassword - старый пароль.
     * @param newPassword - новый пароль.
     * @return false если пароль не валиден, true если пароль был изменен.
     */
    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            return false;
        }
        if (!ValidationUtils.isValidPassword(newPassword)) {
            log.debug("Новый пароль не прошел валидацию");
            return false;
        }
        userService.changeUsersPass(customer, newPassword);
        return true;
    }

    /**
     * Поиск клиента по id.
     *
     * @param id клиента.
     * @return Customer.
     */
    @Override
    @Transactional
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }


    /**
     * Метод добавления клиента.
     *
     * @param customer - клиент для добавления.
     */
    @Override
    @Transactional
    public void addCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    /**
     * Метод отписки от рассылки.
     *
     * @param id клиента.
     */
    @Override
    @Transactional
    public void cancelSubscription(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(UserNotFoundException::new);
        customer.setDayOfWeekForStockSend(null);
        updateCustomer(customer);
    }

    /**
     * метод получения клиентов, подписанных на рассылку, по дню недели.
     *
     * @param dayNumber день недели
     * @return List<Customer>
     */
    @Override
    @Transactional
    public List<Customer> findByDayOfWeekForStockSend(byte dayNumber) {
        List<Customer> customers = customerRepository.findByDayOfWeekForStockSend(DayOfWeekForStockSend.values()[dayNumber - 1]);
        if (customers.isEmpty()) {
            throw new UserNotFoundException();
        }
        return customers;
    }

    /**
     * У нас клиент изначально не удаляется. При нажатии на кнопку "удалить профиль"
     * происходит запись времени, когда кнопка была нажата и подтвеждена.
     * Мы ему даем 30 дней на восстановление.
     * Время удаления записывается в поле "anchorForDelete" у Customer,
     * затем мы меняем его AccountNonBlockedStatus на false ,
     * это нужно для обработки в spring security.
     * <p>
     * Метод, который изменяет статус клиента при нажатии на кнопку "удалить профиль"
     *
     * @param id клиента.
     */
    @Override
    @Transactional
    public void changeCustomerStatusToLocked(Long id) {
        Customer customerStatusChange = getCurrentLoggedInUser();
        customerStatusChange.setAccountNonBlockedStatus(false);
        customerStatusChange.setAnchorForDelete(LocalDateTime.now());
        updateCustomer(customerStatusChange);
        log.info("профиль покупателя с почтой " + customerStatusChange.getEmail() + "заблокирован");
    }

    /**
     * метод обновления дня для рассылки.
     *
     * @param customer              клиент.
     * @param dayOfWeekForStockSend день недели.
     */
    @Override
    @Transactional
    public void updateCustomerDayOfWeekForStockSend(Customer customer, String dayOfWeekForStockSend) {
        if (dayOfWeekForStockSend.isEmpty()) {
            customer.setDayOfWeekForStockSend(null);
        } else {
            customer.setDayOfWeekForStockSend(DayOfWeekForStockSend.valueOf(dayOfWeekForStockSend));
        }
        updateCustomer(customer);
    }

    /**
     * Метод получения текущего залогининового клиента.
     *
     * @return Customer.
     */
    @Override
    @Transactional
    public Customer getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return customerRepository.findByEmail(auth.getName()).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Обновление клиента.
     *
     * @param customer клиент.
     */
    @Override
    @Transactional
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    /**
     * Метод для восстановления клиента
     *
     * @param email - емейл для восстановления
     */
    @Override
    @Transactional
    public void restoreCustomer(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        customer.setAccountNonBlockedStatus(true);
        customer.setAnchorForDelete(null);
        updateCustomer(customer);
    }

    /**
     * Метод проверки на существование клиента по email.
     *
     * @param email клиента.
     * @return false если такого клиента нет в БД или его статус больше 30 дней.
     * true если статус null или такой пользователь есть в БД.
     */
    @Override
    @Transactional
    public boolean isExist(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            return false;
        } else if (customer.get().isAccountNonLocked() || customer.get().getAnchorForDelete() == null) {
            return true;
        }
        return true;
    }

    /**
     * Удаление клиента по id.
     *
     * @param id клиента.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Метод который будет ходить по базе раз в день,
     * и удалять кастомеров которые удалили свой профиль и у которых срок для восстановления истек.
     * Время и таска создается в Datainitializer'e
     */
    @Override
    @Transactional
    public void deleteAllBlockedWithThirtyDaysPassed() {
        LocalDateTime timeAfterThirtyDaysFromNow = LocalDateTime.now().minusDays(30);
        List<Customer> listForDelete = customerRepository.findAllByAnchorForDeleteThirtyDays(timeAfterThirtyDaysFromNow);
        if (listForDelete.isEmpty()) {
            log.info("Список профилей для удаления пуст, метод для удаления не будет вызвыан");
        } else {
            log.info("В списке на удаление есть профили , вызываем метод для удаления");
            customerRepository.deleteInBatch(listForDelete);
        }
    }
}
