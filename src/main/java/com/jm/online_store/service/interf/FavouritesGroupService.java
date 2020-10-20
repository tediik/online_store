package com.jm.online_store.service.interf;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavouritesGroupService {
    List<FavouritesGroup> findAll();
    List<FavouritesGroup> findAllByUser(User user);
    void addFavouritesGroup(FavouritesGroup favouritesGroup);
    void deleteById(Long id);
    Optional<FavouritesGroup> findById(Long id);
    Optional<FavouritesGroup> findByName(String name);
    FavouritesGroup getOneFavouritesGroupByUserAndByName(User user, String name);
    void save (FavouritesGroup favouritesGroup);
}
