package com.jm.online_store.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Сущность расширяющая {@link User}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer extends User {

    /**
     * Статус удаления клиента.
     */
    @Column(name = "status")
    private LocalDateTime status;

    /**
     * День недели для получения рассылок.
     */
    @Column(name = "day_of_week_for_stock_send")
    @Enumerated(EnumType.STRING)
    private DayOfWeekForStockSend dayOfWeekForStockSend;

    public Customer(String email, String password) {
        super(email, password);
    }

    public Customer(@Email @NotBlank String email, DayOfWeekForStockSend dayOfWeekForStockSend, String password) {
        super(email, password);
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }

    /**
     * Дни недели.
     */
    public enum DayOfWeekForStockSend {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
}
