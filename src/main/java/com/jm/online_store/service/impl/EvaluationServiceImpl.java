package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.EvaluationRepository;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final UserService userService;

    @Override
    public List<Evaluation> getAllProductEvaluation(Product product) {
        return evaluationRepository.findAllByProduct(product);
    }

    @Override
    public Evaluation addEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    @Override
    public Optional<Evaluation> getEvaluation(User user, Product product) {
        return Optional.ofNullable(evaluationRepository.findByProductAndUser(
                product,
                userService.findById(user.getId()).orElseThrow(UserNotFoundException::new)
        ));
    }
}
