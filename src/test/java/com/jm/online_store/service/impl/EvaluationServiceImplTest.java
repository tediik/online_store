package com.jm.online_store.service.impl;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.EvaluationRepository;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvaluationServiceImplTest {
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    private User user;
    private Evaluation evaluation1;
    private Evaluation evaluation2;
    private Product product;
    private List<Evaluation> evaluations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User("email","1");
        evaluations = new ArrayList<>();
        product = new Product("product",234D,2);
        evaluation1 = new Evaluation(5,user,product);
        evaluation2 = new Evaluation(4,user,null);
        evaluations.add(evaluation1);
        evaluations.add(evaluation2);
    }
    @AfterEach
    void tearDown() {
        user = null;
        product = null;
        evaluations = null;
        evaluation1 = null;
        evaluation2 = null;
    }

    @Test
    public void getEvaluationTest() {
        when(evaluationRepository.findByProductAndUser(product,user)).thenReturn(evaluation1);
        when(userService.findById(any())).thenReturn(Optional.of(user));
        assertEquals(evaluationService.getEvaluation(user,product),Optional.of(evaluation1));
        verify(userService,times(1)).findById(any());
    }

    @Test
    public void getEvaluationThrowExceptionTest() {
        when(userService.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> evaluationService.getEvaluation(user,product));
    }
}
