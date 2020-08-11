package com.jm.online_store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "stock_img")
    private String stockImg;

    @Column(name = "stock_title")
    private String stockTitle;

    @Column(name = "stock_text")
    private String stockText;

    @Column(name = "startDate")
    private Date startDate;

    public Stock() {

    }

    public Stock(Long id, String stockImg, String stockTitle, String stockText, Date startDate) {
        this.id = id;
        this.stockImg = stockImg;
        this.stockTitle = stockTitle;
        this.stockText = stockText;
        this.startDate = startDate;
    }

    public Stock(Long id, String stockImg, String stockTitle, String stockText) {
        this.id = id;
        this.stockImg = stockImg;
        this.stockTitle = stockTitle;
        this.stockText = stockText;
    }

    public Stock(String stockImg, String stockTitle, String stockText) {
        this.stockImg = stockImg;
        this.stockTitle = stockTitle;
        this.stockText = stockText;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStock_img() {
        return stockImg;
    }

    public void setStockImg(String stockImg) {
        this.stockImg = stockImg;
    }

    public String getStock_title() {
        return stockTitle;
    }

    public void setStockTitle(String stockTitle) {
        this.stockTitle = stockTitle;
    }

    public String getStock_text() {
        return stockText;
    }

    public void setStockText(String stockText) {
        this.stockText = stockText;
    }
}
