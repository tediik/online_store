package com.jm.online_store.service.interf;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CommentService {

    List<Comment> findAllByProductId(Long productId);

    List<Comment> findAllByReviewId(Long reviewId);

    List<Comment> findAllByCustomer(User user);

    Comment addComment(@RequestBody Comment comment);

    void addCommentInit(Comment comment);

    Comment findById(Long parentId);

    void removeById(Long commentId);

    void update(Comment comment);
}
