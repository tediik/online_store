package com.jm.online_store.service.interf;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;

import java.util.List;

public interface SharedStockService {
    List<SharedStock> findAll();

    SharedStock addSharedStock(SharedStock sharedStock);
}
