package com.example.googletabletest.repository;


import com.example.googletabletest.repository.entity.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CellRepository extends JpaRepository<Cell, Long> {
    List<Cell> findAllByTableEntityName(String tableName);

    Optional<Cell> findByColumnNumberAndAndRow(Integer column, String row);

    Boolean existsByColumnNumberAndRowAndTableEntityId(Integer column, String row,Long tableId);
}
