package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description =  "Сущность Employee расширяющая User")
@Table(name = "employees")
public class Employee extends User {


    public Employee(String email, String password ) {
        super(email, password);
    }

    @OneToMany(mappedBy = "employee", orphanRemoval = true)
    @JsonIgnore
    private Set<Feedback> feedbacks;
}
