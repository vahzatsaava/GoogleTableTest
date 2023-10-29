package com.example.googletabletest.mapper;

import com.example.googletabletest.model.TableModel;
import com.example.googletabletest.repository.entity.TableEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {
    TableEntity toTableEntity(TableModel tableModel);

    @InheritInverseConfiguration
    TableModel toTableModel(TableEntity tableEntity);
}
