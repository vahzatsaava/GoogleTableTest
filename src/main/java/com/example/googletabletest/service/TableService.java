package com.example.googletabletest.service;

import com.example.googletabletest.repository.TableRepository;
import com.example.googletabletest.repository.entity.TableEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    @Transactional
    public TableEntity createTable(String tableName) {
        TableEntity table = findByName(tableName);
        if (table != null) {
            return table;
        }
        TableEntity tableEntity = new TableEntity();
        tableEntity.setName(tableName);
        return tableRepository.save(tableEntity);
    }

    public TableEntity findById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found by id " + id));
    }

    private TableEntity findByName(String name) {
        return tableRepository.findByName(name)
                .orElse(null);
    }


}
