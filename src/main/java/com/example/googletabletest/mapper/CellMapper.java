package com.example.googletabletest.mapper;

import com.example.googletabletest.model.CellModel;
import com.example.googletabletest.model.CellModelResultOutput;
import com.example.googletabletest.repository.entity.Cell;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CellMapper {

    CellModel toCellModel(Cell cellEntity);

    @InheritInverseConfiguration
    Cell toCellEntity(CellModel cellModel);

    CellModelResultOutput toCellModelResultOutput(Cell cell);
}
