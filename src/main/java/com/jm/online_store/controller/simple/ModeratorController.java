package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.ReportCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
@AllArgsConstructor
public class ModeratorController {
    private final ReportCommentService reportCommentService;

    @GetMapping
    public String moderatorPage(Model model) {
        model.addAttribute("numberOfReports", reportCommentService.findAllReportComments().size());
        return "moderatorPage";
    }

    @GetMapping("/comments")
    public String commentsModeration() {
        return "moderatorCommentsCheck";
    }
}
