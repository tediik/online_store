package com.jm.online_store.service.impl;

import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.SharedStockNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.SharedStockRepository;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SharedStockServiceImpl implements SharedStockService {
    private final SharedStockRepository sharedStockRepository;
    private final StockService stockService;
    private final UserService userService;

    @Override
    public List<SharedStock> findAll() {
        return sharedStockRepository.findAll();
    }

    @Override
    @Transactional
    public SharedStock addSharedStock(SharedStock sharedStock) {
        User user = null != sharedStock.getUser() ?
                userService.findById(sharedStock.getUser().getId()).orElseThrow(() ->
                        new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND)) : null ;
        SharedStock sharedStockToAdd = SharedStock.builder()
                .stock(stockService.findStockById(sharedStock.getStock().getId()))
                .socialNetworkName(sharedStock.getSocialNetworkName())
                .user(user)
                .build();
        return sharedStockRepository.save(sharedStockToAdd);
    }

    /**
     * Метод возвращает залогиненного юзера. Работает только
     * при включенной сессии
     *
     * @return возвращает User
     */
    private User getCurrentLoggedInUser() {
       return userService.getCurrentLoggedInUser();
    }

}
