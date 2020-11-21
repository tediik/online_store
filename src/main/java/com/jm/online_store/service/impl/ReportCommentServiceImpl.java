package com.jm.online_store.service.impl;

import com.jm.online_store.model.Comment;
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

    @Override
    public void addReportComment(ReportComment reportComment) {
        reportCommentRepository.save(reportComment);
    }

    @Override
    public List<ReportComment> findAllReportComments() {
        return reportCommentRepository.findAll();
    }

    @Override
    public void deleteReport(Long id) {
        reportCommentRepository.deleteById(id);
    }

    @Override
    public void deleteReportAndComment(Long id) {
        reportCommentRepository.findAllByCommentId(id)
                .forEach(reportComment -> reportCommentRepository.deleteById(reportComment.getId()));
        commentRepository.findAllByParentId(id)
                .forEach(commentRepository::delete);
        commentRepository.deleteById(id);
    }
}
