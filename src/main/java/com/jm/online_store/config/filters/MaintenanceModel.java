package com.jm.online_store.config.filters;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MaintenanceModel {
    private boolean mode;

    public boolean turnOnMaintenance() {
        return mode = true;
    }

    public boolean turnOffMaintenance() {
        return mode = false;
    }

}
