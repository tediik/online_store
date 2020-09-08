package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String anons;

    @Column(name = "full_text")
    @Type(type = "text")
    private String fullText;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate postingDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    @Column(name = "archived")
    private boolean archived;

    public News(String title, String anons, String fullText) {
        this.title = title;
        this.anons = anons;
        this.fullText = fullText;
    }

}
