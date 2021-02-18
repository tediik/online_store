package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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

    @NotBlank
    private String region;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @NotBlank
    private String building;

    private String district;

    private String zip;

    @Column(name = "shop")
    private boolean shop;

    public Address(@NotBlank String region, @NotBlank String city, @NotBlank String street, @NotBlank String building, String zip, boolean shop) {
        this.region = region;
        this.city = city;
        this.street = street;
        this.building = building;
        this.zip = zip;
        this.shop = shop;
    }
}