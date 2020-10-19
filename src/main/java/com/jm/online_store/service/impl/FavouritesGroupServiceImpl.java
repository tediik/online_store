package com.jm.online_store.service.impl;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.FavouritesGroupRepository;
import com.jm.online_store.service.interf.FavouritesGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavouritesGroupServiceImpl implements FavouritesGroupService {
    private final FavouritesGroupRepository favouritesGroupRepository;

    @Override
    public List<FavouritesGroup> findAll() {
        return favouritesGroupRepository.findAll();
    }

    @Override
    public List<FavouritesGroup> findAllByUser(User user) {
        return favouritesGroupRepository.findAllByUser(user);
    }

    @Override
    public void addFavouritesGroup(FavouritesGroup favouritesGroup) {
        favouritesGroupRepository.save(favouritesGroup);
    }

    @Override
    public void deleteById(Long id) {
        favouritesGroupRepository.deleteById(id);
    }

    @Override
    public Optional<FavouritesGroup> findById(Long id) {
        return favouritesGroupRepository.findById(id);
    }

    @Override
    public Optional<FavouritesGroup> findByName(String name) {
        return favouritesGroupRepository.findByName(name);
    }

    @Override
    public FavouritesGroup getOneFavouritesGroupByUserAndByName(User user, String name) {
        return favouritesGroupRepository.getOneFavouritesGroupByUserAndByName(user, name);
    }
}
