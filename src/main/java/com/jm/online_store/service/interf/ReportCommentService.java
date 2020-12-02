package com.jm.online_store.service.interf;

import com.jm.online_store.model.ReportComment;

import java.util.List;

public interface ReportCommentService {
    List<ReportComment> findAllReportComments();

    void deleteReport(Long id);

    void deleteReportAndComment(Long id);

    void addReportComment(ReportComment reportComment);
}
