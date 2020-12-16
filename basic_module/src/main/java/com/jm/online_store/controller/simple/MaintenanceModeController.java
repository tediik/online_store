package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролллер страницы "Технические работы (maintenanceMode.html)"
 */
@Controller
public class MaintenanceModeController {

    @GetMapping("maintenanceMode")
    public String maintenanceMode() {
        return "/maintenanceMode";
    }

}
