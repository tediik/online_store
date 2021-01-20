package com.jm.online_store.service.impl;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ModeratorsStatisticRepository;
import com.jm.online_store.service.interf.ModeratorsStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ModeratorsStatisticServiceImpl implements ModeratorsStatisticService {

    private final ModeratorsStatisticRepository moderatorsStatisticRepository;

    @Override
    public List<ModeratorsStatistic> findAll() {
        return moderatorsStatisticRepository.findAll();
    }

    @Override
    public void incrementDismissedCount(User moderator) {
        ModeratorsStatistic updateStatistic;
        if (moderatorsStatisticRepository.getByModeratorEquals(moderator) == null) {
            updateStatistic = new ModeratorsStatistic();
            updateStatistic.setModerator(moderator);
            updateStatistic.setDismissedCount(1L);
        } else  {
            updateStatistic = moderatorsStatisticRepository.getByModeratorEquals(moderator);
            updateStatistic.setDismissedCount(updateStatistic.getDismissedCount() + 1L);
        }
        updateStatistic.setLastActivityDate(LocalDateTime.now());
        moderatorsStatisticRepository.save(updateStatistic);
    }

    @Override
    public void incrementApprovedCount(User moderator) {
        ModeratorsStatistic updateStatistic;
        if (moderatorsStatisticRepository.getByModeratorEquals(moderator) == null) {
            updateStatistic = new ModeratorsStatistic();
            updateStatistic.setModerator(moderator);
            updateStatistic.setApprovedCount(1L);
        } else  {
            updateStatistic = moderatorsStatisticRepository.getByModeratorEquals(moderator);
            updateStatistic.setApprovedCount(updateStatistic.getApprovedCount() + 1L);
        }
        updateStatistic.setLastActivityDate(LocalDateTime.now());
        moderatorsStatisticRepository.save(updateStatistic);
    }

}
