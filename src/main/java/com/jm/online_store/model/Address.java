package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность адрес, связана с сущностью {@link Order}
 * и сущностью {@link User}
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "address")
@ApiModel(description =  "Сущность Address, связана с сущностью Order и User")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String zip;

    @NonNull
    private String region;

    private String district;

    @NonNull
    private String city;

    @NonNull
    private String street;

    @NonNull
    private String building;

    private String flat;

    @Column(name = "shop")
    private boolean shop;

    public Address(@NonNull String zip, @NonNull String region, @NonNull String city, @NonNull String street, @NonNull String building, boolean shop) {
        this.zip = zip;
        this.region = region;
        this.city = city;
        this.street = street;
        this.building = building;
        this.shop = shop;
    }
}