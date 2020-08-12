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
public class Description implements Serializable {
    private static final long serialVersionUID = -1123556464L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue
    private Long id;
    @NonNull
    private String serialNumber;
    @NonNull
    private int ram;
    @NonNull
    private int hdd;
    @NonNull
    private Double weight;
    @NonNull
    private String information;

}
