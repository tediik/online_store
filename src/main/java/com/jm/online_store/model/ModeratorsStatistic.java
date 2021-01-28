package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "moderators_statistic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeratorsStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * дата и время последней активности модератора
     */
    @Column(name = "last_activity_date")
    private String lastActivityDate;

    /**
     * количество одобренных жалоб
     */
    @Column(name = "approved_count")
    private Long approvedCount;

    /**
     * количество отклоненных жалоб
     */
    @Column(name = "dismissed_count")
    private Long dismissedCount;

    /**
     * связь с объектом пользователя-модератора в общей таблице пользователей
     */
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
}
