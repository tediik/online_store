package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Рест контроллер для заказов.
 */
@AllArgsConstructor
@RequestMapping("/api/customer")
@RestController
@Api(description = "Rest controller for orders")
public class OrderRestController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<NewsDto>>() {}.getType();

    /**
     * метод получения списка всех заказов авторизованного пользователя или пустой список.
     * @return List<OrderDto> список заказов.
     */
    @GetMapping("/getAllOrders")
    @ApiOperation(value = "Get list of all orders",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders have been found"),
            @ApiResponse(code = 200, message = "Orders have not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<OrderDTO>>> getAllOrders() {
        Customer autorityCustomer = customerService.getCurrentLoggedInCustomer();
        List<OrderDTO> returnValue = modelMapper.map(autorityCustomer.getOrders(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод получения списка всех действительных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getIncartsOrders")
    @ApiOperation(value = "Get list of all incarts orders",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders have been found"),
            @ApiResponse(code = 200, message = "Orders have not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<OrderDTO>>> getIncartsOrders() {
        Customer autorityCustomer = customerService.getCurrentLoggedInCustomer();
        List<OrderDTO> returnValue = modelMapper.map(orderService.findAllByCustomerIdAndStatus(autorityCustomer.getId(),
                Order.Status.INCARTS), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод получения списка всех завершенных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getCompletedOrders")
    @ApiOperation(value = "Get list of all complete orders",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders have been found"),
            @ApiResponse(code = 200, message = "Orders have not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<OrderDTO>>> getCompletedOrders() {
        Customer autorityCustomer = customerService.getCurrentLoggedInCustomer();
        List<OrderDTO> returnValue = modelMapper.map(orderService.findAllByCustomerIdAndStatus(autorityCustomer.getId(),
                Order.Status.COMPLETED), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод получения списка всех отмененных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getCanceledOrders")
    @ApiOperation(value = "Get list of all cancelled orders",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders have been found"),
            @ApiResponse(code = 200, message = "Orders have not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<OrderDTO>>> getCanceledOrders() {
        Customer autorityCustomer = customerService.getCurrentLoggedInCustomer();
        List<OrderDTO> returnValue = modelMapper.map(orderService.findAllByCustomerIdAndStatus(autorityCustomer.getId(),
                Order.Status.CANCELED), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * метод получения данных по заказу по идентификатору.
     *
     * @param id идентификатор заказа
     * @return данные заказа по DTO
     */
    @GetMapping("/getOrderById")
    @ApiOperation(value = "Get data of order by order ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "Order has been found"),
            @ApiResponse(code = 404, message = "Order hasn't been found")
    })
    public ResponseEntity<ResponseDto<OrderDTO>> getOrderById(@RequestBody Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true, orderService.findOrderDTOById(id)));
    }
}
