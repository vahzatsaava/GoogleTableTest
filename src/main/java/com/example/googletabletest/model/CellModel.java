package com.example.googletabletest.model;

import lombok.Data;

@Data
public class CellModel {
    private Long id;
    private String formula;
    private Double result;
    private String row;
    private Integer columnNumber;
    private TableModel tableEntity;
}
