package com.jm.online_store.model;

import com.jm.online_store.enums.RepairOrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repair_order")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String fullNameClient;
    @NonNull
    private String telephoneNumber;
    @NonNull
    private String nameDevice;

    private boolean guarantee;
    @NonNull
    @Type(type = "text")
    private String fullTextProblem;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate acceptanceDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate modifiedDate;

    @Enumerated(EnumType.STRING)
    private RepairOrderType repairOrderType;
}
