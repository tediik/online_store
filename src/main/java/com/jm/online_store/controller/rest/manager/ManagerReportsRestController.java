package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.SentStockNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.SentStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager")
@Api(description = "Rest controller for manage subscribes from manager page")
public class ManagerReportsRestController {
    private final CustomerService customerService;
    private final SentStockService sentStockService;

    /**
     * метод получения списка пользователей, подписанных на рассылку по номеру дня
     *
     * @param dayOfWeek день недели
     * @return список пользователей
     */
    @GetMapping("/users/{dayOfWeek}")
    @ApiOperation(value = "Get list of users subscribing on the report by day number")
    public ResponseEntity<List<Customer>> allUsersByDayOfWeek(@PathVariable String dayOfWeek) {
        return ResponseEntity.ok(customerService.findByDayOfWeekForStockSend(dayOfWeek));
    }

    /**
     * метод поиска пользователя, подписанного на рассылку по email "на лету"
     *
     * @param email почта подписчика
     * @return список пользователей
     */
    @GetMapping("/user/{email}")
    @ApiOperation(value = "Find user subscribing on the report by email")
    public ResponseEntity<List<Customer>> findSubscriberByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.findSubscriberByEmail(email));
    }

    /**
     * метод отмены подписки со страницы менеджера
     *
     * @param id пользователя
     * @return Статус ответа зависящий от успешности отмены подписки для пользователя
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "Method for cancel subscribe  from manager page")
    public ResponseEntity<Long> cancelSubscription(@PathVariable Long id) {
        customerService.cancelSubscription(id);
        return ResponseEntity.ok().build();
    }

    /**
     * метод поиска отправленных акций в интервале дат
     *
     * @param beginDate дата, от которой будет осуществляться поиск
     * @param endDate   дата, до которой будет осуществляться поиск
     * @return Словарь, где ключом является объект LocalDate, а значением его частота
     */
    @GetMapping("/report")
    @ApiOperation(value = "Method for searching for sent stocks in day interval")
    public ResponseEntity<Map<LocalDate, Long>> allSentStocks(
            @RequestParam(value = "beginDate", required = false) String beginDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        LocalDate begin = LocalDate.parse(beginDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(sentStockService.getSentStocksMap(begin, end));
    }

    @ExceptionHandler({SentStockNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity handlerControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
