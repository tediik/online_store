package com.jm.online_store.service.impl;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ReviewRepository;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
public class ReviewServiceImplTest {

    @Autowired
    ReviewService reviewService;

    @MockBean
    UserService userService;

    @MockBean
    ReviewRepository reviewRepository;

    User loggedInUser;
    Review argumentReview;

    @BeforeEach
    void init() {
        loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setEmail("test@mail.ru");
        loggedInUser.setPassword("1");

        argumentReview = new Review();
        argumentReview.setContent("Some Content");
        argumentReview.setId(1L);
    }

    @Test
    void addReview() {
        given(userService.getCurrentLoggedInUser()).willReturn(loggedInUser);
        given(userService.findById(1L)).willReturn(Optional.ofNullable(loggedInUser));
        given(reviewRepository.save(argumentReview)).willReturn(argumentReview);

        Review review = reviewService.addReview(argumentReview);
        assertThat(review, notNullValue());
        assertThat(loggedInUser, notNullValue());
        verify(reviewRepository, times(1)).save(review);
        assertThat(review.getCustomer(), notNullValue());
        assertThat(review.getContent(), notNullValue());
        assertThat(review.getContent(), is(argumentReview.getContent()));
        assertThat(review.getCustomer(), is(argumentReview.getCustomer()));
    }
}