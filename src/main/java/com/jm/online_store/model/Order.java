package com.jm.online_store.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private Long amount;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime dateTime;

    // либо вот так - отсылка только к id продукта
    // либо делать связь ManyToMany с Product
    @ElementCollection
    private Set<Long> productsIds;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    // Список статусов заказа
    public enum Status {
        COMPLETED, CANCELED, INCARTS;
    }
}
