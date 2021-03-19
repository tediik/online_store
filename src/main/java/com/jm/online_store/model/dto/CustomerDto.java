package com.jm.online_store.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "DTO для сущности комментария Customer")
@Component
public class CustomerDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @JsonSerialize()
    private DayOfWeekForStockSend dayOfWeekForStockSend;

    public CustomerDto(Long id, String email, DayOfWeekForStockSend dayOfWeekForStockSend) {
        this.id = id;
        this.email = email;
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }
}
