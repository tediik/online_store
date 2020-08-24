package com.jm.online_store.service.impl;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.UserService;
import lombok.extern.slf4j.Slf4j;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenRepository confirmTokenRepository;
    private final MailSenderServiceImpl mailSenderService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    @Setter
    private PasswordEncoder passwordEncoder;

    @Value("${spring.server.url}")
    private String urlActivate;

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }


    @Override
    public boolean isExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public void addUser(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            throw new InvalidEmailException();
        }
        if (isExist(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.setRoles(persistRoles(user.getRoles()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(@NotNull User user) {
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

    @Override
    @Transactional
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

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

        mailSenderService.send(userForm.getEmail(), "Activation code", message);
    }

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

        User user = new User();
        user.setEmail(confirmationToken.getUserEmail());
        user.setPassword(confirmationToken.getUserPassword());
        user.setRoles(userRoles);

        addUser(user);

        try {
            request.login(user.getEmail(), confirmationToken.getUserPassword());
        } catch (ServletException e) {
            log.debug("Servlet exception from ActivateUser Method {}", e.getMessage());
        }
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

    @Override
    @Transactional
    public void updateUserImage(Long userId, MultipartFile file) {
        //delete current user's profile picture
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //Save received image File to Uploads folder in users directory
        if (!file.isEmpty()) {
            deleteUserImage(userId);
            String uploadDirectory = System.getProperty("user.dir") + "/uploads";
            Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            try {
                byte[] bytes = file.getBytes();
                Files.write(fileNameAndPath, bytes);
                //Set user's profile picture
                User user = userRepository.findById(userId).get();
                user.setProfilePicture(filename);
                userRepository.save(user);
            } catch (IOException e) {
                log.debug("Failed to store file {} {}", fileNameAndPath, e.getMessage());
            }
        }
        log.debug("Failed to store file - file is not present {}", filename);
    }

    @Override
    @Transactional
    public void deleteUserImage(Long userId) {
        final String defaultAvatar = StringUtils.cleanPath("def.jpg");
        User user = userRepository.findById(userId).get();
        //Get profilePicture name from User and delete this profile picture from Uploads
        String uploadDirectory = System.getProperty("user.dir") + "/uploads";
        Path fileNameAndPath = Paths.get(uploadDirectory, user.getProfilePicture());
        //Check if deleting picture is not a default avatar
        try {
            if (!fileNameAndPath.getFileName().toString().equals(defaultAvatar)) {
                Files.delete(fileNameAndPath);
            }
        } catch (IOException e) {
            log.debug("Failed to delete file {} {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
        }
        //Set a default avatar as a user profilePicture
        setDefaultUserAvatar(userId);
    }

    public void setDefaultUserAvatar(Long userId) {
        final String defaultAvatar = StringUtils.cleanPath("def.jpg");
        User user = userRepository.findById(userId).get();
        user.setProfilePicture(defaultAvatar);
        userRepository.save(user);
    }
}
