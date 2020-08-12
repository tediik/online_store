package com.jm.online_store.config;


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
public class DataInitializer {

    @Autowired
    private UserService userService;


    @Autowired
    private RoleService roleService;



    public void roleConstruct() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role managerRole = new Role("ROLE_MANAGER");

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);

        User admin = new User("admin@mail.ru", "1");
        User manager = new User("manager@mail.ru", "1");
        User customer = new User("customer@mail.ru", "1");


        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        managerRoles.add(managRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);




        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);



    }


}
