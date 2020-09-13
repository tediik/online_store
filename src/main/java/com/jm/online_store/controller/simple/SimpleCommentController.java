package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleCommentController {

    // TODO: 13/09/2020 This is temporary Comment testing controller. After main product page will be completed and launched, this Controller should be removed

    /**
     * Returns comment page
     * @return
     */
    @GetMapping("/comment-page")
    public String homePage() {
        return "commentTesterPage";
    }
}
