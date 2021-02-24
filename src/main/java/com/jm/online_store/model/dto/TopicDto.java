package com.jm.online_store.model.dto;

import com.jm.online_store.model.TopicsCategory;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для Topic")
public class TopicDto {
    private Long id;
    private String topicName;
    private TopicsCategoryDto topicsCategory;
}
