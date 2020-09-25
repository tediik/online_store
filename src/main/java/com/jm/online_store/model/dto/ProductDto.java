package com.jm.online_store.model.dto;

import com.jm.online_store.model.Comment;


import java.util.List;

public class ProductDto {

    private Long id;
    private List<Comment> comments;

    public ProductDto(Long id, List<Comment> comments) {
        this.id = id;
        this.comments = comments;
    }

    public ProductDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
