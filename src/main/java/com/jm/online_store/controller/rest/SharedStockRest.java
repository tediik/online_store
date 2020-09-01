package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/sharedStock")
public class SharedStockRest {
    private final StockService stockService;
    private final SharedStockService sharedStockService;

    @PostMapping
    public ResponseEntity<String> addSharedStock(@RequestBody SharedStock sharedStock, Authentication authentication) {
        sharedStock.setUser((User) authentication.getPrincipal());
        try {
            sharedStockService.addSharedStock(sharedStock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
