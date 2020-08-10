package com.jm.online_store.service;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmTokenRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${spring.server.url}")
    private String urlActivate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }


    @Override
    public boolean emailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
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
    public boolean activateUser(String token, HttpServletRequest request) {

        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        if (confirmationToken == null) {
            return false;
        }

        Set<Role> userSetRoles = Collections.singleton(roleRepository.findByName("ROLE_CUSTOMER").get());

        User user = new User();
        user.setEmail(confirmationToken.getUserEmail());
        user.setPassword(confirmationToken.getUserPassword());
        user.setRoles(userSetRoles);

        addUser(user);

        try {
            request.login(user.getEmail(),confirmationToken.getUserPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return true;
    }
}
