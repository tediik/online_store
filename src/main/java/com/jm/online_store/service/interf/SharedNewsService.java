package com.jm.online_store.service.interf;

import com.jm.online_store.model.SharedNews;

import java.util.List;

public interface SharedNewsService {
    List<SharedNews> findAll();

    SharedNews addSharedNews(SharedNews sharedNews);
}
