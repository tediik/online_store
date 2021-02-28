package com.jm.online_store.service.impl;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.CustomerServiceException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final AddressService addressService;

    /**
     * Метод сервиса для добавления нового адреса пользователю
     * @param customerReq переданный пользователь
     * @param address новый адрес для пользователя
     * @throws UserNotFoundException вылетает, если пользователь не найден в БД
     */
    @Override
    @Transactional
    public boolean addNewAddressForCustomer(Customer customerReq, Address address) {
        Customer customer = findById(customerReq.getId()).orElseThrow(() ->
                new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
        Optional<Address> addressFromDB = addressService.findSameAddress(address);
        if (addressFromDB.isPresent() && !customer.getUserAddresses().contains(addressFromDB.get())) {
            Address addressToAdd = addressFromDB.get();
            if (customer.getUserAddresses() != null) {
                customer.getUserAddresses().add(addressToAdd);
            } else {
                customer.setUserAddresses(Collections.singleton(address));
            }
            updateCustomer(customer);
            return true;
        }
        if (addressFromDB.isEmpty()) {
            Address addressToAdd = addressService.addAddress(address);
            if (customer.getUserAddresses() != null) {
                customer.getUserAddresses().add(addressToAdd);
            } else {
                customer.setUserAddresses(Collections.singleton(address));
            }
            updateCustomer(customer);
            return true;
        }
        return false;

    }

    /**
     * Все клиенты.
     * @return List<Customer>.
     */
    @Override
    @Transactional
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Проверка на валидность пароля и изменения пароля.
     * @param id клиента.
     * @param oldPassword - старый пароль.
     * @param newPassword - новый пароль.
     * @return false если пароль не валиден, true если пароль был изменен.
     */
    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Customer customer = findById(id).orElseThrow(CustomerServiceException::new);
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

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Поиск клиента по id.
     * @param id клиента.
     * @return Customer.
     */
    @Override
    @Transactional
    public Customer getById(Long id) {
        return customerRepository.findById(id).orElseThrow(()
                -> new CustomerServiceException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
    }

    /**
     * Поиск подписчика по email "на лету".
     * @param email клиента.
     * @return Customer.
     */
    @Override
    @Transactional
    public List<Customer> findSubscriberByEmail(String email) {
        return customerRepository.findSubscriberByEmail(email);
    }

    /**
     * Добавление клиента.
     * @param customer - клиент для добавления.
     */
    @Override
    @Transactional
    public Customer addCustomer(Customer customer) {
       return customerRepository.save(customer);
    }

    /**
     * Отписка от рассылки.
     * @param id клиента.
     */
    @Override
    @Transactional
    public void cancelSubscription(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() +
                        String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));
        customer.setDayOfWeekForStockSend(null);
    }

    /**
     * Получение клиентов, подписанных на рассылку, по дню недели.
     * @param dayOfWeek день недели
     * @return List<Customer>
     */
    @Override
    @Transactional
    public List<Customer> findByDayOfWeekForStockSend(String dayOfWeek) {
        return customerRepository.findByDayOfWeekForStockSend(DayOfWeekForStockSend.valueOf(dayOfWeek));
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
        Customer customerStatusChange = customerRepository.findById(id).orElseThrow(UserNotFoundException::new);
        customerStatusChange.setAccountNonBlockedStatus(false);
        customerStatusChange.setAnchorForDelete(LocalDateTime.now());
        updateCustomer(customerStatusChange);
        log.info("профиль покупателя с почтой " + customerStatusChange.getEmail() + "заблокирован");
    }

    /**
     * Обновление дня для рассылки.
     * @param customer клиент.
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
     * Получение текущего залогиненного клиента.
     * @return Customer.
     */
    @Override
    @Transactional
    public Customer getCurrentLoggedInCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return customerRepository.findByEmail(auth.getName()).orElseThrow(()
                -> new UserNotFoundException(ExceptionEnums.CUSTOMERS.getText() + ExceptionConstants.NOT_FOUND));
    }

    /**
     * Обновление клиента.
     * @param customer клиент.
     */
    @Override
    @Transactional
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    /**
     * Восстановление клиента.
     * @param email - емейл для восстановления
     */
    @Override
    @Transactional
    public void restoreCustomer(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException(ExceptionEnums.CUSTOMERS.getText() + ExceptionConstants.NOT_FOUND));
        customer.setAccountNonBlockedStatus(true);
        customer.setAnchorForDelete(null);
        updateCustomer(customer);
    }

    /**
     * Проверка на существование клиента по email.
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
     * @param id клиента.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Меняет по идентификатору пользователя в комментариях и отзывах
     * на "Deleted пользователя(DeletedCustomer)".
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void changeCustomerProfileToDeletedProfileByID(long id) {
        User customer = userService.findUserById(id);
        List<Comment> customerComments = commentService.findAllByCustomer(customer);
        List<Review> customerReview = reviewService.findAllByCustomer(customer);
        User deletedUser = userService.findUserByEmail("deleted@mail.ru");
        for (Comment comment : customerComments) comment.setCustomer(deletedUser);
        for (Review review : customerReview) review.setCustomer(deletedUser);
    }

    /**
     * Метод, который будет ходить по базе раз в день,
     * и удалять кастомеров которые удалили свой профиль и у которых срок для восстановления истек.
     * Время и таска создается в Datainitializer'e
     * настройка времени находится в  application.yml
     */
    @Override
    @Scheduled(cron = "${delete_expired_customer_period.cron}")
    @Transactional
    public void deleteAllBlockedWithThirtyDaysPassed() {
        log.info("Метод удаляющий пользователей с удаленным профилем , у которых 30 дней на восстановление прошло - запущен");
        LocalDateTime timeAfterThirtyDaysFromNow = LocalDateTime.now().minusDays(30);
        List<Customer> listForDelete = customerRepository.findAllByAnchorForDeleteThirtyDays(timeAfterThirtyDaysFromNow);
        if (listForDelete.isEmpty()) {
            log.debug("Список профилей для удаления пуст, метод для удаления не будет вызван");
        } else {
            log.info("Удалено {} профилей", listForDelete.size());
            listForDelete.forEach(customer -> changeCustomerProfileToDeletedProfileByID(customer.getId()));
            customerRepository.deleteAll(listForDelete);
        }
    }

    @Override
    @Transactional
    public Customer changeMail(String newMail) {
        Customer customer = getCurrentLoggedInCustomer();
        if (customer == null) {
            throw new AuthenticationCredentialsNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_AUTHENTICATED);
        }
        if (isExist(newMail)) {
            throw new EmailAlreadyExistsException(ExceptionEnums.CUSTOMER.getText() + String.format(ExceptionConstants.ALREADY_EXISTS, newMail));
        }
        if (ValidationUtils.isNotValidEmail(newMail)) {
            throw new InvalidEmailException(ExceptionEnums.CUSTOMERS.getText() + String.format(ExceptionConstants.NOT_VALID, newMail));
        }
        userService.changeUsersMail(customer, newMail);
        return customer;
    }

    @Override
    @Transactional
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(()
                -> new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
    }

    @Transactional
    @Override
    public User getCurrentLoggedInCustomer(String sessionID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // AnonymousAuthenticationToken happens when anonymous authentication is enabled
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        if (auth instanceof AnonymousAuthenticationToken) {
            if (userService.findByEmail(sessionID).isEmpty()) {
                customerRepository.save(new Customer(sessionID, null));
            }
            return findCustomerByEmail(sessionID);
        }
        return userService.findByEmail(auth.getName()).orElseThrow();
    }
}
