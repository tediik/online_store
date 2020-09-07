package com.jm.online_store.service.impl;

import com.jm.online_store.model.ProductComment;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ProductCommentRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ProductCommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<ProductComment> findAll() {
        return commentRepository.findAll();
    }

    /**
     * Method checks if productComment is a new post or reply to previous comments
     * then sets a current user as author of a comment and saves to dataBase
     * @param productComment
     * @return ProductComment
     */
    @Override
    @Transactional
    public ProductComment addComment(ProductComment productComment) {
        User loggedInUser = userService.getCurrentLoggedInUser();
        if (productComment.getParentId() != null) {
            productComment.setParentComment(commentRepository.findById(productComment.getParentId()).get());
        }
        productComment.setCustomer(userService.findById(loggedInUser.getId()).get());
        return commentRepository.save(productComment);
    }

    @Override
    public ProductComment findById(Long parentId) {
        return commentRepository.findById(parentId).get();
    }
}
