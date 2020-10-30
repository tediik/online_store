package com.jm.online_store.repository;

import com.jm.online_store.model.SharedNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedNewsRepository extends JpaRepository<SharedNews, Long> {
}
