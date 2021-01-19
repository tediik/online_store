package com.jm.online_store.service.impl;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.repository.ModeratorsStatisticRepository;
import com.jm.online_store.service.interf.ModeratorsStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModeratorsStatisticServiceImpl implements ModeratorsStatisticService {

    private final ModeratorsStatisticRepository moderatorsStatisticRepository;

    @Override
    public List<ModeratorsStatistic> findAll() {
        return moderatorsStatisticRepository.findAll();
    }
}
