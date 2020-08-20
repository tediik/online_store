package com.jm.online_store.config;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.OrderServiceImpl;
import com.jm.online_store.service.ProductService;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final ProductService productService;
    private final OrderServiceImpl orderService;

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
    public void ordersConstruct() {
        User customer = userService.findByEmail("customer@mail.ru").get();

        List<Product> products = new ArrayList<>();
        products.add(new Product("PC DEXP Jupiter P302", 500100.0, 3, 4.9));
        products.add(new Product("Notebook MSI GL75 10SFK-224RU", 100500.0, 7, 5.0));
        products.add(new Product("TV LED Xiaomi Mi 4S", 12345.0, 13, 4.8));
        products.add(new Product("Apple iPad 2019 128 Gb", 32123.0, 666, 4.5));
        products.add(new Product("Smartphone Samsung Galaxy S10 128 Gb", 54321.0, 23, 4.1));
        products.add(new Product("RC car buggy WLToys 12428", 6666.0, 100500, 4.6));

        for (Product product : products) {
            productService.saveProduct(product);
        }

        Order completedOrder1 = new Order(LocalDateTime.of(2019, 12, 31, 22, 10), Order.Status.COMPLETED);
        completedOrder1.setProducts(Set.of(
                productService.findProductById(1L).get(),
                productService.findProductById(2L).get()));

        Order completedOrder2 = new Order(LocalDateTime.of(2020, 1, 23, 13, 37), Order.Status.COMPLETED);
        completedOrder2.setProducts(Set.of(productService.findProductById(3L).get()));

        Order incartsOrder1 = new Order(LocalDateTime.of(2020, 3, 10, 16, 51), Order.Status.INCARTS);
        incartsOrder1.setProducts(Set.of(productService.findProductById(5L).get()));

        Order canceledOrder = new Order(LocalDateTime.of(2020, 6, 13, 15, 3), Order.Status.CANCELED);
        canceledOrder.setProducts(Set.of(
                productService.findProductById(4L).get(),
                productService.findProductById(5L).get(),
                productService.findProductById(6L).get()));

        Order incartsOrder2 = new Order(LocalDateTime.now(), Order.Status.INCARTS);
        incartsOrder2.setProducts(Set.of(productService.findProductById(6L).get()));

        orderService.addOrder(completedOrder1);
        orderService.addOrder(completedOrder2);
        orderService.addOrder(incartsOrder1);
        orderService.addOrder(canceledOrder);
        orderService.addOrder(incartsOrder2);

        customer.setOrders(Set.copyOf(orderService.findAll()));

        userService.updateUser(customer);
    }
}
