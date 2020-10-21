package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.SentStockNotFoundException;
import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.UserService;
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
import java.util.TreeMap;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager")
public class ManagerReportsRestController {
    private final UserService userService;
    private final SentStockService sentStockService;

    /**
     * метод получения списка пользователей, подписанных на рассылку по номеру дня
     *
     * @param dayNumber день недели
     * @return список пользователей
     */
    @GetMapping("/users/{dayNumber}")
    public ResponseEntity<List<User>> allUsers(@PathVariable byte dayNumber) {
        return ResponseEntity.ok(userService.findByDayOfWeekForStockSend(dayNumber));
    }

    /**
     * метод отмены подписки со страницы менеджера
     *
     * @param id пользователя
     * @return Статус ответа зависящий от успешности отмены подписки для пользователя
     */
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Long> cancelSubscription(@PathVariable Long id) {
        userService.cancelSubscription(id);

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
