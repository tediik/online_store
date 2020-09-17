package com.jm.online_store.model.mapper;


import com.jm.online_store.model.AbstractEntity;
import com.jm.online_store.model.dto.AbstractDto;

public interface Mapper <E extends AbstractEntity, D extends AbstractDto> {

    E toEntity(D dto);

    D toDto(E entity);
}
