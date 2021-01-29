package com.jm.online_store.repository;

import com.jm.online_store.model.ModeratorsStatistic;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeratorsStatisticRepository extends JpaRepository<ModeratorsStatistic, Long> {
    ModeratorsStatistic getModeratorsStatisticByModerator(User moderator);
}
