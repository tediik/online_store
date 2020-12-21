package com.jm.online_store.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description =  "Сущность ConfirmationToken для  токена")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column
    private long tokenId;

    @Column
    private String confirmationToken;

    @Column
    private String userEmail;

    @Column
    private String userPassword;

    @Column
    private Long userId;

    private LocalDate createdDate;

    public ConfirmationToken(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        createdDate = LocalDate.now();
        confirmationToken = UUID.randomUUID().toString();
    }

    public ConfirmationToken(Long userId, String userMail) {
        this.userId = userId;
        this.userEmail = userMail;
        createdDate = LocalDate.now();
        confirmationToken = UUID.randomUUID().toString();
    }
}
