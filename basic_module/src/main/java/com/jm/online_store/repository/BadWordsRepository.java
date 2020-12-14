package com.jm.online_store.repository;

import com.jm.online_store.model.BadWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BadWordsRepository extends JpaRepository<BadWords, Long> {
    boolean existsBadWordsByBadword(String wordName);

    @Query("FROM BadWords b WHERE b.isEnabled = true")
    List<BadWords> getBadWordsActive();
}
