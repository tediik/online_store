package com.jm.online_store.model.dto;

import com.jm.online_store.model.Product;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@ApiModel(description =  "DTO для передачи комментария продукту")
public class ProductForCommentDto {

    private Long id;
    private List<CommentDto> comments;

    public static ProductForCommentDto productToDto(Product product) {
        ProductForCommentDto productForCommentDto = new ProductForCommentDto();
        productForCommentDto.setId(product.getId());
        productForCommentDto.setComments(product.getComments()
                .stream()
                .map(CommentDto::commentEntityToDto)
                .collect(Collectors.toList()));
        return productForCommentDto;
    }
}