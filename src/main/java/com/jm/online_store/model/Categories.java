package com.jm.online_store.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Categories implements Serializable {
    private static final long serialVersionUID = -3453453324L;

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String category;

    //For example, Category = Laptop (or PC), SuperCategory = Computer
    @NonNull
    private String superCategory;

    @OneToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private List< Product > products;
}
