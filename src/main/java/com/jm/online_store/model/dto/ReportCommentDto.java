package com.jm.online_store.model.dto;

import com.jm.online_store.enums.ReportReason;
import com.jm.online_store.model.ReportComment;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для построения жалобы для дальнейшего получения и отправки в {@Link ModeratorRestController}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "Dto для построения жалобы для дальнейшего получения и отправки в ModeratorRestController")
public class ReportCommentDto {
    private Long reportId;
    private Long commentId;
    private User reportCustomer;
    private String reportReason;
    private String reasonComment;
    private String reportedComment;

    /**
     * Получение из ReportComment -> ReportCommentDto.
     *
     * @param reportComment
     * @return ReportCommentDto
     */
    public static ReportCommentDto entityToDto(ReportComment reportComment) {
        ReportCommentDto reportCommentDto = new ReportCommentDto();
        reportCommentDto.setReportId(reportComment.getId());
        reportCommentDto.setCommentId(reportComment.getComment().getId());
        reportCommentDto.setReportCustomer(reportComment.getReportCustomer());
        reportCommentDto.setReportReason(reportComment.getReportReason().name());
        reportCommentDto.setReasonComment(reportComment.getReasonComment());
        reportCommentDto.setReportedComment(reportComment.getComment().getContent());
        return reportCommentDto;
    }

    /**
     * Получение из ReportCommentDto -> ReportComment.
     *
     * @param reportCommentDto
     * @return ReportComment
     */
    public static ReportComment DtoToEntity(ReportCommentDto reportCommentDto) {
        ReportComment reportComment = new ReportComment();
        reportComment.setReportReason(ReportReason.valueOf(reportCommentDto.getReportReason()));
        reportComment.setReasonComment(reportCommentDto.getReasonComment());
        return reportComment;
    }
}
