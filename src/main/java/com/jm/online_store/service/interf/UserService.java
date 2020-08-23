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

    Optional<User> findByEmail(String username);

    boolean isExist(String email);

    void regNewAccount(User user);

    boolean activateUser(String code, HttpServletRequest request);

    void updateUserImage(Long valueOf, MultipartFile imageFile) throws IOException;

    void deleteUserImage(Long userId);
}



