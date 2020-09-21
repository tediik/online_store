package com.jm.online_store.service.impl;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.CommentRepository;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
public class CommentServiceImplTest {

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    UserService userService;

    @Test
    void addComment() {

        Comment comment = new Comment();
        comment.setContent("Some Content");
        comment.setCustomer(new User("some user","1"));
        assertTrue(commentRepository.save(comment));
    }
}
