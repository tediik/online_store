package com.jm.online_store.repository;

import com.jm.online_store.model.SharedStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedStockRepository extends JpaRepository<SharedStock, Long> {

}
