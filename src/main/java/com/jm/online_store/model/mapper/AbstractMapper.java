package com.jm.online_store.model.mapper;


import com.jm.online_store.model.AbstractEntity;
import com.jm.online_store.model.dto.AbstractDto;
import org.modelmapper.Converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class AbstractMapper <E extends AbstractEntity, D extends AbstractDto> implements Mapper<E,D>  {

    @Autowired
    ModelMapper mapper;

    private Class<E> entityClass;
    private Class<D> dtoClass;


    public AbstractMapper(Class<E> entityClass, Class<D> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public AbstractMapper() {
        super();
    }

    @Override
    public E toEntity(D dto) {
        return Objects.isNull(dto)
                ? null
                :mapper.map(dto,entityClass);
    }

    @Override
    public D toDto(E entity) {
        return Objects.isNull(entity)
                ? null
                :mapper.map(entity,dtoClass);
    }

    Converter<E,D> toEntityConverter(){
        return context -> {
            E source = context.getSource();
            D destination = context.getDestination();
            mapSpecificFields(source,destination);
            return context.getDestination();
        };
    }

    Converter<D,E> toDtoConverter(){
        return context -> {
            D source = context.getSource();
            E destination = context.getDestination();
            mapSpecificFields(source,destination);
            return context.getDestination();
        };
    }


    void mapSpecificFields(E source, D destination) {
    }

    void mapSpecificFields(D source, E destination) {
    }





}
