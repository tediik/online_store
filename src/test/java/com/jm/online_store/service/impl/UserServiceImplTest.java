package com.jm.online_store.service.impl;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.AddressRepository;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.TemplatesMailingSettingsService;
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
import java.util.Optional;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private CustomerRepository customerRepository = mock(CustomerRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);
    private ConfirmationTokenRepository confirmTokenRepository = mock(ConfirmationTokenRepository.class);
    private MailSenderServiceImpl mailSenderService = mock(MailSenderServiceImpl.class);
    private AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private AddressService addressService = mock(AddressService.class);
    private AddressRepository addressRepository = mock(AddressRepository.class);
    private CommonSettingsServiceImpl commonSettingsService = mock(CommonSettingsServiceImpl.class);
    private FavouritesGroupService favouritesGroupService = mock(FavouritesGroupService.class);
    private TemplatesMailingSettingsService templatesMailingSettingsService = mock(TemplatesMailingSettingsServiceImpl.class);
    private UserService userService = new UserServiceImpl(userRepository, roleRepository , customerRepository, confirmTokenRepository, mailSenderService, authenticationManager, passwordEncoder, addressService, commonSettingsService, favouritesGroupService, templatesMailingSettingsService);

    private User userFullParameter;
    private User userWithIdEmailPassword;
    private User userFullParameterAndIdPicture;
    private User userWithInvalidEmail;
    private User userWithNull;
    private User userWithTheSameEmail;

    @BeforeEach
    void init() {
        userWithIdEmailPassword = new User();
        userWithIdEmailPassword.setId(2L);
        userWithIdEmailPassword.setEmail("pochta@google.com");
        userWithIdEmailPassword.setPassword("2");
        userFullParameter = new User("masha@mail.ru", "123",
                "Masha", "Ivanova", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));
        userFullParameter.setId(5L);
        userFullParameterAndIdPicture = new User("ira@mail.ru", "123",
                "Ira", "Vasina", Collections.singleton(new Role(1L, "ROLE_CUSTOMER")));
        userFullParameterAndIdPicture.setId(3L);
        userFullParameterAndIdPicture.setProfilePicture("def.jpg");
        userWithInvalidEmail = new User();
        userWithInvalidEmail.setEmail("куцук!!!!!!5@ваваы@fds.eew");
        userWithNull = new User(null, null,
                null, null);
        userWithTheSameEmail = new User("masha@mail.ru", "324",
                "Misha", "Ivanov", Collections.singleton(new Role(1L, "ROLE_MANAGER")));
    }

    @Test
    public void shouldAddUserSuccessfully() {
        when(userRepository.findByEmail(userFullParameter.getEmail())).thenReturn(Optional.ofNullable(userFullParameter));

        assertThrows(InvalidEmailException.class, () ->
                userService.addUser(userWithInvalidEmail));
        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.addUser(userFullParameter));

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(passwordEncoder, times(2)).encode(any());
    }

    @Test
    public void updateUserProfileTest() {

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUserProfile(userFullParameterAndIdPicture));

        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void updateUserAdminPanelTest() {
        when(userRepository.findById(userWithIdEmailPassword.getId())).thenReturn(Optional.ofNullable(userWithNull));
        when(userRepository.findById(userWithInvalidEmail.getId())).thenReturn(Optional.ofNullable(userFullParameter));
        when(userRepository.findById(userWithTheSameEmail.getId())).thenReturn(Optional.ofNullable(userWithIdEmailPassword));
        when(userRepository.findByEmail(userWithTheSameEmail.getEmail())).thenReturn(Optional.ofNullable(userWithTheSameEmail));

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUserAdminPanel(userFullParameterAndIdPicture));
        assertThrows(NullPointerException.class, () ->
                userService.updateUserAdminPanel(userWithIdEmailPassword));
        assertThrows(InvalidEmailException.class, () ->
                userService.updateUserAdminPanel(userWithInvalidEmail));
        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.updateUserAdminPanel(userWithTheSameEmail));

        verify(userRepository, times(4)).findById(any());
        verify(userRepository, times(2)).findByEmail(any());
    }

    @Test
    public void regNewAccountTest() {
        when(confirmTokenRepository.save(new ConfirmationToken())).thenReturn(null);
        doNothing().when(mailSenderService).send(userFullParameterAndIdPicture.getEmail(), "Activation code", "message", "emailType");
        userService.regNewAccount(userFullParameterAndIdPicture);
        verify(confirmTokenRepository, times(1)).save(any());
    }


    @Test
    public void activateUserTest() {
        String token = "";
        HttpServletRequest request = createMock(HttpServletRequest.class);
        when(confirmTokenRepository.findByConfirmationToken(token)).thenReturn(new ConfirmationToken());
        when(passwordEncoder.encode(userFullParameterAndIdPicture.getPassword())).thenReturn("password encoded");

        assertTrue(userService.activateUser(token, request));

        verify(confirmTokenRepository, times(1)).findByConfirmationToken(any());
        verify(passwordEncoder, times(1)).encode(any());
    }

    @Test
    public void activateNewUsersMailTest() {
        when((userRepository.findById(any()))).thenReturn(Optional.ofNullable(userFullParameterAndIdPicture));
        when(confirmTokenRepository.findByConfirmationToken(" ")).thenReturn(new ConfirmationToken());
        when(userRepository.saveAndFlush(userFullParameterAndIdPicture)).thenReturn(userFullParameterAndIdPicture);

        assertTrue(userService.activateNewUsersMail(" ", createMock(HttpServletRequest.class)));

        verify(confirmTokenRepository, times(1)).findByConfirmationToken(any());
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserImageTest() throws IOException {

        byte[] array = new byte[]{1, 2, 3, 4, 5, 66, 7, 7, 8, 9, 77, 8, 9, 0};
        MockMultipartFile file = new MockMultipartFile("File", "file.jpg", "img", array);
        when(userRepository.findById(userFullParameterAndIdPicture.getId())).thenReturn(Optional.ofNullable(userFullParameterAndIdPicture));
        when(userRepository.findById(userFullParameter.getId())).thenReturn(Optional.ofNullable(userFullParameter));

        assertThrows(NullPointerException.class, () ->
                userService.updateUserImage(userFullParameter.getId(), mock(MockMultipartFile.class)));
        assertNotNull(userService.updateUserImage(userFullParameterAndIdPicture.getId(), file), "check validness of whole updateUserImage() method");

        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).save(userFullParameterAndIdPicture);
    }

    @Test
    public void deleteUserImageTest() throws IOException {
        when(userRepository.findById(userFullParameterAndIdPicture.getId())).thenReturn(Optional.ofNullable(userFullParameterAndIdPicture));

        assertNotNull(userService.deleteUserImage(3L), "Ожидается корректность выполения метода при удалении картинки");

        verify(userRepository, times(1)).findById(3L);
    }

    @Test
    public void addNewUserFromAdminTest() {
        Role role = new Role("ROLE_CUSTOMER");
        Optional<Role> customer = Optional.of(role);
        when(passwordEncoder.encode(userFullParameterAndIdPicture.getPassword())).thenReturn("password encoded");
        when(roleRepository.findByName(role.getName())).thenReturn(customer);
        when(userRepository.save(userFullParameterAndIdPicture)).thenReturn(userFullParameterAndIdPicture);

        userService.addNewUserFromAdmin(userFullParameterAndIdPicture);

        verify(passwordEncoder, times(1)).encode(any());
        verify(roleRepository, times(1)).findByName(any());
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    public void updateUserFromAdminPageTest() {
        when(userRepository.findById(userFullParameterAndIdPicture.getId())).thenReturn(Optional.ofNullable(userFullParameterAndIdPicture));
        when(passwordEncoder.encode(userFullParameterAndIdPicture.getPassword())).thenReturn("password encoded");
        when(userService.updateUserFromAdminPage(userFullParameterAndIdPicture)).thenReturn(new User());

        Assert.notNull(userService.updateUserFromAdminPage(userFullParameterAndIdPicture), "Проверка, что NotNull");

        verify(passwordEncoder, times(2)).encode(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void changePasswordTest() {
        when(userRepository.findById(userFullParameter.getId())).thenReturn(Optional.ofNullable(userFullParameter));
        when(passwordEncoder.matches("oldPassword", userFullParameter.getPassword())).thenReturn(true);
        when(userRepository.findById(userFullParameterAndIdPicture.getId())).thenReturn(Optional.ofNullable(userFullParameterAndIdPicture));
        when(passwordEncoder.matches("ollllldPassword", userFullParameterAndIdPicture.getPassword())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                userService.changePassword(7L, "oldPassword", "newPassword"));
        assertFalse(userService.changePassword(userFullParameterAndIdPicture.getId(), "ollllldPassword", "n1ewPassword9"));
        assertTrue(userService.changePassword(userFullParameter.getId(), "oldPassword", "n1ewPassword9"));

        verify(userRepository, times(3)).findById(any());
        verify(passwordEncoder, times(1)).encode(any());
    }
    @Test
    public void addNewAddressForUserTest() {
        Address addressToAdd = new Address("420077","Татарстан","Казань","Революционная","25",false);
        when(addressService.findSameAddress(any())).thenReturn(Optional.of(addressToAdd));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithIdEmailPassword));
        assertTrue(userService.addNewAddressForUser(userWithIdEmailPassword,addressToAdd));
        verify(addressRepository,times(0)).save(any());
        verify(userRepository,times(1)).save(any());

        userWithIdEmailPassword.setUserAddresses(Collections.singleton(addressToAdd));
        when(addressService.findSameAddress(any())).thenReturn(Optional.of(addressToAdd));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithIdEmailPassword));
        assertFalse(userService.addNewAddressForUser(userWithIdEmailPassword,addressToAdd));
    }
}
