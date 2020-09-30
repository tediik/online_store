package com.jm.online_store.service.impl;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.EvaluationRepository;
import com.jm.online_store.service.interf.EvaluationService;
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

    /**
     * Метод получения всех оценок для продукта
     * @param product продукт
     * @return List<Evaluation> список оценок
     */
    @Override
    public List<Evaluation> getAllProductEvaluation(Product product) {
        return evaluationRepository.findAllByProduct(product);
    }

    /**
     * Метод добавления оценки
     * @param evaluation оценка
     * @return оценку добавленную в базу
     */
    @Override
    public Evaluation addEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    /**
     * Метод получения оценки из базs для конкретного товара и пользователя
     * @param user текущий пользователь
     * @param product продукт
     * @return Optional<Evaluation> оценка пользователя
     */
    @Override
    public Optional<Evaluation> getEvaluation(User user, Product product) {
        return Optional.ofNullable(evaluationRepository.findByProductAndUser(
                product,
                userService.findById(user.getId()).orElseThrow(UserNotFoundException::new)
        ));
    }
}
