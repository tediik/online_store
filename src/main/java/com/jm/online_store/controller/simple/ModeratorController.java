package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.ReportCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
@AllArgsConstructor
public class ModeratorController {

    @GetMapping
    public String moderatorPage() {
        return "moderator-page";
    }

}
