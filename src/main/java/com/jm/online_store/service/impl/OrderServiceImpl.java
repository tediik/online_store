package com.jm.online_store.service.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.OrdersNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllByCustomerId(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }

    @Override
    public List<Order> findAllByCustomerIdAndStatus(Long customerId, Order.Status status) {
        return orderRepository.findAllByCustomerIdAndStatus(customerId, status);
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * метод построения OrderDTO из Order, полученного из БД.
     * @param id идентификатор.
     * @return объект OrderDTO
     */
    @Override
    public OrderDTO findOrderDTOById(Long id) {
        return convertOrderToOrderDTO(orderRepository.findById(id).get());
    }

    /**
     * метод конвертации Order в OrderDTO для отсекания лишних данных.
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

    /**
     * Метод создает Excel файл с отчетом о продажах в промежутке дат
     * @param startDate - {@link LocalDate} beginning of period
     * @param endDate   - {@link LocalDate} end of period
     * @return - returns {@link List<SalesReportDto>}
     */
    @Override
    public XSSFWorkbook exportOrdersToExcel(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
        List<SalesReportDto> ordersList = findAllSalesBetween(startDate, endDate);
        if (ordersList.isEmpty()) {
            throw new OrdersNotFoundException(ExceptionEnums.ORDER.getText() + ExceptionConstants.NOT_FOUND);
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Orders");
        int rowCount = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.getAlignment();

        /*Средний чек*/
        XSSFRow row = sheet.createRow(rowCount++);
        XSSFCell cell = row.createCell(5);
        cell.setCellValue("средний чек за период:");
        cell.setCellStyle(style);
        cell = row.createCell(6);
        if (averageOrder(ordersList).isPresent()){
            cell.setCellValue(averageOrder(ordersList).getAsDouble());
        }
        /*таблица с отчетом*/
        row = sheet.createRow(rowCount++);

        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(0);

        cell = row.createCell(1);
        cell.setCellValue("Логин(email)");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(1);

        cell = row.createCell(2);
        cell.setCellValue("Имя");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(2);

        cell = row.createCell(3);
        cell.setCellValue("Дата заказа");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(3);

        cell = row.createCell(4);
        cell.setCellValue("Общее количество");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(4);

        cell = row.createCell(5);
        cell.setCellValue("Список товаров в заказе(кол-во)");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(5);

        cell = row.createCell(6);
        cell.setCellValue("Сумма заказа");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(6);

        for (SalesReportDto salesReportDto : ordersList) {
            row = sheet.createRow(rowCount++);

            cell = row.createCell(0);
            cell.setCellValue(salesReportDto.getOrderNumber());

            cell = row.createCell(1);
            cell.setCellValue(salesReportDto.getUserEmail());

            cell = row.createCell(2);
            cell.setCellValue(salesReportDto.getCustomerInitials());

            cell = row.createCell(3);
            cell.setCellValue(salesReportDto.getPurchaseDate());

            cell = row.createCell(4);
            cell.setCellValue(salesReportDto.getQuantity());

            cell = row.createCell(5);
            cell.setCellValue(salesReportDto.getListOfProducts());

            cell = row.createCell(6);
            cell.setCellValue(salesReportDto.getOrderSummaryPrice());
        }

        return workbook;
    }

    /**
     * Метод создает PDF файл с отчетом о продажах в промежутке дат
     * @param startDate - {@link LocalDate} beginning of period
     * @param endDate   - {@link LocalDate} end of period
     * @return - returns {@link List<SalesReportDto>}
     */
    @Override
    public void exportOrdersToPDF(LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException, DocumentException {
        List<SalesReportDto> ordersList = findAllSalesBetween(startDate, endDate);
        if (ordersList.isEmpty()) {
            throw new OrdersNotFoundException(ExceptionEnums.ORDER.getText() + ExceptionConstants.NOT_FOUND);
        }
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        Font infoFont = FontFactory.getFont("fonts/HelveticaRegular.ttf", BaseFont.IDENTITY_H, true, 10);
        Font titleFont = FontFactory.getFont("fonts/HelveticaRegular.ttf", BaseFont.IDENTITY_H, true, 16, Font.BOLD);
        document.open();

        document.add(new Paragraph("Отчет о продажах", titleFont));
        if (averageOrder(ordersList).isPresent()) {
            Paragraph orderAverage = new Paragraph("Средний чек : " + averageOrder(ordersList).getAsDouble(), infoFont);
            document.add(orderAverage);
        }

        PdfPTable table = new PdfPTable(7);
        Stream.of("ID", "Логин(email)", "Имя", "Дата заказа", "Общее количество", "Список товаров в заказе(кол-во)", "Сумма заказа")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, infoFont));
                    table.addCell(header);
                });
        for (SalesReportDto salesReportDto : ordersList) {
            table.addCell(String.valueOf(salesReportDto.getOrderNumber()));
            table.addCell(String.valueOf(salesReportDto.getUserEmail()));
            table.addCell(String.valueOf(salesReportDto.getCustomerInitials()));
            table.addCell(String.valueOf(salesReportDto.getPurchaseDate()));
            table.addCell(String.valueOf(salesReportDto.getQuantity()));
            table.addCell(String.valueOf(salesReportDto.getListOfProducts()));
            table.addCell(String.valueOf(salesReportDto.getOrderSummaryPrice()));
        }
        document.add(table);
        document.close();
    }

    /**
     * Метод вычисляет средний чек за период
     * @param orderList - список всех {@link Order}
     * @return - возвращает OptionalDouble
     */
    @Override
    public OptionalDouble averageOrder(List<SalesReportDto> orderList){
       return orderList.stream()
               .map(SalesReportDto::getOrderSummaryPrice)
               .mapToDouble(order -> order)
               .average();
    }
}
