package com.jm.online_store.service.interf;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    List<User> findByRole(String role);

    /*List<User> findByDayOfWeekForStockSend(byte dayNumber);*/

    Optional<User> findById(Long id);

    User findUserByEmail(String email);

    User findUserById(Long id);

    void addUser(User user);


/*    boolean checkUserStatus(String email, String password);*/

   /* void restoreUser(String email);*/

    void deleteByID(Long id);

    void updateUser(User user);

    User updateUserProfile(User user);

    void updateUserFromController(User user);

    void updateUserAdminPanel(User user);

    Optional<User> findByEmail(String email);

    User findByFirstName(String FirstName);

    boolean isExist(String email);

    void regNewAccount(User user);
 
    void changeUsersMail(User user, String newMail);

    void changeUsersPass(User user, String newMail);

    void sendConfirmationTokenToResetPassword(User user);

    void restorePassword(User user);

    boolean activateNewUsersMail(String code, HttpServletRequest request);

    boolean activateUser(String code, HttpServletRequest request);

    User updateUserFromAdminPage(User user);

    String updateUserImage(Long valueOf, MultipartFile imageFile) throws IOException;

    String deleteUserImage(Long userId) throws IOException;

    void addNewUserFromAdmin(User newUser);

    boolean changePassword(Long id, String oldPassword, String newPassword);

   /* void cancelSubscription(Long id);*/

    User getCurrentLoggedInUser();

    User getUserByToken(String token);

    boolean addNewAddressForUser(User user, Address address);

}