package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@AllArgsConstructor
public class TestWrapper {

    private Product product;

    private MultipartFile multipartFile;
}
