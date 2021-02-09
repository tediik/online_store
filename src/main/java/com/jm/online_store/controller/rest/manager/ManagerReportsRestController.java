package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.SentStockNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.SentStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
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
    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * метод получения списка пользователей, подписанных на рассылку по номеру дня
     *
     * @param dayOfWeek день недели
     * @return список пользователей
     */
    @GetMapping("/users/{dayOfWeek}")
    @ApiOperation(value = "Get list of users subscribing on the report by day number",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<List<CustomerDto>>> allUsersByDayOfWeek(@PathVariable String dayOfWeek) {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        List<CustomerDto> returnValue = modelMapper.map(customerService.findByDayOfWeekForStockSend(dayOfWeek), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод поиска пользователя, подписанного на рассылку по email "на лету"
     *
     * @param email почта подписчика
     * @return список пользователей
     */
    @GetMapping("/user/{email}")
    @ApiOperation(value = "Find user subscribing on the report by email",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<List<CustomerDto>>> findSubscriberByEmail(@PathVariable String email) {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        List<CustomerDto> returnValue = modelMapper.map(customerService.findSubscriberByEmail(email), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод отмены подписки со страницы менеджера
     *
     * @param id пользователя
     * @return Статус ответа зависящий от успешности отмены подписки для пользователя
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "Method for cancel subscribe  from manager page",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<Long>> cancelSubscription(@PathVariable Long id) { //ВОПРОС: Логично ли возвращать Long ?
        customerService.cancelSubscription(id);
        return ResponseEntity.ok(new ResponseDto<>(true, id));
    }

    /**
     * метод поиска отправленных акций в интервале дат
     *
     * @param beginDate дата, от которой будет осуществляться поиск
     * @param endDate   дата, до которой будет осуществляться поиск
     * @return Словарь, где ключом является объект LocalDate, а значением его частота
     */
    @GetMapping("/report")
    @ApiOperation(value = "Method for searching for sent stocks in day interval",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<Map<LocalDate, Long>>> allSentStocks(
            @RequestParam(value = "beginDate", required = false) String beginDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        LocalDate begin = LocalDate.parse(beginDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(new ResponseDto<>(true, sentStockService.getSentStocksMap(begin, end)));
    }

}
