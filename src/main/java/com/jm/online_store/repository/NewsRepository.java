package com.jm.online_store.repository;

import com.jm.online_store.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAll();

    Optional<News> findById(long id);

    boolean existsById(Long id);

    @Query("select a from News a where a.postingDate <= :postingDate")
    List<News> findAllWithPostingDateTimeBefore(
            @Param("postingDate") LocalDateTime postingDate);

    @Query("select a from News a where a.postingDate >= :postingDate")
    List<News> findAllWithPostingDateTimeAfter(
            @Param("postingDate") LocalDateTime postingDate);
}