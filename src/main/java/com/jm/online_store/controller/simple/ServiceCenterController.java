package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Контроллер сервисного центра
 */
@Controller
@AllArgsConstructor
public class ServiceCenterController {

    @GetMapping("/serviceCenter")
    public String getServiceCenter(){
        return "service-center";
    }

    @GetMapping("/serviceCheckStatus")
    public String getServiceCheckStatus(){
        return "service-check-status";
    }

    @GetMapping("/service")
    public String getServiceProfile() {
        return "service-page";
    }
}