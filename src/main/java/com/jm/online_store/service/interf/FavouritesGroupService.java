package com.jm.online_store.service.interf;

import com.jm.online_store.model.FavouritesGroup;

import java.util.List;
import java.util.Optional;

public interface FavouritesGroupService {
    List<FavouritesGroup> findAll();
    void addFavouritesGroup(FavouritesGroup favouritesGroup);
    Optional<FavouritesGroup> findByName(String name);
}
