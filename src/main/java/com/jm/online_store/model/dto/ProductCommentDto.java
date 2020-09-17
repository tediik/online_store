package com.jm.online_store.model.dto;

import com.jm.online_store.model.ProductComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCommentDto extends AbstractDto {

    private Long id;

    private Collection<ProductComment> comments = new ArrayList<>();


}
