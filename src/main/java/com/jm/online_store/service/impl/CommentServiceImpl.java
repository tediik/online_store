package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CommentNotSavedException;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.AnswerNotifications;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.CommentRepository;
import com.jm.online_store.repository.ReviewRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.AnswerNotificationsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final CommonSettingsService commonSettingsService;
    private final MailSenderService mailSenderService;
    private final ProductService productService;
    private final AnswerNotificationsService answerNotificationsService;

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    /**
     * Fetches an arrayList of all Comments by productId
     *
     * @param productId
     * @return List<comment>
     */
    @Override
    public List<Comment> findAllByProductId(Long productId) {
        return commentRepository.findAllByProductId(productId);
    }

    @Override
    public List<Comment> findAllByReviewId(Long reviewId) {
        return commentRepository.findAllByReviewId(reviewId);
    }


    /**
     * Find and return List of comments from database by Customer Id
     *
     * @return List<comment>
     */
    @Override
    public List<Comment> findAllByCustomer(User user) {
        return commentRepository.findCommentsByCustomer(user);
    }

    /**
     * Method checks if Comment is a new post or reply  to previous comment or comment for review
     * then sets a current user as author of a comment, saves to dataBase
     * and send comment to method for sending email to customer
     * and send comment to method for making notification
     *
     * @param comment
     * @return Comment
     */
    @Override
    @Transactional
    public Comment addComment(Comment comment) {
        User loggedInUser = userService.getCurrentLoggedInUser();
        if (loggedInUser.isAccountNonReadOnlyStatus()) {
            if (comment.getParentId() != null) {
                comment.setParentComment(commentRepository.findById(comment.getParentId()).get());
                answerNotificationsService.addNotification(comment);
            }
            if (comment.getReview() != null) {
                comment.setReview(reviewRepository.findById(comment.getReview().getId()).get());
                answerNotificationsService.addNotification(comment);
            }
            comment.setCustomer(userService.findById(loggedInUser.getId()).get());
            sendCommentAnswer(comment);
            return commentRepository.save(comment);
        } else {
            throw new CommentNotSavedException();
        }
    }

    /**
     * Find and retrieve ProductComment from database by comment Id
     *
     * @return ProductComment
     */
    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).get();
    }


    /**
     * Delete ProductComment from database by comment Id
     * @param commentId
     */
    @Override
    public void removeById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * Обновляет комментарий. Из измененного коммента с фронта достается контент и присваивается
     * этому же комментарию в БД
     * @param commentEdited
     */
    @Override
    public Comment update(Comment commentEdited) {
        Comment commentToUpdate = findById(commentEdited.getId());
        commentToUpdate.setContent(commentEdited.getContent());
        commentToUpdate.setDeletedHasKids(commentEdited.isDeletedHasKids());
        return commentRepository.save(commentToUpdate);
    }

    /**
     * For init comments only
     *
     * @param comment
     */
    @Override
    @Transactional
    public void addCommentInit(Comment comment) {
        commentRepository.save(comment);
    }

    /**
     * Отправляет покупателю email с информацией о новом ответе на комментарий или отзыв,
     * если есть согласие на рассылку
     * @param comment новый ответ на комментарий/отзыв
     */
    @Override
    public void sendCommentAnswer(Comment comment) {
        User user;
        String message;
        String template = commonSettingsService
                .getSettingByName("new_comment_answer_template")
                .getTextValue();

        if(comment.getParentId() != null) {
            user = comment.getParentComment().getCustomer();
            message = template.replaceAll("@@parentType@@", "комментарий");
        } else if(comment.getReview() != null) {
            user = comment.getReview().getCustomer();
            message = template.replaceAll("@@parentType@@", "отзыв");
        } else {
            return;
        }
        String email = user.getEmail();
        Product product = productService.getProductById(comment.getProductId());
        if (user.getConfirmCommentsEmails().toString().equals("CONFIRMED")) {
            if(user.getFirstName() != null) {
                message = message.replaceAll("@@user@@", user.getFirstName());
            } else {
                message = message.replaceAll("@@user@@", "Покупатель");
            }
            message = message.replaceAll("@@product@@", product.getProduct());
            try {
                mailSenderService.sendHtmlMessage(email, "У Вас новый ответ!", message, "New answer to comment");
            } catch (MessagingException e) {
                log.debug("Can not send mail about new answer to comment {} to {}", comment.getId(), email);
            }
        }
    }

    /**
     * Поик комментариев (ответов) по id родителя
     * @param parentId - id родительского комментария
     * @return List<Comment> список ответных комментариев
     */
    @Override
    @Transactional
    public List<Comment> getCommentsByParentId(Long parentId) {
        return commentRepository.findAllByParentId(parentId);
    }

}

