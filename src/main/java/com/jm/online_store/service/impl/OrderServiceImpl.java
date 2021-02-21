package com.jm.online_store.service.impl;

import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.OrdersNotFoundException;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.model.dto.SalesReportDto;
import com.jm.online_store.repository.OrderRepository;
import com.jm.online_store.service.interf.OrderService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> findAllByUserIdAndStatus(Long userId, Order.Status status) {
        return orderRepository.findAllByUserIdAndStatus(userId, status);
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * метод построения OrderDTO из Order, полученного из БД.
     *
     * @param id идентификатор.
     * @return объект OrderDTO
     */
    @Override
    public OrderDTO findOrderDTOById(Long id) {
        return convertOrderToOrderDTO(orderRepository.findById(id).get());
    }

    /**
     * метод конвертации Order в OrderDTO для отсекания лишних данных.
     *
     * @param order объект order.
     * @return объект OrderDTO.
     */
    private OrderDTO convertOrderToOrderDTO(Order order) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(order, OrderDTO.class);
    }

    /**
     * Метод добавления заказа.
     * Первоначально кол-во продуктов и общая стоимость равны 0,
     * эти поля изменяются методом {@link ProductInOrderServiceImpl#addToOrder(long, long, int)}
     *
     * @param order заказ, сохраняемый в базу
     * @return id сохранённого объекта
     */
    @Override
    public Long addOrder(Order order) {
        order.setAmount(Long.valueOf(0));
        order.setOrderPrice(Double.valueOf(0));
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    /**
     * Method finds all orders with status Status.COMPLETED between start and end date
     *
     * @param startDate - {@link LocalDate} beginning of period
     * @param endDate   - {@link LocalDate} end of period
     * @return - returns {@link List<SalesReportDto>}
     */
    @Override
    public List<SalesReportDto> findAllSalesBetween(LocalDate startDate, LocalDate endDate) {
        List<Order> completedOrders = orderRepository.findAllByStatusEqualsAndDateTimeBetween(Order.Status.COMPLETED, startDate.atStartOfDay(), endDate.atTime(23,59,59));
        List<SalesReportDto> salesList = new ArrayList<>();
        completedOrders.forEach(order -> salesList.add(SalesReportDto.orderToSalesReportDto(order)));
        return salesList;
    }

    @Override
    public StatefulBeanToCsv<SalesReportDto> exportOrdersByCSV(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
        List<SalesReportDto> ordersList = findAllSalesBetween(startDate, endDate);
        if (ordersList.isEmpty()) {
            throw new OrdersNotFoundException(ExceptionEnums.ORDER.getText() + ExceptionConstants.NOT_FOUND);
        }
        StatefulBeanToCsv<SalesReportDto> writer = null;
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            writer = new StatefulBeanToCsvBuilder<SalesReportDto>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(';')
                    .withOrderedResults(true)
                    .build();
            writer.write(ordersList);
        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
             e.printStackTrace();
        }
        return writer;
    }
}
