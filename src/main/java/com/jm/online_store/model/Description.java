package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "description")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Description {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Description(String serialNumber, String producer, int warranty, String dimensions, String color,
                       Double weight, String information, int ram, int hdd, String screenResolution, boolean wifiOnBoard, String bluetoothVersion) {
        this.serialNumber = serialNumber;
        this.producer = producer;
        this.warranty = warranty;
        this.dimensions = dimensions;
        this.color = color;
        this.weight = weight;
        this.information = information;
        this.ram = ram;
        this.hdd = hdd;
        this.screenResolution = screenResolution;
        this.wifiOnBoard = wifiOnBoard;
        this.bluetoothVersion = bluetoothVersion;
    }
}
