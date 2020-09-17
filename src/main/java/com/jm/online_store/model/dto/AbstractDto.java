package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractDto implements Serializable {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy:mm:ss.SSS")
    LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy:mm:ss.SSS" )
    LocalDateTime updated;





}
