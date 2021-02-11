package com.jm.online_store.model.dto;


import com.jm.online_store.model.Topic;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для Topic")
public class TopicsCategoryDto {
    private Long id;
    private String categoryName;
    private List<Topic> topics;
}
