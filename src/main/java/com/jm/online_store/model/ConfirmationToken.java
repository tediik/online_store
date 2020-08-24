package com.jm.online_store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

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
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    public ConfirmationToken() {

    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserId(Long userId) { this.userId = userId; }

    public Long getUserId() {
        return userId;
    }
}
