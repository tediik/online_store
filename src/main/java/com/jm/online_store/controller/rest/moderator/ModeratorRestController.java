package com.jm.online_store.controller.rest.moderator;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.model.ReportComment;
import com.jm.online_store.model.dto.ReportCommentDto;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.ModeratorsStatisticService;
import com.jm.online_store.service.interf.ReportCommentService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Рест контроллер для модератора.
 */
@RestController
@Slf4j
@RequestMapping("/api/moderator")
@AllArgsConstructor
@Api(description = "Rest controller for moderator")
public class ModeratorRestController {
    private final CommentService commentService;
    private final UserService userService;
    private final ReportCommentService reportCommentService;
    private final ModeratorsStatisticService moderatorsStatisticService;

    /**
     * Получения списка всех жалоб на комментарии с помощью WebSocket.
     * @return ReportCommentDto
     */
    @MessageMapping("/report")
    @SendTo("/table/report")
    @ApiOperation(value = "Get list of all reports on comments with WebSocket",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Reports were not found"),
            @ApiResponse(code = 200, message = "Reports found")
    })
    public List<ReportCommentDto> allReportComments() {
        return reportCommentService.findAllReportComments().stream()
                .map(ReportCommentDto::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавление жалобы на комментарий.
     * @param reportCommentDto
     */
    @PostMapping
    @ApiOperation(value = "Add a new report on comment",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Report hasn't been added"),
            @ApiResponse(code = 200, message = "Report has been added")
    })
    public ResponseEntity<ReportCommentDto> addReportComment(@RequestBody ReportCommentDto reportCommentDto) {
        ReportComment reportComment = ReportCommentDto.DtoToEntity(reportCommentDto);
        reportComment.setComment(commentService.findById(reportCommentDto.getCommentId()));
        reportComment.setReportCustomerEmail(userService.getCurrentLoggedInUser().getEmail());
        reportCommentService.addReportComment(reportComment);
        return ResponseEntity.ok(reportCommentDto);
    }

    /**
     * Удаления жалобы.
     * @param id жалобы.
     */
    @DeleteMapping("/leave/{id}")
    @ApiOperation(value = "Delete report on comment by report ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Report hasn't been deleted"),
            @ApiResponse(code = 200, message = "Report has been deleted"),
            @ApiResponse(code = 204, message = "There is no report with such id")
    })
    public ResponseEntity<ReportComment> deleteReport(@PathVariable("id") Long id) {
        moderatorsStatisticService.incrementDismissedCount(userService.getCurrentLoggedInUser());
        reportCommentService.deleteReport(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление жалобы и коментария.
     * @param id комментария.
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Delete report and comment  by comment ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Comment and report haven't been deleted"),
            @ApiResponse(code = 200, message = "Comment and report have been deleted"),
            @ApiResponse(code = 204, message = "There is no comment with such id")
    })
    public ResponseEntity<ReportComment> deleteReportAndComment(@PathVariable("id") Long id) {
        moderatorsStatisticService.incrementApprovedCount(userService.getCurrentLoggedInUser());
        reportCommentService.deleteReportAndComment(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Вывод статистики работы с жалобами
     * @return List<ModeratorsStatistic> - список со статистикой модераторов
     */
    @GetMapping("/statistic")
    @ApiOperation(value = "List moderators statistic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "List of statistics not found"),
            @ApiResponse(code = 200, message = "List of statistics found")
    })
    public ResponseEntity<List<ModeratorsStatistic>> showModeratorsStatistic() {
        List<ModeratorsStatistic> moderatorsStatistics = moderatorsStatisticService.findAll();
        return ResponseEntity.ok(moderatorsStatistics);
    }

    /**
     * Вывод количества необработанных комментариев
     * @return reportCommentService.findAllReportComments().size() - количество комментариев, ожидающих проверки
     */
    @GetMapping("/number-of-reports")
    @ApiOperation(value = "Number of non-checked reports",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "No comments found"),
            @ApiResponse(code = 200, message = "Number of comments found"),
    })
    public ResponseEntity<Integer> showNumberOfReports() {
        List<ModeratorsStatistic> moderatorsStatistics = moderatorsStatisticService.findAll();
        return ResponseEntity.ok(reportCommentService.findAllReportComments().size());
    }
}
