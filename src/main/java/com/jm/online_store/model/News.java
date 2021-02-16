package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "news")
@ApiModel(description =  "Сущность News, связь SharedNews")
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

    @JsonIgnore
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "news-sharedNews")
    private Set<SharedNews> sharedNews;

    public News(String title, String anons, String fullText) {
        this.title = title;
        this.anons = anons;
        this.fullText = fullText;
    }
}
