package com.example.googletabletest.model.request;

import lombok.Data;

@Data
public class RowAndColumnInputRequest {
    private String row;
    private Integer columnNumber;
}
