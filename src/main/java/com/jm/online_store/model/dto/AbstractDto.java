package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractDto implements Serializable {

    private Long id;
}
