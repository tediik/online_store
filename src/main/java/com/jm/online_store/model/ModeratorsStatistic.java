package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "moderators_statistic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeratorsStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_activity_date")
    @CreationTimestamp
    private LocalDateTime lastActivityDate;

    @Column(name = "approved_count")
    private Long approvedCount;

    @Column(name = "dismissed_count")
    private Long dismissedCount;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;
}
