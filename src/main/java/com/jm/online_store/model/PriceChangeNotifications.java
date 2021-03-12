package com.jm.online_store.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "price_change_notifications")
@ApiModel(description = "Сущность уведомление пользователя")
public class PriceChangeNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "change_date")
    @CreationTimestamp
    private LocalDateTime changeDate;

    @NonNull
    @Column(name = "old_price")
    private Double oldPrice;

    @NonNull
    @Column(name = "new_price")
    private Double newPrice;

    @NonNull
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    public PriceChangeNotifications(@NonNull Double oldPrice,
                                    @NonNull Double newPrice,
                                    @NonNull Long productId,
                                    @NotNull Long customerId,
                                    @NotNull LocalDateTime changeDate) {
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.productId = productId;
        this.customerId = customerId;
        this.changeDate = changeDate;
    }
}
