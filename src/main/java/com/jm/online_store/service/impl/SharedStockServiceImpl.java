package com.jm.online_store.service.impl;

import com.jm.online_store.exception.aatest.ExceptionConstants;
import com.jm.online_store.exception.aatest.ExceptionEnums;
import com.jm.online_store.exception.sharedStockService.SharedStockExceptionConstants;
import com.jm.online_store.exception.sharedStockService.SharedStockNotFoundException;
import com.jm.online_store.exception.userService.UserExceptionConstants;
import com.jm.online_store.exception.userService.UserNotFoundException;
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
        sharedStock.setUser(getCurrentLoggedInUser());
        if (sharedStock.getUser() != null) {
            User foundUser = sharedStock.getUser();
            if (userService.findById(foundUser.getId()).isPresent()) {
                SharedStock returnValue = SharedStock.builder()
                            .stock(stockService.findStockById(sharedStock.getStock().getId()))
                            .socialNetworkName(sharedStock.getSocialNetworkName())
                            .user(foundUser)
                            .build();
                return  sharedStockRepository.save(returnValue);
            } else {
                throw new UserNotFoundException(ExceptionEnums.USER.getText() + ExceptionConstants.NOT_FOUND);
            }
        } else {
            throw new SharedStockNotFoundException(ExceptionEnums.SHARER_STOCK.getText() + ExceptionConstants.NOT_FOUND);
        }

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
