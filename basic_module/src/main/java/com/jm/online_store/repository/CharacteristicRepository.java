package com.jm.online_store.repository;

import com.jm.online_store.model.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacteristicRepository extends JpaRepository<Characteristic, Long> {

    List<Characteristic> findAll();
}
