package com.jm.online_store.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "description")
@Data
@NoArgsConstructor
public class Description {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue
    private Long id;
    @NonNull
    private String serialNumber;
    @NonNull
    private String producer;
    @NonNull
    private int warranty;
    @NonNull
    private String dimensions;
    @NonNull
    private String color;
    @NonNull
    private Double weight;
    //additional info about product
    @NonNull
    private String information;
    //Optional fields
    private int ram;
    private int hdd;
    private String screenResolution;
    private boolean wifiOnBoard;
    private String bluetoothVersion;
}
