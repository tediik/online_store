package com.jm.online_store.service.impl;

import com.jm.online_store.repository.ProductCommentRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.UserService;
import lombok.extern.slf4j.Slf4j;

import static org.mockito.Mockito.mock;


@Slf4j
public class CommentServiceTest {
    private final ProductCommentRepository commentRepository = mock(ProductCommentRepository.class);
    private final UserService userService = mock(UserServiceImpl.class);
    private final CommentService commentService = new CommentServiceImpl(commentRepository,userService);

}
