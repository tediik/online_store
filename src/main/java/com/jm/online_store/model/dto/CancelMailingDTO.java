package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * DTO для передачи параметров на страницу подтверждения отмены рассылки
 */
@Data
@ApiModel(description = "DTO для передачи на страницу товара")
public class CancelMailingDTO {

    private String id;
    private String email;
    private String password;
    private String product;

    public CancelMailingDTO(String id, String email, String password, String product) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.product = product;
    }

    public CancelMailingDTO(String email) {
        this.email = email;
    }
}
