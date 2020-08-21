package com.jm.online_store.config;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.OrderServiceImpl;
import com.jm.online_store.service.ProductInOrderService;
import com.jm.online_store.service.ProductService;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
    private final ProductInOrderService productInOrderService;

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

        List<Long> productsIds = new ArrayList<>();
        for (Product product : products) {
            productsIds.add(productService.saveProduct(product));
        }

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(LocalDateTime.of(2019, 12, 31, 22, 10), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 1, 23, 13, 37), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 3, 10, 16, 51), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.of(2020, 6, 13, 15, 3), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.now(), Order.Status.INCARTS));

        List<Long> ordersIds = new ArrayList<>();
        for (Order order : orders) {
            ordersIds.add(orderService.addOrder(order));
        }

        productInOrderService.addToOrder(productsIds.get(0), ordersIds.get(0), 1);
        productInOrderService.addToOrder(productsIds.get(1), ordersIds.get(0), 2);

        productInOrderService.addToOrder(productsIds.get(2), ordersIds.get(1), 1);

        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(2), 2);

        productInOrderService.addToOrder(productsIds.get(3), ordersIds.get(3), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(3), 2);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(3), 3);

        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(4), 3);

        customer.setOrders(Set.copyOf(orderService.findAll()));

        userService.updateUser(customer);
    }
}
