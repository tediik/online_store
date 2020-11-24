package com.jm.online_store.service.impl;

import com.jm.online_store.model.ReportComment;
import com.jm.online_store.repository.CommentRepository;
import com.jm.online_store.repository.ReportCommentRepository;
import com.jm.online_store.service.interf.ReportCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReportCommentServiceImpl implements ReportCommentService {
    private final ReportCommentRepository reportCommentRepository;
    private final CommentRepository commentRepository;

    /**
     * Добавление жалобы.
     *
     * @param reportComment жалоба на комментарий.
     */
    @Override
    public void addReportComment(ReportComment reportComment) {
        reportCommentRepository.save(reportComment);
    }

    /**
     * Получения всех жалоб.
     *
     * @return List<ReportComment>
     */
    @Override
    public List<ReportComment> findAllReportComments() {
        return reportCommentRepository.findAll();
    }

    /**
     * Удаления жалобы.
     *
     * @param id жалобы.
     */
    @Override
    public void deleteReport(Long id) {
        reportCommentRepository.deleteById(id);
    }

    /**
     * Удаления всех жалоб на комментарий, удаление всех ответов на комментарий и удаления комментария.
     *
     * @param id комментария.
     */
    @Override
    public void deleteReportAndComment(Long id) {
        reportCommentRepository.findAllByCommentId(id)
                .forEach(reportComment -> reportCommentRepository.deleteById(reportComment.getId()));
        commentRepository.findAllByParentId(id)
                .forEach(commentRepository::delete);
        commentRepository.deleteById(id);
    }
}
