package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    @GetMapping
    public String moderatorPage() {
        return "moderatorPage";
    }

    @GetMapping("/comments")
    public String commentsModeration() {
        return "commentsModeration";
    }
}
