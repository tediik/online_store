package com.jm.online_store.controller.restjwt;


import com.jm.online_store.config.security.jwt.JwtTokenProvider;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.model.dto.AuthenticationRequestDto;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication requests (login, logout, register, etc.)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
public class AuthenticationRestControllerJWT {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "Выдает user, email и Bearer токен в ответ на запрос; в body указывается JSON {\n" +
            "    \"email\":\"****@mail.ru\",\n" +
            "    \"password\":\"*****\"\n" +
            "}")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String email = requestDto.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, requestDto.getPassword()));
            User user = userService.findUserByEmail(email);
            UserDto userDto = new UserDto();
            if (user == null) {
                throw new UsernameNotFoundException("User with email: " + email + " not found");
            }
            String token = jwtTokenProvider.createToken(email, user.getRoles());
            Map<Object, Object> response = new HashMap<>();
            response.put("user", userDto.fromUser(user));
            response.put("email", email);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
