package com.example.googletabletest.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "table_entity")
@Data
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",unique = true)
    private String name;

    @OneToMany(mappedBy = "tableEntity",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Cell> cells;

}
