package com.jm.online_store.config.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
public class MaintenanceRestController {

    @Autowired
    private MaintenanceModel maintenanceModel;

    @GetMapping("/admin{maintenance}")
    public ResponseEntity<String> toggleMaintenanceMode(HttpServletRequest request) {
        String status = request.getRequestURI();;
        if (status.contains("true")) {
            maintenanceModel.turnOnMaintenance();
        } else {
            maintenanceModel.turnOffMaintenance();
        }
        return ResponseEntity.ok("Changed maintenance status to ");
    }

//    @RequestMapping("/admin{someID}")
//    public void getAttr(@PathVariable(value="someID") String id, @RequestParam String someAttr) {
//        System.out.println(id);
//        System.out.println(someAttr);
//    }

}
