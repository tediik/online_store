package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.enums.Response;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.SalesReportDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.SentStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/manager")
@Api(description = "Rest controller for manage subscribes from manager page")
public class ManagerReportsRestController {
    private final CustomerService customerService;
    private final SentStockService sentStockService;
    private final OrderService orderService;
    private final Type listType = new TypeToken<List<CustomerDto>>() {
    }.getType();
    private final ModelMapper modelMapper;

    /**
     * метод получения списка пользователей, подписанных на рассылку по номеру дня
     * или пустой список
     * @param dayOfWeek день недели
     * @return список пользователей
     */
    @GetMapping("/users/{dayOfWeek}")
    @ApiOperation(value = "Returns list of users subscribing on the report by day number",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscribed customers has been found"),
            @ApiResponse(code = 200, message = "Subscribed customers hasn't been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<CustomerDto>>> allUsersByDayOfWeek(@PathVariable String dayOfWeek) {
        List<CustomerDto> returnValue = modelMapper.map(customerService.findByDayOfWeekForStockSend(dayOfWeek), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод поиска пользователя, подписанного на рассылку по email "на лету"
     * или пустой список
     * @param email почта подписчика
     * @return список пользователей
     */
    @GetMapping("/user/{email}")
    @ApiOperation(value = "Find user subscribing on the report by email",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscribed customer has been found"),
            @ApiResponse(code = 200, message = "Subscribed customer hasn't been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<CustomerDto>>> findSubscriberByEmail(@PathVariable String email) {
        List<CustomerDto> returnValue = modelMapper.map(customerService.findSubscriberByEmail(email), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод отмены подписки со страницы менеджера.
     * @param id пользователя
     * @return строковый ответ с описанием результата операции по отмене подписки для пользователя
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "Method for cancel subscribe  from manager page",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscription has been canceled"),
            @ApiResponse(code = 404, message = "Subscribed customer hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> cancelSubscription(@PathVariable Long id) {
        customerService.cancelSubscription(id);
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(Response.HAS_BEEN_UPDATED.getText(), id),
                Response.NO_ERROR.getText()));
    }

    /**
     * метод поиска отправленных акций в интервале дат
     * @param beginDate дата, от которой будет осуществляться поиск
     * @param endDate   дата, до которой будет осуществляться поиск
     * @return Словарь, где ключом является объект LocalDate, а значением его частота
     */
    @GetMapping("/report")
    @ApiOperation(value = "Method for searching for sent stocks in day interval",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stocks have been found"),
            @ApiResponse(code = 200, message = "Stocks haven't been found")
    })
    public ResponseEntity<ResponseDto<Map<LocalDate, Long>>> allSentStocks(
            @RequestParam(value = "beginDate", required = false) String beginDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        LocalDate begin = LocalDate.parse(beginDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(new ResponseDto<>(true, sentStockService.getSentStocksMap(begin, end)));
    }

    /**
     * Метод поиска продаж в интервале дат
     * @param stringStartDate - начальная дата
     * @param stringEndDate   - конечная дата
     * @return - {@link ResponseEntity} со списком {@link Order} в статусе Complete
     */
    @GetMapping("/sales")
    @ApiOperation(value = "Get mapping for get request to response with sales during the custom date range",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "sales have been found"),
            @ApiResponse(code = 200, message = "sales haven't been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<SalesReportDto>>> getSalesForCustomRange(@RequestParam String stringStartDate,
                                                                                    @RequestParam String stringEndDate) {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);
        return ResponseEntity.ok(new ResponseDto<>(true, orderService.findAllSalesBetween(startDate, endDate)));
    }

    /**
     * Mapping for csv export.
     * @param stringStartDate - beginning of the period that receives from frontend in as String
     * @param stringEndDate   - end of the period that receives from frontend in as String
     * @param response        - response to write back stream with csv
     * @return - ResponseEntity
     */
    @GetMapping("/sales/exportCSV")
    @ApiOperation(value = "Mapping for csv export",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "orders have been sent"),
            @ApiResponse(code = 404, message = "orders haven't been found"),
            @ApiResponse(code = 500, message = "problem with writing CSV")
    })
    public ResponseEntity<ResponseDto<FileSystemResource>> getSalesForCustomRangeAndExportToCSV(@RequestParam String stringStartDate,
                                                                                                @RequestParam String stringEndDate,
                                                                                                HttpServletResponse response) {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);
        orderService.exportOrdersByCSV(startDate, endDate, response);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод для выгрузки отчета в Excel.
     * @param stringStartDate - beginning of the period that receives from frontend in as String
     * @param stringEndDate   - end of the period that receives from frontend in as String
     * @param response        - response to write back stream with Excel
     * @return - ResponseEntity
     */
    @GetMapping("/sales/exportExcel")
    @ApiOperation(value = "метод экспорта в Excel",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "orders have been sent"),
            @ApiResponse(code = 404, message = "orders haven't been found"),
            @ApiResponse(code = 500, message = "problem with writing Excel")
    })
    public ResponseEntity<ResponseDto<FileSystemResource>> getSalesForCustomRangeAndExportToXlsx(@RequestParam String stringStartDate,
                                                                                                 @RequestParam String stringEndDate,
                                                                                                 HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            LocalDate startDate = LocalDate.parse(stringStartDate);
            LocalDate endDate = LocalDate.parse(stringEndDate);
            orderService.exportOrdersToExcel(startDate, endDate, response).write(response.getOutputStream());
            return ResponseEntity.ok(new ResponseDto(true, Response.SUCCESS, Response.NO_ERROR.getText()));

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto<>(false, ExceptionEnums.ORDERS.getText() + ExceptionConstants.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Метод для выгрузки отчета в PDF.
     * @param stringStartDate - beginning of the period that receives from frontend in as String
     * @param stringEndDate   - end of the period that receives from frontend in as String
     * @param response        - response to write back stream with PDF
     * @return - ResponseEntity
     */
    @GetMapping("/sales/exportPDF")
    @ApiOperation(value = "метод экспорта в Excel",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "orders have been sent"),
            @ApiResponse(code = 404, message = "orders haven't been found"),
            @ApiResponse(code = 500, message = "problem with writing PDF")
    })
    public ResponseEntity<ResponseDto<String>> getSalesForCustomRangeAndExportToPDF(@RequestParam String stringStartDate,
                                                                                    @RequestParam String stringEndDate,
                                                                                    HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            LocalDate startDate = LocalDate.parse(stringStartDate);
            LocalDate endDate = LocalDate.parse(stringEndDate);
            orderService.exportOrdersToPDF(startDate, endDate, response);
            return new ResponseEntity<>(new ResponseDto(true, "Repair order was generated", Response.NO_ERROR.getText()), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDto<>(false, ExceptionEnums.ORDERS.getText() + ExceptionConstants.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Метод для расчета среднего чека в списке
     * @param orderList - список {@link SalesReportDto}
     * @return {@link ResponseEntity} со значением среднего чека OptionalDouble.
     */
    @GetMapping("/sales/averageOrder")
    @ApiOperation(value = "Метод рассчитывает средний чек в списке продаж",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "AverageOrder have been received"),
            @ApiResponse(code = 200, message = "AverageOrder haven't been received")
    })
    public ResponseEntity<ResponseDto<OptionalDouble>> getAverageOrder(@RequestParam List<SalesReportDto> orderList) {
        return ResponseEntity.ok(new ResponseDto<>(true, orderService.averageOrder(orderList)));
    }
}
