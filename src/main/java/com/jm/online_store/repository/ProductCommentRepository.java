package com.jm.online_store.repository;

import com.jm.online_store.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepository extends JpaRepository<Comment, Long> {

    List<Comment>findAllByProductId(Long productId);


}
