package com.example.googletabletest.controller;

import com.example.googletabletest.repository.entity.TableEntity;
import com.example.googletabletest.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:4200")
public class TableController {

    private final TableService tableService;

    @PostMapping
    public ResponseEntity<TableEntity> createTable(@RequestParam String tableName) {
        TableEntity tableEntity = tableService.createTable(tableName);
        return new ResponseEntity<>(tableEntity, HttpStatus.CREATED);
    }

}
