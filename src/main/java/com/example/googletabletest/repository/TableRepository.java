package com.example.googletabletest.repository;

import com.example.googletabletest.repository.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    Optional<TableEntity> findByName(String name);
}
