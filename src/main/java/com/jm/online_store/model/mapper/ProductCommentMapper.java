package com.jm.online_store.model.mapper;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ProductCommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductCommentMapper extends AbstractMapper <Product, ProductCommentDto>{

    @Autowired
    public ProductCommentMapper(){
        super(Product.class, ProductCommentDto.class);
    }

}
