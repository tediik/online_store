//package com.jm.online_store;
//
//
//import com.jm.online_store.model.Role;
//import com.jm.online_store.model.User;
//import com.jm.online_store.service.RoleService;
//import com.jm.online_store.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class TestData {
//
//    @Autowired
//    private UserService userService;
//
//
//    @Autowired
//    private RoleService roleService;
//
//
//    @PostConstruct
//    public void roleConstruct() {
//        Role adminRole = new Role("ROLE_ADMIN");
//        Role userRole = new Role("ROLE_USER");
//        Role managerRole = new Role("ROLE_MANAGER");

//        roleService.addRole(adminRole);
//        roleService.addRole(userRole);
//        roleService.addRole(managerRole);
//
//        User admin = new User("admin@mail.ru", "1");
//        User manager = new User("manager@mail.ru", "1");
//        User user = new User("visitor@mail.ru", "1");

//
//        Optional<Role> aRole = roleService.findByName("ROLE_ADMIN");
//        Optional<Role> uRole = roleService.findByName("ROLE_USER");
//        Optional<Role> mRole = roleService.findByName("ROLE_MANAGER");
//
//        Set<Role> userRoles = new HashSet<>();
//        Set<Role> adminRoles = new HashSet<>();
//        Set<Role> managerRoles = new HashSet<>();

//        userRoles.add(uRole.get());
//        adminRoles.add(aRole.get());
//        adminRoles.add(uRole.get());
//        managerRoles.add(managerRole);
//
//        manager.setRoles(managerRoles);
//        admin.setRoles(adminRoles);
//        user.setRoles(userRoles);
//
//
//
//
//        userService.addUser(manager);
//        userService.addUser(user);
//        userService.addUser(admin);
//
//
//
//    }
//
//
//}
