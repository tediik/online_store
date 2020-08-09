package com.jm.online_store.service;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmTokenRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        userRepository.save(user);
    }

    @Override
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
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
                        "http://localhost:9999/activate/%s",
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

        Set<Role> userRole = roleRepository
                .findByName("ROLE_CUSTOMER")
                .stream()
                .collect(Collectors.toSet());

        User user = new User();
        user.setEmail(confirmationToken.getUserEmail());
        user.setPassword(confirmationToken.getUserPassword());
        user.setRoles(userRole);

        userRepository.save(user);

        // Аутентификация сразу после подтверждения регистрации по ссылке на почте
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        authToken.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }
}
