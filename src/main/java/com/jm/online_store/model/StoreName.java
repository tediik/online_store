package com.jm.online_store.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class StoreName {

    @Id
    private Integer id = 1;

    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
