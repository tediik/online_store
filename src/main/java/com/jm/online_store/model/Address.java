package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
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