package com.jm.online_store.model;

import com.jm.online_store.enums.BadWordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность Стоп Слова, для фильтрации
 * комментариев
 */

@Entity
@Table(name = "bad_words")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BadWords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String badword;
    @NonNull
    private BadWordStatus status;

    public BadWords(@NonNull String badword, @NonNull BadWordStatus status) {
        this.badword = badword;
        this.status = status;
    }
}
