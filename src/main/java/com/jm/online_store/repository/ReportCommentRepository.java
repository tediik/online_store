package com.jm.online_store.repository;

import com.jm.online_store.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    List<ReportComment> findAllByCommentId(Long id);
}
