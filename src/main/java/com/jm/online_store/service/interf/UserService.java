package com.jm.online_store.service.interf;

import com.jm.online_store.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    void addUser(User user);

    void deleteByID(Long id);

    void updateUser(User user);

    void updateUserAdminPanel(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByFirstName(String FirstName);

    boolean isExist(String email);

    void regNewAccount(User user);

    void changeUsersMail(User user, String newMail);

    boolean activateNewUsersMail(String code, HttpServletRequest request);

    boolean activateUser(String code, HttpServletRequest request);

    String updateUserImage(Long valueOf, MultipartFile imageFile) throws IOException;

    String deleteUserImage(Long userId) throws IOException;
}



