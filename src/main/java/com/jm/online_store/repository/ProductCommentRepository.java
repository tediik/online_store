package com.jm.online_store.repository;

import com.jm.online_store.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {

    List<ProductComment>findAllByProductId(Long productId);


}
