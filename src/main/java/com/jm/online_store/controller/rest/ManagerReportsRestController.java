package com.jm.online_store.controller.rest;

import com.jm.online_store.model.News;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager", method = RequestMethod.GET)
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
        List<User> allUsers = userService.findByDayOfWeekForStockSend(dayNumber);
        return ResponseEntity.ok().body(allUsers);
    }

    /**
     * метод отмены подписки со страницы менеджера
     *
     * @param id полльзователя
     * @return
     */
    @PostMapping("/cancel/{id}")
    public ResponseEntity<User> allUsers(@PathVariable Long id) {
        userService.cancelSubscription(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<Map<LocalDate,Long>> allSentStocks(
            @RequestParam(value="param1", required=false) String param1,
            @RequestParam(value="param2", required=true) String param2) {
        LocalDate begin = LocalDate.parse(param1);
        LocalDate end = LocalDate.parse(param2);
        Map<LocalDate,Long> sentStocks = sentStockService.getSentStocksMap(begin,end);
        Map<LocalDate,Long> sentStocksSorted = new TreeMap<LocalDate, Long>(sentStocks);
        return ResponseEntity.ok().body(sentStocksSorted);
    }
}