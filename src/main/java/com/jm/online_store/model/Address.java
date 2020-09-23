package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "zip")
    private String zip;

    @NonNull
    @Column(name = "region")
    private String region;

    @Column(name = "district")
    private String district;

    @NonNull
    @Column(name = "city")
    private String city;

    @NonNull
    @Column(name = "street")
    private String street;

    @NonNull
    @Column(name = "building")
    private String building;

    @Column(name = "flat")
    private String flat;

    @Column(name = "shop")
    private boolean shop;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Set<Order> orders;

    public Address(@NonNull String zip, @NonNull String region, @NonNull String city, @NonNull String street, @NonNull String building, boolean shop) {
        this.zip = zip;
        this.region = region;
        this.city = city;
        this.street = street;
        this.building = building;
        this.shop = shop;
    }
}