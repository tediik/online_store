package com.jm.online_store.controller.rest;

import com.jm.online_store.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadRestController {

    @Autowired
    FileServiceImpl fileService;


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);

        return "redirect:/manager/stocks";
    }
}
