package com.jm.online_store.model.dto;


import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Product;
import lombok.Data;
import java.util.List;

@Data
public class ProductForCommentDto {

    private Long id;
    private List<Comment> comments;

    public static ProductForCommentDto productToDto(Product product){
        ProductForCommentDto productForCommentDto = new ProductForCommentDto();
        productForCommentDto.setId(product.getId());
        productForCommentDto.setComments(product.getComments());
        return productForCommentDto;
    }

}
