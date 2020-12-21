package com.jm.online_store.repository;

import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findAllByProduct(Product product);
    Evaluation findByProductAndUser(Product product, User user);
}
