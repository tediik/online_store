package com.jm.online_store.model.dto;

import com.jm.online_store.model.ProductInOrder;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * модель DTO для построения заказа по идентификатору.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description =  "Модель DTO для построения заказа по ижентификатору")
public class OrderDTO {
    private Long id;
    private Long amount;
    private Double orderPrice;
    private LocalDateTime dateTime;
    private List<ProductInOrder> productInOrders;
}
