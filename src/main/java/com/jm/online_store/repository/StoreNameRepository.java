package com.jm.online_store.repository;

import com.jm.online_store.model.StoreName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreNameRepository extends JpaRepository<StoreName, String> {
    StoreName findById(Integer id);
}
