package com.jm.online_store.repository;

import com.jm.online_store.model.SentStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SentStockRepository extends JpaRepository<SentStock, Long> {
    List<SentStock> findAllBySentDateAfterAndSentDateBefore(LocalDate sentDateAfter, LocalDate sentDateBefore);
}
