package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    private DayOfWeekForStockSend dayOfWeekForStockSend;

    public CustomerDto(Long id, String email, DayOfWeekForStockSend dayOfWeekForStockSend) {
        this.id = id;
        this.email = email;
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }
}
