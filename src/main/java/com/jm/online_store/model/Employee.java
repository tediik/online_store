package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description =  "Сущность Employee расширяющая User")
public class Employee extends User{

    @OneToMany(mappedBy = "employee", orphanRemoval = true)
    @JsonIgnore
    private Set<Feedback> feedbacks;
}
