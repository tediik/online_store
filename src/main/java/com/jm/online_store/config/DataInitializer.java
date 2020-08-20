package com.jm.online_store.config;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Description;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.Data;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@Component
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final CategoriesService categoriesService;

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

//    @PostConstruct
    public void productInit() {

        Categories category1 = new Categories("Laptop", "Computer");
        Categories category2 = new Categories("PC", "Computer");
        Categories category3 = new Categories("Smartphone", "Cellphone");

        Product product1 = new Product("Asus-NX4567", 299.9, 15, 4.0, "Computer");
        Product product2 = new Product("ACER-5432", 399.9, 10, 4.2, "Computer");
        Product product3 = new Product("Samsung-7893", 259.9, 20, 4.6, "Computer");

        Product product4 = new Product("NX-7893-PC-09878", 924.0, 3, 4.2, "Computer");
        Product product5 = new Product("ZX-7654-PC-1", 1223.9, 7, 4.7, "Computer");
        Product product6 = new Product("NY-2345-PC-453", 1223.9, 7, 4.7, "Computer");

        Product product7 = new Product("XIAOMI-Mi10", 599.9, 120, 4.9, "Cellphone");
        Product product8 = new Product("LG-2145", 439.5, 78, 3.9, "Cellphone");
        Product product9 = new Product("Apple-10", 1023.9, 74, 4.8, "Cellphone");

        Description description1 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "some additional info here");
        Description description2 = new Description("23464223", "ACER", 1, "654x38x245", "yellow", 2.1, "some additional info here");
        Description description3 = new Description("99966732", "Samsung", 3, "550x27x368", "white", 1.1, "some additional info here");
        Description description4 = new Description("33311432NXU", "ATop corp.", 3, "698x785x368", "black", 3.1, "some additional info here");
        Description description5 = new Description("33211678NXU", "ATop corp.", 3, "690x765x322", "black", 3.5, "some additional info here");
        Description description6 = new Description("333367653Rh", "Rhino corp.", 3, "612x678x315", "orange", 2.8, "some additional info here");
        Description description7 = new Description("X54355543455", "Xiaomi", 1, "115x56x13", "grey", 0.115, "some additional info here", 512, 512, "1920x960", true, "5.0");
        Description description8 = new Description("L55411165632", "LG", 2, "110x48x19", "black", 0.198, "some additional info here",  1024, 256, "1920x960", false, "4.0");
        Description description9 = new Description("A88563902273", "Apple corp.", 1, "112x55x8", "black", 0.176, "some additional info here",  2048, 128, "1024x480", true, "5.0");

        product1.setDescriptions(description1);
        product2.setDescriptions(description2);
        product3.setDescriptions(description3);
        product4.setDescriptions(description4);
        product5.setDescriptions(description5);
        product6.setDescriptions(description6);
        product7.setDescriptions(description7);
        product8.setDescriptions(description8);
        product9.setDescriptions(description9);

        category1.setProducts(Arrays.asList(product1, product2, product3));
        category2.setProducts(Arrays.asList(product4, product5, product6));
        category3.setProducts(Arrays.asList(product7, product8, product9));

        categoriesService.saveAll(Arrays.asList(category1, category2, category3));
    }
}
