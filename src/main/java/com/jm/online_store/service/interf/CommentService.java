package com.jm.online_store.service.interf;

import com.jm.online_store.model.ProductComment;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CommentService {
    
    List<ProductComment> findAll(Long productId);
    
    ProductComment addComment(@RequestBody ProductComment productComment);

    ProductComment findById(Long parentId);
}
