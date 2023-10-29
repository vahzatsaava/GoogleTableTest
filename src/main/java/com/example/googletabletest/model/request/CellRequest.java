package com.example.googletabletest.model.request;

import lombok.Data;

@Data
public class CellRequest {
    private Long id;
    private String formula;
    private String row;
    private Integer columnNumber;
}
