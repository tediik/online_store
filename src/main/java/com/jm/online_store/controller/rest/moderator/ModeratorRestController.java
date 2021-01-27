package com.jm.online_store.controller.rest.moderator;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.model.ReportComment;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ReportCommentDto;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.ModeratorsStatisticService;
import com.jm.online_store.service.interf.ReportCommentService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiOperation(value = "Get list of all reports on comments with WebSocket")
    public List<ReportCommentDto> allReportComments() {
        return reportCommentService.findAllReportComments().stream()
                .map(ReportCommentDto::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавление жалобы на комментарий.
     *
     * @param reportCommentDto
     */
    @PostMapping
    @ApiOperation(value = "Add a new report on comment")
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
    @ApiOperation(value = "Delete report on comment by report ID")
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
    @ApiOperation(value = "Delete report and comment  by comment ID")
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
    @ApiOperation(value = "List moderators statistic")
    public ResponseEntity<List<ModeratorsStatistic>> showModeratorsStatistic() {
        List<ModeratorsStatistic> moderatorsStatistics = moderatorsStatisticService.findAll();
        return ResponseEntity.ok(moderatorsStatistics);
    }

    /**
     * метод обработки изменения пароля User.
     *
     * @param model       модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
    @PostMapping("/change-password")
    @ApiOperation(value = "processes Customers request to change email")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No user with such id"),
            @ApiResponse(code = 400, message = "Wrong email or user with such email already exists"),
            @ApiResponse(code = 200, message = "Changes accepted"),
    })
    public ResponseEntity changePassword(Model model,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.findById(user.getId()).isEmpty()) {
            log.debug("Нет пользователя с идентификатором: {}", user.getId());
            return ResponseEntity.noContent().build();
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.badRequest().body("notValidEmailError");
        }
        if (userService.findById(user.getId()).get().getEmail().equals(user.getEmail())
                && userService.isExist(user.getEmail())) {
            log.debug("Пользователь с таким адресом электронной почты уже существует");
            return ResponseEntity.badRequest().body("duplicatedEmailError");
        }
        if (userService.changePassword(user.getId(), oldPassword, newPassword))
            log.debug("Изменения для пользователя с идентификатором: {} был успешно добавлен", user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changemail")
    @ApiOperation(value = "processes Customers request to change email")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "duplicatedEmailError or notValidEmailError"),
            @ApiResponse(code = 200, message = "Email will be changed after confirmation"),
    })
    public ResponseEntity<String> changeMailReq(@RequestParam String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            log.debug("Попытка ввести дублирующийся email: " + newMail);
            return new ResponseEntity("duplicatedEmailError", HttpStatus.BAD_REQUEST);
        }
        if (ValidationUtils.isNotValidEmail(newMail)) {
            return new ResponseEntity("notValidEmailError", HttpStatus.BAD_REQUEST);
        } else {
            userService.changeUsersMail(user, newMail);
            return ResponseEntity.ok("Email будет изменен после подтверждения.");
        }
    }

    @GetMapping("/number-of-reports")
    @ApiOperation(value = "Number of non-checked reports")
    public ResponseEntity<Integer> showNumberOfReports() {
        List<ModeratorsStatistic> moderatorsStatistics = moderatorsStatisticService.findAll();
        return ResponseEntity.ok(reportCommentService.findAllReportComments().size());
    }
}
