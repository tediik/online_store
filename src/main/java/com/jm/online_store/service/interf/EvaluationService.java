package com.jm.online_store.service.interf;

import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;

import java.util.List;
import java.util.Optional;

public interface EvaluationService {
    List<Evaluation> getAllProductEvaluation(Product product);
    Evaluation addEvaluation(Evaluation evaluation);
    Optional<Evaluation> getEvaluation(User user, Product product);
}
