package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;
    @NonNull
    @Column(name = "product", nullable = false)
    private String product;
    @NonNull
    @Column(name = "price", nullable = false)
    private Double price;
    @NonNull
    private int amount;
    @NonNull
    private Double rating;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Description descriptions;
    @NonNull
    private String productType;
}
