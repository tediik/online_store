package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Сущность - Новость, которой поделились в социальной сети. Отражает то,
 * какой пользователь {@link User}, какой новостью {@link News},
 * в какой социальной сети {@link String} поделился
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "shared_news")
@ApiModel(description =  "Сущность SharedNews - Новость, которой поделились в социальной сети. Отражает то,\n" +
        " * какой пользователь User, какой новостью News,\n" +
        " * в какой социальной сети String поделился. Связана с User и News")
public class SharedNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_network_name")
    private String socialNetworkName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer_id")
    @JsonBackReference(value = "customer-sharedNews")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    @JsonBackReference(value = "news-sharedNews")
    private News news;
}
