package com.jm.online_store.repository;

import com.jm.online_store.model.SharedStocks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedStocksRepository extends JpaRepository<SharedStocks, Long> {

}
