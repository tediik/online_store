package com.jm.online_store.model.dto;

import com.jm.online_store.enums.ReportReason;
import com.jm.online_store.model.ReportComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCommentDto {
    private Long reportId;
    private Long commentId;
    private String reportReason;
    private String reasonComment;
    private String reportedComment;

    public static ReportCommentDto entityToDto(ReportComment reportComment) {
        ReportCommentDto reportCommentDto = new ReportCommentDto();
        reportCommentDto.setReportId(reportComment.getId());
        reportCommentDto.setCommentId(reportComment.getComment().getId());
        reportCommentDto.setReportReason(reportComment.getReportReason().name());
        reportCommentDto.setReasonComment(reportComment.getReasonComment());
        reportCommentDto.setReportedComment(reportComment.getComment().getContent());
        return reportCommentDto;
    }

    public static ReportComment DtoToEntity(ReportCommentDto reportCommentDto) {
        ReportComment reportComment = new ReportComment();
        reportComment.setReportReason(ReportReason.valueOf(reportCommentDto.getReportReason()));
        reportComment.setReasonComment(reportCommentDto.getReasonComment());
        return reportComment;
    }
}
