package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleCommentController {

    @GetMapping("/comment-page")
    public String homePage() {
        return "commentTesterPage";
    }
}
