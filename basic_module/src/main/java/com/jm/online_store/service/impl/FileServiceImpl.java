package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.FileService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {


    private static final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images" +
            File.separator + "stocks";

    public String uploadFile(MultipartFile file) {
        String uniqueFilename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path copyLocation = Paths
                    .get(uploadDir, uniqueFilename);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  File.separator + "uploads" + File.separator + "images" + File.separator + "stocks" + File.separator + uniqueFilename;
    }
}