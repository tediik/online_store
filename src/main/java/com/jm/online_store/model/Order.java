package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private Double orderPrice;

    @NonNull
    private LocalDateTime dateTime;

    @NonNull
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    //TODO @JsonManagedReference пока не удаляю, возможно придется менять обратно
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @JsonBackReference //пока не удаляю, возможно придется менять обратно
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<ProductInOrder> productInOrders;

//    @ManyToOne (cascade=CascadeType.ALL)
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
//            property = "id")
//    private Address address;

    // Список статусов заказа
    public enum Status {
        COMPLETED, CANCELED, INCARTS;
    }

    public Order(@NonNull LocalDateTime dateTime, @NonNull Status status) {
        this.dateTime = dateTime;
        this.status = status;
    }
}