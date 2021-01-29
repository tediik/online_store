package com.jm.online_store.service.impl;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ModeratorsStatisticRepository;
import com.jm.online_store.service.interf.ModeratorsStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ModeratorsStatisticServiceImpl implements ModeratorsStatisticService {

    private final ModeratorsStatisticRepository moderatorsStatisticRepository;

    /**
     * список статистики отклоненных и принятых жалоб
     * @return List<ModeratorsStatistic>
     */
    @Override
    public List<ModeratorsStatistic> findAll() {
        return moderatorsStatisticRepository.findAll();
    }

    /**
     * увеличение счетчика отклоненных жалоб
     * @param moderator - модератор в таблице статистики
     */
    @Override
    public void incrementDismissedCount(User moderator) {
        ModeratorsStatistic updateStatistic;
        if (moderatorsStatisticRepository.getModeratorsStatisticByModerator(moderator) == null) {
            updateStatistic = new ModeratorsStatistic();
            updateStatistic.setModerator(moderator);
            updateStatistic.setDismissedCount(1L);
            updateStatistic.setApprovedCount(0L);
        } else  {
            updateStatistic = moderatorsStatisticRepository.getModeratorsStatisticByModerator(moderator);
            updateStatistic.setDismissedCount(updateStatistic.getDismissedCount() + 1L);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        updateStatistic.setLastActivityDate(LocalDateTime.now().format(formatter));
        moderatorsStatisticRepository.save(updateStatistic);
    }

    /**
     * увеличение счетчика одобренных жалоб
     * @param moderator - модератор в таблице статистики
     */
    @Override
    public void incrementApprovedCount(User moderator) {
        ModeratorsStatistic updateStatistic;
        if (moderatorsStatisticRepository.getModeratorsStatisticByModerator(moderator) == null) {
            updateStatistic = new ModeratorsStatistic();
            updateStatistic.setModerator(moderator);
            updateStatistic.setApprovedCount(1L);
            updateStatistic.setDismissedCount(0L);
        } else  {
            updateStatistic = moderatorsStatisticRepository.getModeratorsStatisticByModerator(moderator);
            updateStatistic.setApprovedCount(updateStatistic.getApprovedCount() + 1L);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        updateStatistic.setLastActivityDate(LocalDateTime.now().format(formatter));
        moderatorsStatisticRepository.save(updateStatistic);
    }

}
