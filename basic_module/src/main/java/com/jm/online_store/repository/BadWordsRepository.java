package com.jm.online_store.repository;

import com.jm.online_store.enums.BadWordStatus;
import com.jm.online_store.model.BadWords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadWordsRepository extends JpaRepository<BadWords, Long> {
    boolean existsBadWordsByBadword(String wordName);

    List<BadWords> findAllByStatusEquals(BadWordStatus status);
}
