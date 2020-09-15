package com.jm.online_store.service.impl;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);
    private ConfirmationTokenRepository confirmTokenRepository = mock(ConfirmationTokenRepository.class);
    private MailSenderServiceImpl mailSenderService = mock(MailSenderServiceImpl.class);
    private AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private UserService userService = new UserServiceImpl(userRepository, roleRepository, confirmTokenRepository, mailSenderService, authenticationManager, passwordEncoder);

    private User user2;
    private User user3;

    @BeforeEach
    void init() {

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("pochta@google.com");
        user2.setPassword("2");

        user3 = new User("masha@mail.ru", "123",
                "Masha", "Ivanova", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));
        user3 = new User("ira@mail.ru", "123",
                "Ira", "Vasina", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));
        Role customerRole111 = new Role("ROLE_CUSTOMER");
        Optional<Role> custRole = Optional.of(customerRole111);
        Set<Role> customerRoles = new HashSet<>();
        customerRoles.add(custRole.get());
        user3.setRoles(customerRoles);
        user3.setId(3L);
        user3.setProfilePicture("def.jpg");
    }

    @Test
    public void shouldAddUserSuccessfully() {
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("Encoded password");
        when(userRepository.save(user3)).thenReturn(user3);
        userService.addUser(user3);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    public void updateUserProfileTest() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(userRepository.save(user3)).thenReturn(user3);
        assertNotNull(userService.updateUserProfile(user3));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUserAdminPanelTest() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(userRepository.save(user3)).thenReturn(user2);
        userService.updateUserAdminPanel(user3);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void regNewAccountTest() {
        when(confirmTokenRepository.save(new ConfirmationToken())).thenReturn(null);
        doNothing().when(mailSenderService).send(user3.getEmail(), "Activation code", "message", "emailType");
        userService.regNewAccount(user3);
        verify(confirmTokenRepository, times(1)).save(any());
    }

    @Test
    public void changeUsersMailTest(){
        when(confirmTokenRepository.save(new ConfirmationToken())).thenReturn(null);
        doNothing().when(mailSenderService).send(user3.getEmail(), "Activation code", "message", "emailType");
        userService.changeUsersMail(user3, "");
        verify(confirmTokenRepository, times(1)).save(any());
    }

    @Test
    public void activateUserTest() {
        String token = "";
        HttpServletRequest request = createMock(HttpServletRequest.class);
        when(confirmTokenRepository.findByConfirmationToken(token)).thenReturn(new ConfirmationToken());
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("password encoded");
        boolean res = userService.activateUser(token, request);
        assertTrue(res);
        verify(confirmTokenRepository, times(1)).findByConfirmationToken(any());
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    public void activateNewUsersMailTest() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        when((userRepository.findById(any()))).thenReturn(Optional.ofNullable(user3));
        when(confirmTokenRepository.findByConfirmationToken(" ")).thenReturn(new ConfirmationToken());
        when(userRepository.saveAndFlush(user3)).thenReturn(user3);
        boolean res = userService.activateNewUsersMail(" ", request);
        assertTrue(res);
        verify(confirmTokenRepository, times(1)).findByConfirmationToken(any());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void updateUserImageTest() throws IOException {
        byte[] array = new byte[]{1,2,3,4,5,66,7,7,8,9,77,8,9,0};
        MockMultipartFile file2 = new MockMultipartFile("File","file.jpg","img", array);
        when(userRepository.findById(3L)).thenReturn(Optional.ofNullable(user3));
        when(userRepository.save(user3)).thenReturn(user3);
        String result = userService.updateUserImage(user3.getId(), file2);
        assertNotNull(result);
        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).save(user3);
    }

    @Test
    public void deleteUserImageTest() throws IOException {
        when(userRepository.findById(3L)).thenReturn(Optional.ofNullable(user3));
        assertNotNull(userService.deleteUserImage(3L));
        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    public void addNewUserFromAdminTest(){
        Role role = new Role("ROLE_CUSTOMER");
        Optional<Role> cust = Optional.of(role);
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("password encoded");
        when(roleRepository.findByName(role.getName())).thenReturn(cust);
        when(userRepository.save(user3)).thenReturn(user3);
        userService.addNewUserFromAdmin(user3);
        verify(passwordEncoder, times(1)).encode(any());
        verify(roleRepository, times(1)).findByName(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserFromAdminPageTest(){
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("password encoded");
        when(userService.updateUserFromAdminPage(user3)).thenReturn(new User());
        User updateUser = userService.updateUserFromAdminPage(user3);
        Assert.notNull(updateUser,"Проверка, что NotNull");
        verify(passwordEncoder, times(2)).encode(any());
        verify(userRepository, times(1)).save(any());
    }



    @Test
    public void changePasswordTest_when_old_and_new_passwords_are_same(){
        when(userRepository.findById(3L)).thenReturn(Optional.ofNullable(user3));
        when(passwordEncoder.matches("oldPassword", user3.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("password encoded");
        boolean result = userService.changePassword(3L, "oldPassword", "newPassword");
        assertFalse(result);
        verify(userRepository, times(1)).findById(3L);
        verify(passwordEncoder, times(1)).matches("oldPassword", "123");
    }

    @Test
    public void changePasswordTest_when_old_and_new_passwords_are_different(){
        when(userRepository.findById(3L)).thenReturn(Optional.ofNullable(user3));
        when(passwordEncoder.matches("oldPassword", user3.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("password encoded");
        boolean result = userService.changePassword(3L, "oldPassword", "newPassword");
        assertTrue(result);
        verify(userRepository, times(1)).findById(3L);
        verify(passwordEncoder, times(1)).encode("newPassword");
    }
}