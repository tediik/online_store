package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.DeleteExpiredProfileTask;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteExpiredProfileTaskImpl implements DeleteExpiredProfileTask {
    private final CustomerService customerService;

    @Override
    public void run() {
        customerService.deleteAllBlockedWithThirtyDaysPassed();
    }
}
