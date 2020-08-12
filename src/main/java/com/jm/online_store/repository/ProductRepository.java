package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//public interface ProductRepository <T, Long> extends JpaRepository< T , Long > {
public interface ProductRepository extends JpaRepository< Product, Long>, CrudRepository< Product, Long>, PagingAndSortingRepository< Product, Long> {

}
