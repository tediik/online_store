package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Slf4j
@ApiModel(description = "DTO для сущности комментария Employee")
@Component
public class EmployeeDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<FeedBackDto> feedBackDto;
}
