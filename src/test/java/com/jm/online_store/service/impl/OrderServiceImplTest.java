package com.jm.online_store.service.impl;

import com.jm.online_store.exception.OrdersNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductInOrder;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.SalesReportDto;
import com.jm.online_store.repository.OrderRepository;
import com.jm.online_store.service.interf.OrderService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {
    @MockBean
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    private List<Order> orderList;
    Order order1;
    Order order2;
    Customer customer;
    LocalDateTime now;

    @BeforeEach
    void init() {
        orderList = new ArrayList<>();
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ivan");
        customer.setLastName("Petrov");
        customer.setEmail("ivan@mail.ru");
        order1 = new Order(LocalDateTime.now(), Order.Status.COMPLETED);
        order1.setId(1L);
        order1.setCustomer(customer);
        order1.setAmount(0L);
        order1.setOrderPrice((double) 0);
        order2 = new Order(LocalDateTime.now(), Order.Status.COMPLETED);
        order2.setId(2L);
        order2.setCustomer(customer);
        order2.setAmount(0L);
        order2.setOrderPrice((double) 0);
        orderList.add(order1);
        orderList.add(order2);
        now = LocalDateTime.now();
    }

    @Test
    void findAll() {
        when(orderRepository.findAll()).thenReturn(orderList);
        List<Order> testOrderList = orderService.findAll();
        assertEquals(2, testOrderList.size());
        assertFalse(testOrderList.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findAllByUserId() {
        when(orderRepository.findAllByUserId(1L))
                .thenReturn(orderList.stream()
                        .filter(order -> order.getCustomer().getId().equals(1L))
                        .collect(Collectors.toList()));

        List<Order> userOrdersList = orderRepository.findAllByUserId(1L);
        assertEquals(2, userOrdersList.size());
        assertFalse(userOrdersList.isEmpty());
        verify(orderRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void findAllByUserIdAndStatus() {
        when(orderRepository.findAllByUserIdAndStatus(1L, Order.Status.COMPLETED))
                .thenReturn(orderList.stream()
                        .filter(order -> order.getCustomer().getId().equals(1L))
                        .filter(order -> order.getStatus().equals(Order.Status.COMPLETED))
                        .collect(Collectors.toList()));

        List<Order> orderListByUserIdAndStatus = orderService
                .findAllByUserIdAndStatus(1L, Order.Status.COMPLETED);

        assertEquals(2, orderListByUserIdAndStatus.size());
        assertFalse(orderListByUserIdAndStatus.isEmpty());
        verify(orderRepository, times(1))
                .findAllByUserIdAndStatus(1L, Order.Status.COMPLETED);
    }

    @Test
    void findOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(order1));
        Optional<Order> optionalOrder = orderService.findOrderById(1L);
        assertNotNull(optionalOrder.get());
        assertEquals(1L, optionalOrder.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void addOrder() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.save(order)).thenReturn(order);
        Long id = orderService.addOrder(order);
        assertNotNull(id);
        assertNotNull(order.getAmount());
        assertNotNull(order.getOrderPrice());
        verify(orderRepository, times(1)).save(order);
        order = null;
    }

    @Test
    void updateOrder() {
        orderService.updateOrder(order1);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void findAllSalesBetween() {
        /*Создаем продукт*/
        Product product = new Product();
        product.setId(1L);
        product.setProduct("iPhone");
        /*Создаем ProductInOrder*/
        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setProduct(product);
        productInOrder.setAmount(1);
        /*Создаем лист ProductInOrder*/
        List<ProductInOrder> productInOrderList = new ArrayList<>();
        productInOrderList.add(productInOrder);
        /*Создаем 2 ордера и подставляем все что насоздавали выше*/
        Order completedOrder1 = Order.builder()
                .id(10L)
                .orderPrice(123D)
                .amount(2L)
                .dateTime(now)
                .status(Order.Status.COMPLETED)
                .customer(customer)
                .productInOrders(productInOrderList)
                .build();
        Order completedOrder2 = Order.builder()
                .id(11L)
                .orderPrice(12.3)
                .amount(1L)
                .dateTime(now.plusDays(1))
                .status(Order.Status.COMPLETED)
                .customer(customer)
                .productInOrders(productInOrderList)
                .build();
        /*делаем лист ордеров и добавляем туда 2 ордера которые сделали выше*/
        List<Order> completedOrders = new ArrayList<>();
        completedOrders.add(completedOrder1);
        completedOrders.add(completedOrder2);
        /*Создаем лист dto предполагаемый*/
        List<SalesReportDto> expectedSalesList = new ArrayList<>();
        completedOrders.forEach(order -> expectedSalesList.add(SalesReportDto.orderToSalesReportDto(order)));
        /*эмулируем возвращение репозиторием листа с выполнеными ордерами*/
        when(orderRepository
                .findAllByStatusEqualsAndDateTimeBetween(any(), any(), any()))
                .thenReturn(completedOrders);
        assertEquals(expectedSalesList, orderService.findAllSalesBetween(now.minusDays(2).toLocalDate(), now.plusDays(2).toLocalDate()),
                "Expected Sales List doesn't match actual");
    }

    @Test
    @DisplayName("Testing method findAllSalesBetween in class OrderServiceImpl to throw Exception if returned List is empty")
    void throwExceptionIfFindAllSalesBetweenReturnsEmptyList() {
        now = LocalDateTime.now();
        List<Order> emptyOrderList = new ArrayList<>();
        when(orderRepository
                .findAllByStatusEqualsAndDateTimeBetween(Order.Status.COMPLETED, now.minusDays(2), now.plusDays(2)))
                .thenReturn(emptyOrderList);
        OrdersNotFoundException thrownException =
                assertThrows(OrdersNotFoundException.class, () -> orderService
                                .findAllSalesBetween(now.minusDays(2).toLocalDate(), now.plusDays(2).toLocalDate()),
                        "Expected exception doesnt match actual");
    }

    @AfterEach
    void after() {
        orderList.clear();
        order1 = null;
        order2 = null;
        customer = null;
    }
}
