package com.jm.online_store.service.interf;

import com.itextpdf.text.DocumentException;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.model.dto.SalesReportDto;
import com.opencsv.bean.StatefulBeanToCsv;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public interface OrderService {

    List<Order> findAll();

    List<Order> findAllByCustomerId(Long customerId);

    List<Order> findAllByCustomerIdAndStatus(Long customerId , Order.Status status);

    Optional<Order> findOrderById(Long id);

    Long addOrder(Order order);

    void updateOrder(Order order);

    OrderDTO findOrderDTOById(Long id);

    List<SalesReportDto> findAllSalesBetween(LocalDate startDate, LocalDate endDate);

    StatefulBeanToCsv<SalesReportDto> exportOrdersByCSV(LocalDate startDate, LocalDate endDate, HttpServletResponse response);

    XSSFWorkbook exportOrdersToExcel(LocalDate startDate, LocalDate endDate, HttpServletResponse response);

    void exportOrdersToPDF(LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException, DocumentException;

    OptionalDouble averageOrder(List<SalesReportDto> orderList);
}