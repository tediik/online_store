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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);
    private ConfirmationTokenRepository confirmTokenRepository = mock(ConfirmationTokenRepository.class);
    private MailSenderServiceImpl mailSenderService = mock(MailSenderServiceImpl.class);
    private AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private UserService userService = new UserServiceImpl(userRepository, roleRepository, confirmTokenRepository, mailSenderService, authenticationManager, passwordEncoder);

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    @BeforeEach
    void init() {

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("pochta@google.com");
        user2.setPassword("2");

        user1 = new User("masha@mail.ru", "123",
                "Masha", "Ivanova", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));

        user3 = new User("ira@mail.ru", "123",
                "Ira", "Vasina", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));
        user3.setId(3L);
        user3.setProfilePicture("def.jpg");

        user4 = new User();
        user4.setEmail("куцук!!!!!!5@ваваы@fds.eew");

        user5 = new User(null, null,
                null, null);

        user6 = new User("masha@mail.ru", "324",
                "Misha", "Ivanov", Collections.singleton(new Role(1L, "ROLE_MANAGER")));
    }

    @Test
    public void shouldAddUserSuccessfully() {
        when(userRepository.save(user3)).thenReturn(user3);
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.ofNullable(user1));

        userService.addUser(user3);

        assertThrows(InvalidEmailException.class, () -> {
            userService.addUser(user4);
        });
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.addUser(user1);
        });

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(2)).findByEmail(any(String.class));
        verify(passwordEncoder, times(3)).encode(any());
    }

    @Test
    public void updateUserProfileTest() {

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserProfile(user3);
        });

        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void updateUserAdminPanelTest() {

        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user5));
        when(userRepository.findById(user4.getId())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(user6.getId())).thenReturn(Optional.ofNullable(user2));
        when(userRepository.findByEmail(user6.getEmail())).thenReturn(Optional.ofNullable(user6));

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserAdminPanel(user3);
        });
        assertThrows(NullPointerException.class, () -> {
            userService.updateUserAdminPanel(user2);
        });
        assertThrows(InvalidEmailException.class, () -> {
            userService.updateUserAdminPanel(user4);
        });
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUserAdminPanel(user6);
        });

        verify(userRepository, times(4)).findById(any());
        verify(userRepository, times(2)).findByEmail(any());
    }

    @Test
    public void regNewAccountTest() {
        when(confirmTokenRepository.save(new ConfirmationToken())).thenReturn(null);
        doNothing().when(mailSenderService).send(user3.getEmail(), "Activation code", "message", "emailType");
        userService.regNewAccount(user3);
        verify(confirmTokenRepository, times(1)).save(any());
    }

    @Test
    public void changeUsersMailTest() {
        User newUser = Mockito.spy(user3);
        when(confirmTokenRepository.save(new ConfirmationToken())).thenReturn(null);
        doNothing().when(mailSenderService).send(user3.getEmail(), "Activation code", "message", "emailType");

        userService.changeUsersMail(newUser, "newMail");

        assertEquals("newMail", newUser.getEmail());

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
        when((userRepository.findById(any()))).thenReturn(Optional.ofNullable(user3));
        when(confirmTokenRepository.findByConfirmationToken(" ")).thenReturn(new ConfirmationToken());
        when(userRepository.saveAndFlush(user3)).thenReturn(user3);

        assertTrue(userService.activateNewUsersMail(" ", createMock(HttpServletRequest.class)));

        verify(confirmTokenRepository, times(1)).findByConfirmationToken(any());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void updateUserImageTest() throws IOException {

        byte[] array = new byte[]{1, 2, 3, 4, 5, 66, 7, 7, 8, 9, 77, 8, 9, 0};
        MockMultipartFile file = new MockMultipartFile("File", "file.jpg", "img", array);

        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(user3)).thenReturn(user3);

        assertThrows(NullPointerException.class, () -> {
            userService.updateUserImage(user1.getId(), mock(MockMultipartFile.class));
        });
        assertNotNull(userService.updateUserImage(user3.getId(), file), "check validness of whole updateUserImage() method");

        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).save(user3);
    }

    @Test
    public void deleteUserImageTest() throws IOException {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));

        assertNotNull(userService.deleteUserImage(3L), "Ожидается корректность выполения метода при удалении картинки");

        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    public void addNewUserFromAdminTest() {
        Role role = new Role("ROLE_CUSTOMER");
        Optional<Role> customer = Optional.of(role);
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("password encoded");
        when(roleRepository.findByName(role.getName())).thenReturn(customer);
        when(userRepository.save(user3)).thenReturn(user3);

        userService.addNewUserFromAdmin(user3);

        verify(passwordEncoder, times(1)).encode(any());
        verify(roleRepository, times(1)).findByName(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserFromAdminPageTest() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(passwordEncoder.encode(user3.getPassword())).thenReturn("password encoded");
        when(userService.updateUserFromAdminPage(user3)).thenReturn(new User());

        Assert.notNull(userService.updateUserFromAdminPage(user3), "Проверка, что NotNull");

        verify(passwordEncoder, times(2)).encode(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void changePasswordTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(passwordEncoder.matches("oldPassword", user1.getPassword())).thenReturn(true);
        when(userRepository.findById(user3.getId())).thenReturn(Optional.ofNullable(user3));
        when(passwordEncoder.matches("ollllldPassword", user3.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("password encoded");

        assertThrows(UserNotFoundException.class, () -> {
            userService.changePassword(7L, "oldPassword", "newPassword");
        });
        assertFalse(userService.changePassword(user3.getId(), "ollllldPassword", "newPassword"));
        assertTrue(userService.changePassword(user1.getId(), "oldPassword", "newPassword"));

        verify(userRepository, times(3)).findById(any());
        verify(passwordEncoder, times(1)).encode(any());
    }
}