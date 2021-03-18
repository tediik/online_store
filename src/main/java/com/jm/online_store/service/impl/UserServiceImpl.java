package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ConfirmReceiveEmail;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.UserServiceException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.TemplatesMailingSettingsService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String uploadDirectory = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ConfirmationTokenRepository confirmTokenRepository;
    private final MailSenderServiceImpl mailSenderService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;
    private final CommonSettingsService commonSettingsService;
    private final FavouritesGroupService favouritesGroupService;
    private final TemplatesMailingSettingsService templatesMailingSettingsService;

    @Value("${spring.server.url}")
    private String urlActivate;

    @Value("${production-url}")
    private String productionUrl;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Получение списка пользователей, отсортированных в соответствии с выбранной ролью
     * @param roleString роль, по которой фильтруется список пользователей
     * @return List<User> отфильтрованный список пользователей
     */
    @Override
    public List<User> findByRole(String roleString) {

        List<User> filteredUsers = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            for (Role roles : user.getRoles()) {
                if (roles.getName().equals(roleString)) {
                    filteredUsers.add(user);
                }
            }
        }
        return filteredUsers;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByFirstName(String FirstName) {
        return userRepository.findByFirstName(FirstName);
    }

    /**
     * Проверяет существование пользователя в БД.
     * @param email - поле по которому проверяем пользователя
     * @return false -  Если такой пользователь не был найден.
     * Если же все-таки он был найден, и статус удаления у него есть, и 30 дней истекли.
     * true -   Если такой пользователь существует и у него отсутствует статус удаления.
     * Если такой пользователь существует и у него есть статус на удаление, но его 30 дней не истекли.
     */
    @Override
    @Transactional
    public boolean isExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    /**
     * Добавление нового пользователя.
     * Проверяется пароль на валидность, отсутствие пользователя с данным email (уникальное значение).
     * @param user полученный объект User
     */
    @Override
    @Transactional
    public void addUser(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getEmail() != null) {
            if (ValidationUtils.isNotValidEmail(user.getEmail())) {
                throw new InvalidEmailException();
            }
            if (isExist(user.getEmail())) {
                throw new EmailAlreadyExistsException();
            }
            if (!CollectionUtils.isEmpty(user.getRoles())) {
                user.setRoles(persistRoles(user.getRoles()));
            }
            if (user.getProfilePicture().isEmpty()) {
                user.setProfilePicture(StringUtils.cleanPath("def.jpg"));
            }
        }
        userRepository.save(user);
    }

    /**
     * Обновление пользователя.
     * @param user пользователь, полученный из контроллера.
     * @return добавленного/обновленного {@link User}
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Метод обновляет профиль пользователя в личном кабинете.
     * @param user сущность, полученный из контроллера.
     * @return измененного пользователя.
     * @throws UserNotFoundException если пользователь не найден в БД.
     */
    @Override
    @Transactional
    public User updateUserProfile(User user) {
        User updateUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        updateUser.setFirstName(user.getFirstName());
        updateUser.setLastName(user.getLastName());
        updateUser.setBirthdayDate(user.getBirthdayDate());
        updateUser.setUserGender(user.getUserGender());
        return userRepository.save(updateUser);
    }

    /**
     * Обновляет данные пользователя в личном кабинете, используя сущность UserDto.
     * @param userDto сущность, полученная из контроллера.
     * @return измененный пользователь.
     * @throws UserNotFoundException если пользователя не существует в БД.
     */
    @Override
    @Transactional
    public User updateUserDtoProfile(UserDto userDto) {
        User updateUser = userRepository.findById(userDto.getId()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        updateUser.setFirstName(userDto.getFirstName());
        updateUser.setLastName(userDto.getLastName());
        updateUser.setBirthdayDate(userDto.getBirthdayDate());
        updateUser.setUserGender(userDto.getUserGender());
        return userRepository.save(updateUser);

    }

    /**
     * Обновляет данные польователя в Rest-api UserRestController.
     * @param user сущность, полученный из контроллера.
     * @throws UserNotFoundException       если пользователя не существует в БД.
     * @throws EmailAlreadyExistsException если такая почта уже существует.
     * @throws InvalidEmailException       если почта не соответствует формату.
     */
    @Override
    @Transactional
    public void updateUserAdminPanel(@NotNull User user) {
        User editUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        if (!editUser.getEmail().equals(user.getEmail())) {
            if (isExist(user.getEmail())) {
                throw new EmailAlreadyExistsException();
            } else if (ValidationUtils.isNotValidEmail(user.getEmail())) {
                throw new InvalidEmailException();
            }
            editUser.setEmail(user.getEmail());
        }
        editUser.setRoles(persistRoles(user.getRoles()));
        log.debug("editUser: {}", editUser.getEmail());
        userRepository.save(editUser);
    }

    /**
     * Удаляет пользователя по идентификатору.
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Регистрация нового пользователя.
     * Метод генерирует токен на основе email и пароля, затем отправляет письмо
     * с ссылкой для подтверждения регистрации на указанный email.
     * @param userForm User полученный из данных формы.
     */
    @Override
    @Transactional
    public User regNewAccount(User userForm) {
        if (userForm.getEmail() != null) {
            ConfirmationToken confirmationToken = new ConfirmationToken(userForm.getEmail(), userForm.getPassword());
            confirmTokenRepository.save(confirmationToken);
            String messageBody;
            if (templatesMailingSettingsService.getSettingByName("reg_new_account").getTextValue() != null) {
                String templateBody = templatesMailingSettingsService.getSettingByName("reg_new_account").getTextValue();
                messageBody = templateBody.replace("@@userEmail@@", userForm.getEmail())
                        .replace("@@confirmationToken@@", confirmationToken.getConfirmationToken())
                        .replace("@@url@@", urlActivate);
                mailSenderService.send(userForm.getEmail(), "Activation code", messageBody, "Confirmation");
            } else {
                log.debug("Шаблон рассылки при регистрации нового пользователя в базе пустой ");
            }
        } else {
            log.debug("Email пустой");
        }
    return userForm;
    }

    /**
     * Метод формирует токен и отправляет ссылку подтверждение на email указанный анонимом.
     * @param email указанный анонимным пользователем при покупке
     */
    @Override
    @Transactional
    public void regNewAccount(String email) {
        ConfirmationToken confirmationToken = new ConfirmationToken(email, generatePassayPassword());
        confirmTokenRepository.save(confirmationToken);
        String messageBody;
        if (templatesMailingSettingsService.getSettingByName("reg_new_account").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService.getSettingByName("reg_new_account").getTextValue();
            messageBody = templateBody.replace("@@userEmail@@", email)
                    .replace("@@confirmationToken@@", confirmationToken.getConfirmationToken())
                    .replace("@@url@@", urlActivate);
            mailSenderService.send(email, "Activation code", messageBody, "Confirmation");
        } else {
            log.debug("Шаблон рассылки при регистрации нового пользователя в базе пустой ");
        }
    }

    /**
     * Method generates confirmation token based on users ID and Email adress
     * Sends generated token to new users email
     * @param user, newMail
     */
    @Override
    @Transactional
    public void changeUsersMail(User user, String newMail) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), newMail);
        confirmTokenRepository.save(confirmationToken);
        String messageBody;
        if (templatesMailingSettingsService.getSettingByName("change_users_mail").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService.getSettingByName("change_users_mail").getTextValue();
            String userName;
            if (user.getFirstName() != null) {
                userName =  user.getFirstName();
            } else {
                userName = "Подписчик";
            }
            messageBody = templateBody.replace("@@user@@", userName)
                    .replace("@@confirmationToken@@", confirmationToken.getConfirmationToken())
                    .replace("@@url@@", urlActivate);
            mailSenderService.send(newMail, "Activation code", messageBody, "email address validation");
        } else {
            log.debug("Шаблон рассылки при изменении eMail в базе пустой ");
        }
    }

    /**
     * Устанавливет переданному пользователю новый пароль.
     * Генерирует токен на основе id и email, затем отправляет для подтверждения на email пользователя.
     * @param user пользователь
     * @param newPassword новый пароль
     */
    @Override
    @Transactional
    public void changeUsersPass(User user, String newPassword) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), user.getEmail());
        confirmTokenRepository.save(confirmationToken);
        String messageBody;
        if (templatesMailingSettingsService.getSettingByName("change_users_pass").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService
                    .getSettingByName("change_users_pass")
                    .getTextValue();
            if (user.getFirstName() != null) {
                messageBody = templateBody.replace("@@user@@", user.getFirstName());
            } else {
                messageBody = templateBody.replace("@@user@@", "Подписчик");
            }
            mailSenderService.send(user.getEmail(), "Пароль успешно изменен", messageBody, "pass change");
        } else {
            log.debug("Шаблон рассылки при изменении пароля в базе пустой ");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("Для пользователя с логином {} установлен новый пароль: {}", user.getEmail(), newPassword);
    }

    /**
     * С помощью email находим пользователя, затем получаем его пароль.
     * @param email - почта пользователя.
     * @return String - пароль пользователя.
     */
    @Override
    @Transactional
    public String getPasswordByMail(String email) {
        return findUserByEmail(email).getPassword();
    }

    /**
     * Генерирует новый пароль и отправляет его пользователю на почту.
     * @param user - Покупатель, запросивший смену пароля.
     */
    @Transactional
    @Override
    public void restorePassword(User user) {
        String newPass = generatePassayPassword();
        String messageBody;
        if (templatesMailingSettingsService.getSettingByName("restore_password").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService
                    .getSettingByName("restore_password")
                    .getTextValue();
            if (user.getFirstName() != null) {
                messageBody = templateBody.replace("@@user@@", user.getFirstName())
                .replace("@@newPassword@@", newPass);
            } else {
                messageBody = templateBody.replace("@@user@@", "Подписчик");
            }
            mailSenderService.send(user.getEmail(), "Сгенерирован временный новый пароль: ", messageBody, "pass change");
        } else {
            log.debug("Шаблон рассылки для генерации нового временного пароля в базе пустой ");
        }
        user.setPassword(passwordEncoder.encode(newPass));
        log.info("Для пользователя с логином: {} сгенерирован новый пароль: {}", user.getEmail(), newPass);
    }

    /**
     * Метод использует библиотеку Passay для генерации рандомного пароля в соответствии с указанными требованиями к паролю
     * @return рандомный сгенерированный пароль
     */
    private String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();

        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, lowerCaseRule,
                upperCaseRule, digitRule);
    }

    /**
     * Генерирует токен для сброса пароля и отправляет на указанную пользователем почту
     * @param user пользователь, запросивший сброс пароля.
     */
    @Override
    @Transactional
    public void sendConfirmationTokenToResetPassword(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), user.getEmail());
        confirmTokenRepository.save(confirmationToken);
        String messageBody;
        if (templatesMailingSettingsService.getSettingByName("send_confirmation_token_to_reset_password").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService.getSettingByName("send_confirmation_token_to_reset_password").getTextValue();
            if (user.getFirstName() != null) {
                messageBody = templateBody.replace("@@user@@", user.getFirstName())
                        .replace("@@confirmationToken@@", confirmationToken.getConfirmationToken())
                        .replace("@@url@@", urlActivate);
            } else {
                messageBody = templateBody.replace("@@user@@", "Подписчик");
            }
            mailSenderService.send(user.getEmail(), "Ссылка-подтверждение для генерации нового пароля", messageBody, "pass change");
        } else {
            log.debug("Шаблон рассылки при регистрации нового пароля в базе пустой ");
        }
        log.info("На почту: {} отправлена ссылка-подтверждение для генерации нового пароля.", user.getEmail());
    }

    /**
     * Метод проверки активации пользователя.
     * @param token модель, построенная на основе пользователя, после подтверждения
     * @param request параметры запроса.
     * @return булево значение "true or false"
     */
    @Override
    @Transactional
    public boolean activateUser(String token, HttpServletRequest request) {
        String storeName = commonSettingsService.getSettingByName("store_name").getTextValue(); //Название магазина из БД
        String messageBody;
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        if (confirmationToken == null) {
            log.debug("ConfirmationToken is null");
            return false;
        }
        Set<Role> userRoles = roleRepository.findByName("ROLE_CUSTOMER")
                .map(Collections::singleton)
                .orElse(Collections.emptySet());

        Customer customer = new Customer();
        customer.setEmail(confirmationToken.getUserEmail());
        customer.setPassword(confirmationToken.getUserPassword());
        customer.setRoles(userRoles);
        addUser(customer);

        FavouritesGroup favouritesGroup = new FavouritesGroup();
        favouritesGroup.setName("Все товары");
        favouritesGroup.setUser(customer);
        favouritesGroupService.save(favouritesGroup);

        if (userRepository.existsByEmail(confirmationToken.getUserEmail())) {
            List<SubBasket> subBasketList = getCurrentLoggedInUser(request.getSession().getId()).getUserBasket();
            userRepository.delete(getCurrentLoggedInUser(request.getSession().getId()));
            customer.setUserBasket(subBasketList);
        }
        if (templatesMailingSettingsService.getSettingByName("activate_user").getTextValue() != null) {
            String templateBody = templatesMailingSettingsService.getSettingByName("activate_user").getTextValue();
            if (customer.getEmail() != null) {
                messageBody = templateBody.replace("@@user@@", customer.getEmail())
                        .replace("@@password@@", confirmationToken.getUserPassword())
                        .replace("@@url@@", String.format("<a href='%s'>" + storeName + "</a>",  productionUrl));
            } else {
                messageBody = templateBody.replace("@@user@@", "Подписчик");
            }
            try {
                mailSenderService.sendHtmlMessage(customer.getEmail(), "Информация о регистрации на сайте " + storeName, messageBody, "info");
            } catch (MessagingException e) {
                log.debug("Message sending error in ActivateUser Method {}", e.getMessage());
            }
        } else {
            log.debug("Шаблон рассылки при активации пользователя в базе пустой ");
        } try {
            request.login(customer.getEmail(), confirmationToken.getUserPassword());
        } catch (ServletException e) {
            log.debug("Servlet exception from ActivateUser Method {}", e.getMessage());
        }
        return true;
    }

    /**
     * Method receives token and request after User confirms mail change via link
     * After that, new email address is saved to users DB table
     */
    @Override
    @Transactional
    public boolean activateNewUsersMail(String token, HttpServletRequest request) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        if (confirmationToken == null) {
            return false;
        }
        User user = userRepository.findById(confirmationToken.getUserId()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        user.setEmail(confirmationToken.getUserEmail());
        userRepository.save(user);
        log.info("Для пользователя с логином: {} установлен новый логин: {}", user.getEmail(), confirmationToken.getUserEmail());
        return true;
    }

    private Set<Role> persistRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    /**
     * updateUserImage method receives authorised user's Id and Multipart Image file
     * saves Image in Uploads/images folder
     * and sets saved image to userProfilePicture
     */
    @Override
    @Transactional
    public String updateUserImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).get();
        String uniqueFilename = StringUtils.cleanPath(UUID.randomUUID() + "." + file.getOriginalFilename());
        if (!file.isEmpty()) {
            Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
            try {
                byte[] bytes = file.getBytes();
                Files.write(fileNameAndPath, bytes);
                //Set user's profile picture
                user.setProfilePicture(uniqueFilename);
                userRepository.save(user);
            } catch (IOException e) {
                log.debug("Failed to store file: {}, because: {}", fileNameAndPath, e.getMessage());
            }
        }
        return File.separator + "uploads" + File.separator + "images" + File.separator + uniqueFilename;
    }

    /**
     * deleteUserImage method receives authorised user's Id
     * deletes current user's profile picture and sets a default avatar
     * default avatar cannot be deleted
     */
    @Override
    @Transactional
    public String deleteUserImage(Long userId) {
        final String defaultAvatar = StringUtils.cleanPath("def.jpg");
        User user = userRepository.findById(userId).get();
        //Get profilePicture name from User and delete this profile picture from Uploads
        Path fileNameAndPath = Paths.get(uploadDirectory, user.getProfilePicture());
        //Check if deleting picture is not a default avatar
        try {
            if (!fileNameAndPath.getFileName().toString().equals(defaultAvatar)) {
                Files.delete(fileNameAndPath);
            }
        } catch (IOException e) {
            log.debug("Failed to delete file: {}, because: {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
        }
        //Set a default avatar as a user profilePicture
        user.setProfilePicture(defaultAvatar);
        return File.separator + "uploads" + File.separator + "images" + File.separator + defaultAvatar;

    }

    /**
     * Метод позволяет добавлять нового пользователя со страницы для админа
     * @param newUser получаем с контроллера
     */
    @Override
    @Transactional
    public void addNewUserFromAdmin(User newUser) {
        if (ValidationUtils.isNotValidEmail(newUser.getEmail())) {
            log.debug("Wrong email");
            throw new UserServiceException(ExceptionEnums.EMAIL.getText() + String.format(ExceptionConstants.NOT_VALID,newUser.getEmail()));
        }
        if (isExist(newUser.getEmail())) {
            log.debug("User with same email already exists");
            throw new UserServiceException(ExceptionEnums.EMAIL.getText() + String.format(ExceptionConstants.ALREADY_EXISTS, newUser.getEmail()));
        }
        if (newUser.getPassword().equals("")) {
            log.debug("Password is empty");
            throw new UserServiceException(ExceptionEnums.PASSWORD.getText() + ExceptionConstants.IS_EMPTY);
        }
        if (newUser.getRoles().size() == 0) {
            log.debug("Roles not selected");
            throw new UserServiceException(ExceptionEnums.ROLES.getText() + ExceptionConstants.IS_EMPTY);
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.getRoles().forEach(role -> role.setId(roleRepository.findByName(role.getName()).get().getId()));
        newUser.setProfilePicture(StringUtils.cleanPath("def.jpg"));
        Set<Role> roles = newUser.getRoles();
        for (Role role : roles) {
            if (!role.getName().equals("ROLE_CUSTOMER") || roles.size() > 1) {
                userRepository.save(newUser);
            } else {
                Customer customer = new Customer(newUser.getEmail(), newUser.getPassword());
                customer.setRoles(newUser.getRoles());
                customer.setProfilePicture(newUser.getProfilePicture());
                customerRepository.save(customer);
            }
            log.debug("User with email: {} was saved successfully", newUser.getEmail());
            return;
        }
    }

    /**
     * Service method to update user from admin page
     * @param user
     * @return User
     */
    @Override
    @Transactional
    public User updateUserFromAdminPage(User user) {
        if (findById(user.getId()).isEmpty()) {
            log.debug("There are no user with id: {}", user.getId());
            throw new UserServiceException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND);
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email");
            throw new UserServiceException(ExceptionEnums.EMAIL.getText() + String.format(ExceptionConstants.NOT_VALID,user.getEmail()));
        }
        if (user.getRoles().size() == 0) {
            log.debug("Roles not selected");
            throw new UserServiceException(ExceptionEnums.ROLES.getText() + ExceptionConstants.NOT_FOUND);
        }
        if (!findById(user.getId()).get().getEmail().equals(user.getEmail())
                && isExist(user.getEmail())) {
            log.debug("User with same email already exists");
            throw new UserServiceException(ExceptionEnums.EMAIL.getText() + String.format(ExceptionConstants.ALREADY_EXISTS, user.getEmail()));
        }
        User editedUser = userRepository.findById(user.getId()).get();
        Set<Role> newRoles = persistRoles(user.getRoles());
        editedUser.setRoles(newRoles);
        editedUser.setEmail(user.getEmail());
        editedUser.setFirstName(user.getFirstName());
        editedUser.setLastName(user.getLastName());
        editedUser.setAccountNonReadOnlyStatus(user.isAccountNonReadOnlyStatus());
        log.debug("Права пользователя {} {}", user.getEmail(), !editedUser.isAccountNonReadOnlyStatus() ? "ReadOnly" : "Read & Write");
        editedUser.setIsAccountNonBlockedStatus(user.getIsAccountNonBlockedStatus());
        log.debug("Пользователь {} {}", user.getEmail(), !editedUser.getIsAccountNonBlockedStatus() ? "заблокирован" : "разблокирован");
        if (!user.getPassword().equals("")) {
            editedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(editedUser);
    }

    @Override
    @Transactional
    public void updateUserFromController(User user) {
        userRepository.saveAndFlush(user);
    }

    /**
     * Изменение пароля пользователя.
     * @param id идентификатор пользователя.
     * @param oldPassword старый пароль.
     * @param newPassword новый пароль.
     * @return false если ввести неправильно текущий пароль,
     * возвращает false если новый пароль не соответствует требованиям,
     * true при успешном изменении.
     */
    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        if (!ValidationUtils.isValidPassword(newPassword)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    /**
     * Метод сервиса для добавления нового адреса пользователю
     * @param user переданный пользователь
     * @param address новый адрес для пользователя
     * @throws UserNotFoundException вылетает, если пользователь не найден в БД
     */
    @Transactional
    @Override
    public boolean addNewAddressForUser(User user, Address address) {
        User usertoUpdate = findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        Optional<Address> addressFromDB = addressService.findSameAddress(address);
        if (addressFromDB.isPresent() && !usertoUpdate.getUserAddresses().contains(addressFromDB.get())) {
            Address addressToAdd = addressFromDB.get();
            if (usertoUpdate.getUserAddresses() != null) {
                usertoUpdate.getUserAddresses().add(addressToAdd);
            } else {
                usertoUpdate.setUserAddresses(Collections.singleton(address));
            }
            updateUser(usertoUpdate);
            return true;
        }
        if (addressFromDB.isEmpty()) {
            Address addressToAdd = addressService.addAddress(address);
            if (usertoUpdate.getUserAddresses() != null) {
                usertoUpdate.getUserAddresses().add(addressToAdd);
            } else {
                usertoUpdate.setUserAddresses(Collections.singleton(address));
            }
            updateUser(usertoUpdate);
            return true;
        }
        return false;
    }

    /**
     * Метод находит User-а по его логину email
     * @param email Юзера
     * @return User
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * Метод находит User-а по его id
     * @param id Юзера
     * @return User
     */
    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    /**
     * Метод возвращает залогиненного активного юзера - User из Authentication
     * Service method which builds and returns currently logged in User from Authentication
     * @param sessionID -параметр по которому вычисляется анонимный пользователь,
     * если его нет в бд -создает его используя параметр в качестве email
     * @return User
     */
    @Transactional
    @Override
    public User getCurrentLoggedInUser(String sessionID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // AnonymousAuthenticationToken happens when anonymous authentication is enabled

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        if (auth instanceof AnonymousAuthenticationToken) {
            if (findByEmail(sessionID).isEmpty()) {
                userRepository.save(new User(sessionID, null));
            }
            return findByEmail(sessionID).orElseThrow(() ->
                    new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
        }
        return findByEmail(auth.getName()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
    }

    /**
     * Метод работает только при включенной сессии
     * @return возращает юзера из базы данных
     */
    @Override
    public User getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // AnonymousAuthenticationToken happens when anonymous authentication is enabled
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return findByEmail(auth.getName()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
    }

    /**
     * Service method which finds and returns the User by token after email confirmation
     * @return User
     */
    @Transactional
    @Override
    public User getUserByToken(String token) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        log.debug("Token: {}", token);
        log.debug("ConfirmationToken: {}", confirmationToken);

        if (confirmationToken == null) {
            throw new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND);
        }
        return userRepository.findByEmail(confirmationToken.getUserEmail()).orElseThrow(() ->
                new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND));
    }

    /**
     * Метод, отправляющий сообщение с просьбой подтвердить подписку пользователю,
     * который нажал на "Подписаться на изменение цены".
     * @param email
     */
    public void sendConfirmationSubscribeLetter(String email) {
        String messageBody;
        if (commonSettingsService
                .getSettingByName("subscribe_confirmation_template")
                .getTextValue() != null) {
            String templateBody = commonSettingsService
                    .getSettingByName("subscribe_confirmation_template")
                    .getTextValue();
            String[] userName = {"Покупатель"};
            findByEmail(email).ifPresent(user -> {
                user.setConfirmReceiveEmail(ConfirmReceiveEmail.REQUESTED);
                user.setConfirmCommentsEmails(ConfirmReceiveEmail.REQUESTED);
                userRepository.save(user);
                if (user.getFirstName() != null) {
                    userName[0] = user.getFirstName();
                }
            });
            messageBody = templateBody.replaceAll("@@user@@", userName[0]);
            try {
                mailSenderService.sendHtmlMessage(email, "Подтвердите Вашу подписку", messageBody, "Subscribe confirmation");
            } catch (MessagingException e) {
                log.debug("Can not send mail about Subscribe confirmation to {}", email);
            }
        } else {
            log.debug("Шаблон рассылки при подтверждении рассылки в базе пустой ");
        }
    }
}
