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
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne
    @JoinColumn(name = "parent_comment")
    private ProductComment parentComment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "comment_date")
    @CreationTimestamp
    private LocalDateTime commentDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
