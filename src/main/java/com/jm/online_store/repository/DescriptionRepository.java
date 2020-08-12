package com.jm.online_store.repository;

import com.jm.online_store.model.Description;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DescriptionRepository extends JpaRepository< Description, Long>, CrudRepository< Description, Long> {
}
