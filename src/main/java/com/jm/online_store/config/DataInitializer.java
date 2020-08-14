package com.jm.online_store.config;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

//    @PostConstruct
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
