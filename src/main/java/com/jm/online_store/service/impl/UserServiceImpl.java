package com.jm.online_store.service.impl;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.passay.CharacterData;
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
import java.util.HashSet;
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

    @Value("${spring.server.url}")
    private String urlActivate;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Получение списка пользователей, отсортированных в соответствии с выбранной ролью
     *
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
     *
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
        if (user.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Добавление нового пользователя.
     * Проверяется пароль на валидность, отсутствие пользователя с данным email (уникальное значение).
     *
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
     *
     * @param user пользователь, полученный из контроллера.
     */
    @Override
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserProfile(User user) {
        User updateUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        updateUser.setFirstName(user.getFirstName());
        updateUser.setLastName(user.getLastName());
        updateUser.setBirthdayDate(user.getBirthdayDate());
        updateUser.setUserGender(user.getUserGender());
        return userRepository.save(updateUser);
    }

    @Override
    @Transactional
    public void updateUserAdminPanel(@NotNull User user) {
        User editUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        if (!editUser.getEmail().equals(user.getEmail())) {
            if (isExist(user.getEmail())) {
                throw new EmailAlreadyExistsException();
            } else if (ValidationUtils.isNotValidEmail(user.getEmail())) {
                throw new InvalidEmailException();
            }
            editUser.setEmail(user.getEmail());
        }
        editUser.setRoles(persistRoles(user.getRoles()));
        log.debug("editUser: {}", editUser);
        userRepository.save(editUser);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Регистрация нового User.
     *
     * @param userForm User построенный из данных формы.
     */
    @Override
    @Transactional
    public void regNewAccount(User userForm) {
        ConfirmationToken confirmationToken = new ConfirmationToken(userForm.getEmail(), userForm.getPassword());
        confirmTokenRepository.save(confirmationToken);

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to online-store. Please, visit next link: " +
                        urlActivate + "/activate/%s",
                userForm.getEmail(),
                confirmationToken.getConfirmationToken()
        );
        mailSenderService.send(userForm.getEmail(), "Activation code", message, "Confirmation");
    }

    /**
     * Method generates confirmation token based on users ID and Email adress
     * Sends generated token to new users email
     */
    @Override
    @Transactional
    public void changeUsersMail(User user, String newMail) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), newMail);
        confirmTokenRepository.save(confirmationToken);

        String message = String.format(
                "Здравствуйте, %s! \n" +
                        "Вы запросили изменение адреса электронной почты. Подтвердите, пожалуйста, по ссылке: " +
                        urlActivate + "/activatenewmail/%s",
                user.getFirstName(),
                confirmationToken.getConfirmationToken()
        );

        mailSenderService.send(newMail, "Activation code", message, "email address validation");
    }

    /**
     * Устанавливет переданному пользователю новый пароль.
     *
     * @param user        Пользователь
     * @param newPassword новый пароль
     */
    @Override
    @Transactional
    public void changeUsersPass(User user, String newPassword) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), user.getEmail());
        confirmTokenRepository.save(confirmationToken);

        String message = String.format(
                "Привет, %s! \n Ваш пароль изменен ",
                user.getFirstName()
        );
        mailSenderService.send(user.getEmail(), "Пароль успешно изменен", message, "pass change");
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("для юзера с логином {} установлен новый пароль: {}", user.getEmail(), newPassword);
    }

    /**
     * Генерирует новый пароль и отправляет его пользователю на почту.
     *
     * @param user - Покупатель, запросивший смену пароля.
     */
    @Transactional
    @Override
    public void restorePassword(User user) {
        String newPass = generatePassayPassword();
        mailSenderService.send(user.getEmail(), "Сгенерирован временный новый пароль", newPass, "pass change");
        user.setPassword(passwordEncoder.encode(newPass));
        log.info("для юзера с логином {} сгенерирован новый пароль: {}", user.getEmail(), newPass);
    }

    /**
     * Метод использует библиотеку Passay для генерации рандомного пароля в соответствии с указанными требованиями к паролю
     *
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
     */
    @Override
    @Transactional
    public void sendConfirmationTokenToResetPassword(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user.getId(), user.getEmail());
        confirmTokenRepository.save(confirmationToken);
        String message = String.format(
                "Привет, %s! \n Вы сделали запрос на сброс пароля, для подтверждения перейдите по ссылке " + urlActivate + "/restorepassword/%s",
                user.getFirstName(),
                confirmationToken.getConfirmationToken()
        );
        mailSenderService.send(user.getEmail(), "Ссылка подтверждение для генерации нового пароля", message, "pass change");
        log.info("На почту: {} отправлена ссылка-подтверждение для генерации нового пароля. Текст: {}", user.getEmail(), message);
    }

    /**
     * метод проверки активации пользователя.
     *
     * @param token   модель, построенная на основе пользователя, после подтверждения
     * @param request параметры запроса.
     * @return булево значение "true or false"
     */
    @Override
    @Transactional
    public boolean activateUser(String token, HttpServletRequest request) {

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

        try {
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
        User user = userRepository.findById(confirmationToken.getUserId()).orElseThrow(UserNotFoundException::new);
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
        log.debug("Failed to store file - file is not present {}", uniqueFilename);
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
     * Service method to add new user from admin page
     *
     * @param newUser
     */
    @Override
    @Transactional
    public void addNewUserFromAdmin(User newUser) {
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
     *
     * @param user
     * @return User
     */
    @Override
    @Transactional
    public User updateUserFromAdminPage(User user) {
        User editedUser = userRepository.findById(user.getId()).get();
        Set<Role> newRoles = persistRoles(user.getRoles());
        editedUser.setRoles(newRoles);
        editedUser.setEmail(user.getEmail());
        editedUser.setFirstName(user.getFirstName());
        editedUser.setLastName(user.getLastName());
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

    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
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
     *
     * @param user    переданный пользователь
     * @param address новый адрес для пользователя
     * @throws UserNotFoundException вылетает, если пользователь не найден в БД
     */
    @Transactional
    @Override
    public boolean addNewAddressForUser(User user, Address address) {
        User usertoUpdate = findById(user.getId()).orElseThrow(UserNotFoundException::new);
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
        if (!addressFromDB.isPresent()) {
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
     * Service method which builds and returns currently logged in User from Authentication
     *
     * @return User
     */
    public User getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // AnonymousAuthenticationToken happens when anonymous authentication is enabled
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return findByEmail(auth.getName()).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Service method which finds and returns the User by token after email confirmation
     *
     * @return User
     */
    @Transactional
    @Override
    public User getUserByToken(String token) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        log.debug("Token: {}", token);
        log.debug("ConfirmationToken: {}", confirmationToken);

        if (confirmationToken == null) {
            throw new UserNotFoundException();
        }
        return userRepository.findByEmail(confirmationToken.getUserEmail()).orElseThrow(UserNotFoundException::new);
    }


    /**
     * Метод, отправляющий сообщение с просьбой подтвердить подписку пользователю,
     * который нажал на "Подписаться на изменение цены".
     * @param email
     */
    public void sendConfirmationSubscribeLetter(String email) {
        String templateBody = commonSettingsService
                .getSettingByName("subscribe_confirmation_template")
                .getTextValue();
        String messageBody;
        String usersName = "Покупатель";
        Optional<User> user = findByEmail(email);
        if (user.isPresent()) {
            User userFromDB = user.get();
            userFromDB.setConfirmReceiveEmail(User.ConfirmReceiveEmail.REQUESTED);
            System.out.println(userFromDB.getConfirmReceiveEmail());
            userRepository.save(userFromDB);
            if (user.get().getFirstName() != null) {
                usersName = userFromDB.getFirstName();
            }
        }
        messageBody = templateBody.replaceAll("@@user@@", usersName);
        try {
            mailSenderService.sendHtmlMessage(email, "Подтвердите Вашу подписку", messageBody, "Subscribe confirmation");
        } catch (MessagingException e) {
            log.debug("Can not send mail about Subscribe confirmation to {}", email);
        }
    }
}
