package com.jm.online_store.config.security.vk;

import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Класс VkApiClient авторизация пользователя через VK API
 */
@Data
@Service
@Slf4j
public class VkApiClient {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    @Setter
    private PasswordEncoder passwordEncoder;

    /**
     * CLIENT_ID - указывается id-приложения
     */
    @Value("${spring.auth.vk.client_id}")
    private String client_id;

    /**
     * SECURE_KEY - указывается защищенный ключ приложения
     */
    @Value("${spring.auth.vk.secure_key}")
    private String secure_key;

    /**
     * Область видимости, к чему мы при подтверждении разрешаем доступ
     */
    @Value("${spring.auth.vk.scope}")
    private String scope;

    /**
     * URI редиректа, необходимо указывать в настройка приложения
     */
    @Value("${spring.auth.vk.redirect_uri}")
    private String redirect_uri;

    /**
     * Указывает тип отображения страницы авторизации. popup - всплывающее окно, page - в отдельном окне
     */
    @Value("${spring.auth.vk.display}")
    private String display;

    /**
     * возвращаемый тип ответа
     */
    @Value("${spring.auth.vk.response_type}")
    private String response_type;

    /**
     * версия vk api
     */
    @Value("${spring.auth.vk.version}")
    private String version;
    private String accessToken = "";
    private String userEmail = "";

    @Value("${spring.auth.vk.authorize_host}")
    private String authorize_host;

    @Value("${spring.auth.vk.token_host}")
    private String token_host;

    @Value("${spring.auth.vk.user-info-url-host}")
    private String userInfoUrl_host;

    private final RestTemplate restTemplate = new RestTemplate();

    private String code;

    private final HttpHeaders headers = new HttpHeaders();

    public String getUserInfoUrl() {
        return userInfoUrl_host +
                "&access_token=" + accessToken +
                "&v=" + version;
    }

    public String getVkAuthUrl() {
        return authorize_host +
                "?client_id=" + client_id +
                "&scope=" + scope +
                "&redirect_uri=" + redirect_uri +
                "&display=" + display +
                "&response_type=" + response_type +
                "&v=" + version;
    }

    public String getTokenUrl() {
        return token_host +
                "?client_id=" + client_id +
                "&client_secret=" + secure_key +
                "&redirect_uri=" + redirect_uri +
                "&code=" + code;
    }

    public User requestVkApi(String code) {
        this.code = code;
        if (code != null && !code.isEmpty()) {
            getAccessToken(code);
        }
        User newUser = getAccountInfo();
        if (!userService.isExist(newUser.getEmail())) {
            userService.addUser(newUser);
        }
        return newUser;
    }

    public User getAccountInfo() {

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(getUserInfoUrl(), HttpMethod.GET, entity, String.class);

        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> responseMap = jsonParser.parseMap(response.getBody());
        String firstName = (String) ((Map<String, Object>) ((List<Object>) responseMap.get("response")).get(0)).get("first_name");
        String lastName = (String) ((Map<String, Object>) ((List<Object>) responseMap.get("response")).get(0)).get("last_name");

        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Set<Role> customerRoles = new HashSet<>();
        customerRoles.add(custRole.get());

        return new User(userEmail, passwordEncoder.encode("1"), firstName, lastName, customerRoles);
    }

    public void getAccessToken(String code) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(getTokenUrl(), HttpMethod.GET, entity, String.class);

        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> responseMap = jsonParser.parseMap(response.getBody());
        accessToken = (String) responseMap.get("access_token");
        userEmail = (String) responseMap.get("email");
    }

    public void authUser(User user) {
        Authentication customAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);
    }
}
