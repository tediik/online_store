package com.jm.online_store.service.impl;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.CommentRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
public class CommentServiceImplTest {

    @Autowired
    CommentService commentService;

    @MockBean
    UserService userService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    UserRepository userRepository;


    User loggedInUser;
    Comment argumentComment;

    @BeforeEach
    void init() {
        loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setEmail("test@mail.ru");
        loggedInUser.setPassword("1");

        argumentComment = new Comment();
        argumentComment.setContent("Some Content");
        argumentComment.setId(1L);
    }

    @Test
    void addComment() {
        given(userService.getCurrentLoggedInUser()).willReturn(loggedInUser);
        given(userService.findById(1L)).willReturn(java.util.Optional.ofNullable(loggedInUser));
        given(commentRepository.save(argumentComment)).willReturn(argumentComment);

        Comment comment = commentService.addComment(argumentComment);
        assertThat(comment, notNullValue());
        assertThat(loggedInUser, notNullValue());
        verify(commentRepository, times(1)).save(comment);
        assertThat(comment.getCustomer(),notNullValue());
        assertThat(comment.getContent(), notNullValue());
        assertThat(comment.getContent(), is(argumentComment.getContent()));
        assertThat(comment.getCustomer(),is(argumentComment.getCustomer()));
    }
}