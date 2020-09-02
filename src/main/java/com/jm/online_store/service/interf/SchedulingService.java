package com.jm.online_store.service.interf;

import org.springframework.scheduling.annotation.Scheduled;

public interface SchedulingService {
    @Scheduled(cron = "${emailStockSending.delay}")
    void sendStocksToCustomers();
}
