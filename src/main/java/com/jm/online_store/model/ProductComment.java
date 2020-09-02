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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long parent_id;

    @ManyToOne
    private ProductComment parent_Comment;

    //    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;
//
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "comment_date")
    @CreationTimestamp
    private LocalDateTime commentDate;



//    @ManyToOne
//    private User parentUser;
}
