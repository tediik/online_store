package com.jm.online_store.service.interf;

import com.jm.online_store.model.Comment;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CommentService {

    List<Comment> findAll(Long productId);

    Comment addComment(@RequestBody Comment comment);

    Comment findById(Long parentId);
}
