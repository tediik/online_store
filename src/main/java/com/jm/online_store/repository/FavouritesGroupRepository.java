package com.jm.online_store.repository;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavouritesGroupRepository extends JpaRepository<FavouritesGroup, Long> {

    @Query("from FavouritesGroup fg where fg.name = :name")
    Optional<FavouritesGroup> findByName(@Param("name") String name);

    @Query("from FavouritesGroup f where f.customer = :customer")
    List<FavouritesGroup> findAllByUser(@Param("customer") User user);

    @Query("from FavouritesGroup f where f.customer = :customer and f.name = :name")
    FavouritesGroup getOneFavouritesGroupByUserAndByName(@Param("customer") Customer customer, @Param("name") String name);

    @Query("from FavouritesGroup f where f.customer = :customer and f.id = :id")
    FavouritesGroup getOneFavouritesGroupByUserAndById(@Param("customer") Customer customer, @Param("id") Long id);

}
