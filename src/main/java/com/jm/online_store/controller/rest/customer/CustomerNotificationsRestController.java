package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.enums.ConfirmReceiveEmail;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.UserServiceException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.PriceChangeNotifications;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.PriceChangeNotificationsService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@AllArgsConstructor
@RestController
@RequestMapping("api/customer/notifications")
@Slf4j
@Api(description = "operations with notifications of customer")
public class CustomerNotificationsRestController {

    private final CustomerService customerService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PriceChangeNotificationsService priceChangeNotificationsService;
    private final CommentService commentService;
    private final ReviewService reviewService;

    /**
     * Получение дня, выбранного покупателем для рассылки (если день не выбран возращает null)
     * @return DayOfWeekForStockSend - день для рассылки
     */
    @GetMapping("/dayOfWeekForStockSend")
    @ApiOperation(value = "Метод возвращает из базы день, в который будет рассылка",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponse(code = 404, message = "Day was not found")
    public ResponseEntity<ResponseDto<DayOfWeekForStockSend>> getCustomerDayOfWeekForStockSend() {
        Customer customer = customerService.getCurrentLoggedInUser();
        DayOfWeekForStockSend day = customerService.getCustomerDayOfWeekForStockSend(customer);
        return new ResponseEntity<>(new ResponseDto<>(true, day), HttpStatus.OK);
    }

    /**
     * Установка, изменение или удаление дня для рассылки
     * @param day - (String) - день недели  или null в случае отмены подписки
     * @return DayOfWeekForStockSend новый день для рассылки или null в случае отмены подписки
     */
    @PutMapping("/dayOfWeekForStockSend")
    @ApiOperation(value = "Метод устанавливает день для рассылки, принимает String",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Wrong string data"),
            @ApiResponse(code = 200, message = "Day was changed")
    })
    public ResponseEntity<ResponseDto<DayOfWeekForStockSend>> setCustomerDayOfWeekForStockSend(@RequestBody String day) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (!day.equals("null")) {
            customer.setDayOfWeekForStockSend(DayOfWeekForStockSend.valueOf(day.toUpperCase()));
            return new ResponseEntity<>(new ResponseDto<>(true, DayOfWeekForStockSend.valueOf(day)), HttpStatus.OK);
        } else {
            customer.setDayOfWeekForStockSend(null);
            return new ResponseEntity<>(new ResponseDto<>(true, day), HttpStatus.OK);
        }
    }

    /**
     * Проверка согласия на рассылку email об изменении цен товаров и новые комментарии
     * @param type - (String) - тип рассылки (price - об изменеии цен, comments - о новых комментариях)
     * @return ConfirmReceiveEmail статус рассыки
     * @throws UserServiceException - если передана ошибочная строка
     */
    @GetMapping("/emailConfirmation/{type}")
    @ApiOperation(value = "Проверяет есть ли согласие на рассылку сообщений, принимает String ('price', 'comments')",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data received successfully"),
            @ApiResponse(code = 400, message = "Wrong string data")
    })
    public ResponseEntity<ResponseDto<ConfirmReceiveEmail>> isEmailConfirmed(@PathVariable String type) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (type.equalsIgnoreCase("price")) {
            return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer.getConfirmReceiveEmail(), ConfirmReceiveEmail.class)), HttpStatus.OK);
        } else if (type.equalsIgnoreCase("comments")){
            return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer.getConfirmCommentsEmails(), ConfirmReceiveEmail.class)), HttpStatus.OK);
        } else {
            throw new UserServiceException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND);
        }
    }

    /**
     * Отправляет запрос на рассылки для залогиненного пользоватея
     * @return CustomerDto залогиненный пользователь, которому отправлено письмо с подтвержением {@link CustomerDto}
     */
    @PutMapping("/emailConfirmation")
    @ApiOperation(value = "Запрашивает подтверждение на рассылку об изменении цен",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные получены"),
            @ApiResponse(code = 400, message = "Информация не получена")
    })
    public ResponseEntity<ResponseDto<CustomerDto>> getEmailConfirmed() {
        Customer customer = customerService.getCurrentLoggedInUser();
        userService.sendConfirmationSubscribeLetter(customer.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer, CustomerDto.class)), HttpStatus.OK);
    }

    /**
     * запрос на отмену email рассылки об изменении цен
     * @return CustomerDto пользователь, для которого отменяется подписка {@link CustomerDto}
     */
    @PutMapping("/unsubscribe/priceChangesEmails")
    @ApiOperation(value = "Прекращает рассылку сообщений об изменении цен",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email unsubscribed"),
            @ApiResponse(code = 400, message = "Email was not unsuscribed")
    })
    public ResponseEntity<ResponseDto<CustomerDto>> unsubscribeNewPrice() {
        Customer customer = customerService.getCurrentLoggedInUser();
        customer.setConfirmReceiveEmail(ConfirmReceiveEmail.NO_ACTIONS);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer, CustomerDto.class)), HttpStatus.OK);
    }

    /**
     * запрос на отмену email рассылки о новых комментариях
     * @return CustomerDto пользователь, для которого отменяется подписка {@link CustomerDto}
     */
    @PutMapping("/unsubscribe/commentsEmails")
    @ApiOperation(value = "Прекращает рассылку сообщений о новых комментариях",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email unsubscribed"),
            @ApiResponse(code = 400, message = "Email was not unsubscribed")
    })
    public ResponseEntity<ResponseDto<CustomerDto>> unsubscribeNewComments() {
        Customer customer = customerService.getCurrentLoggedInUser();
        customer.setConfirmCommentsEmails(ConfirmReceiveEmail.NO_ACTIONS);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer, CustomerDto.class)), HttpStatus.OK);
    }

    /**
     * Поиск всех уведомлений об изменении цен конкретного покупателя
     * @param id покупателя {@link Long}
     * @return List<PriceChangeNotifications> список уведомлений
     */
    @GetMapping("/priceChanges/{id}")
    @ApiOperation(value = "Запрашивает данные об изменении товаров из подписки покупателя по его id",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Prices changes found"),
            @ApiResponse(code = 400, message = "Could not get price notifications")
    })
    public ResponseEntity<ResponseDto<List<PriceChangeNotifications>>> getPriceNotifications(@PathVariable Long id) {
        List<PriceChangeNotifications> list = priceChangeNotificationsService.getCustomerPriceChangeNotifications(id);
        return new ResponseEntity<>(new ResponseDto<>(true, list), HttpStatus.OK);
    }

    /**
     * Поиск ответов на комментарии и отзывы
     * @return <List<Comment>> список комментариев-ответов {@link Comment}
     */
    @GetMapping("/commentAnswers")
    @ApiOperation(value = "Запрашивает ответы на комментарии",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Comments were found"),
            @ApiResponse(code = 400, message = "Comments were not found")
    })
    public ResponseEntity<ResponseDto<List<Comment>>> getCommentAnswers() {
        List<Comment> customerComments = commentService.findAllByCustomer(customerService.getCurrentLoggedInUser());
        List<Review> customerReviews = reviewService.findAllByCustomer(customerService.getCurrentLoggedInUser());
        List<Comment> answers = new ArrayList<>();
        customerComments.stream()
                .forEach(comment -> answers.addAll(commentService.getCommentsByParentId(comment.getId())));
        customerReviews.stream()
                .forEach(review -> answers.addAll(review.getComments()));
        return new ResponseEntity<>(new ResponseDto<>(true, answers), HttpStatus.OK);
    }
}
