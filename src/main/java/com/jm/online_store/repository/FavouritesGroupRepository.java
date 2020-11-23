package com.jm.online_store.repository;

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

    @Query("from FavouritesGroup f where f.user = :user")
    List<FavouritesGroup> findAllByUser(@Param("user") User user);

    @Query("from FavouritesGroup f where f.user = :user and f.name = :name")
    FavouritesGroup getOneFavouritesGroupByUserAndByName(@Param("user") User user, @Param("name") String name);

    @Query("from FavouritesGroup f where f.user = :user and f.id = :id")
    FavouritesGroup getOneFavouritesGroupByUserAndById(@Param("user") User user, @Param("id") Long id);

}
