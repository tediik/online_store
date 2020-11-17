package com.jm.online_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "review_date")
    @CreationTimestamp
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @Column(name = "product_id")
    private Long productId;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Comment> comments;
}


