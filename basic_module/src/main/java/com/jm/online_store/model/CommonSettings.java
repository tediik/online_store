package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class contains settings for common tasks
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommonSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_name", unique = true)
    private String settingName;

    @Column(name = "text_value")
    @Type(type = "text")
    private String textValue;

    @Column(name = "status")
    private String status;
}
