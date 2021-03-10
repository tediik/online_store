package com.jm.online_store.model;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import io.swagger.annotations.ApiModel;
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
@ApiModel(description =  "Сущность Customer расширяющая User")
public class Customer extends User {

    /**
     * Отсчет времени удаления клиента(+30 дней).
     * По уолчанию значение null.Когда клиент , в своем кабинете нажимает кнопку
     * "удалить профиль " мы присваем этому полю текущую дату .
     */
    @Column(name = "anchorForDelete")
    private LocalDateTime anchorForDelete;

    /**
     * День недели для получения рассылок.
     */
    @Column(name = "day_of_week_for_stock_send")
    @Enumerated(EnumType.STRING)
    private DayOfWeekForStockSend dayOfWeekForStockSend;

    public Customer(Long id, String email ) {
        super(id, email);
    }

    public Customer(String email, String password) {
        super(email, password);
    }

    public Customer(@Email @NotBlank String email, DayOfWeekForStockSend dayOfWeekForStockSend, String password) {
        super(email, password);
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }

    /**
     * Конструктор для поиска подписчиков из CustomerRepository (метод findSubscriberByEmail())
     *
     * @param id                    - поле id
     * @param email                 - поле email
     * @param dayOfWeekForStockSend - поле dayOfWeekForStockSend
     */
    public Customer(Long id, String email, DayOfWeekForStockSend dayOfWeekForStockSend) {
        super(id, email);
        this.dayOfWeekForStockSend = dayOfWeekForStockSend;
    }
}
