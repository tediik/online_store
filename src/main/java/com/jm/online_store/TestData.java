package com.jm.online_store;


import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class TestData {

    @Autowired
    private UserService userService;


    @Autowired
    private RoleService roleService;


    @PostConstruct
    public void roleConstruct() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");
        roleService.addRole(adminRole);
        roleService.addRole(userRole);

        User admin = new User("admin@mail.ru", "1");
        User user = new User("user@mail.ru", "1");
        User userOne = new User("premium@mail.ru", "1");
        User userTwo = new User("aloe@mail.ru", "1");

//        User user3 = new User("BillGates@Microsoft.com", "1");
//        User user4 = new User("SteveJobs@Macos.org", "1");

        Optional<Role> aRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> uRole = roleService.findByName("ROLE_USER");

        Set<Role> userRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        userRoles.add(uRole.get());
        adminRoles.add(aRole.get());
        adminRoles.add(uRole.get());

//        admin.setRoles(adminRoles);
//        user.setRoles(userRoles);
//        userOne.setRoles(userRoles);
//        userTwo.setRoles(userRoles);
//        user3.setRoles(roleSet);
//        user4.setRoles(roleSet);


        userService.addUser(user);
        userService.addUser(admin);
        userService.addUser(userOne);
        userService.addUser(userTwo);
//        userService.addUser(user3);
//        userService.addUser(user4);


    }


}
