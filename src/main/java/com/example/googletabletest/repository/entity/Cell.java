package com.example.googletabletest.repository.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "cell")
@Data
@NoArgsConstructor
public class Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String formula;
    private Double result;
    private String row;
    private Integer columnNumber;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity tableEntity;
}
