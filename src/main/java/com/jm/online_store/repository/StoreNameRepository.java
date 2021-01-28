package com.jm.online_store.repository;

import com.jm.online_store.model.StoreName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreNameRepository extends JpaRepository<StoreName, String> {
    StoreName findById(Integer id);
    @Modifying
    @Query("update StoreName u set u.name = :name where u.id = 1")
    int setUserById(@Param("name") String name);
}
