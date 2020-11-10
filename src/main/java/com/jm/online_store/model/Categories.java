package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Categories {//очень хочется убрать множественное число

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String category;
    @NonNull
    private Long parentCategoryId;
    @NonNull
    private int depth;

//    @OneToMany(mappedBy = "categories", cascade = CascadeType.ALL)
//    private List<Product> products;



    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // ALL -??? EAGER - ?
    @JoinColumn(name = "category_id")
    private List<Product> products;
}