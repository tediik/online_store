package com.jm.online_store.service.impl;

import com.jm.online_store.model.SharedNews;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.SharedNewsRepository;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.SharedNewsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SharedNewsServiceImpl implements SharedNewsService {
    private final SharedNewsRepository sharedNewsRepository;
    private final NewsService newsService;
    private final UserService userService;

    @Override
    public List<SharedNews> findAll() {
        return sharedNewsRepository.findAll();
    }

    @Override
    public SharedNews addSharedNews(SharedNews sharedNews) {
        User user = null != sharedNews.getUser() ?
                userService.findById(sharedNews.getUser().getId()).get() : null;
        SharedNews sharedNewsToAdd = SharedNews.builder()
                .news(newsService.findById(sharedNews.getNews().getId()))
                .socialNetworkName(sharedNews.getSocialNetworkName())
                .user(user)
                .build();
        return sharedNewsRepository.save(sharedNewsToAdd);
    }
}
