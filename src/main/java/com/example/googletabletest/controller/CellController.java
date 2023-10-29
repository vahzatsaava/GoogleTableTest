package com.example.googletabletest.controller;

import com.example.googletabletest.model.CellModel;
import com.example.googletabletest.model.CellModelResultOutput;
import com.example.googletabletest.model.request.CellRequest;
import com.example.googletabletest.model.request.RowAndColumnInputRequest;
import com.example.googletabletest.service.CellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cells")
@CrossOrigin(origins = "http://localhost:4200")
public class CellController {
    private final CellService cellService;

    @PostMapping("/create")
    public ResponseEntity<CellModel> createCell(@RequestParam Long tableID, @RequestBody CellRequest cellRequest) {
        return new ResponseEntity<>(cellService.createCell(tableID, cellRequest), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<CellModel> updateCellValue(@RequestBody CellRequest cellRequest) {
        return new ResponseEntity<>(cellService.updateCellFormula(cellRequest), HttpStatus.OK);
    }

    @GetMapping("/all/{name}")
    public ResponseEntity<List<CellModelResultOutput>> getModels(@PathVariable String name) {
        return new ResponseEntity<>(cellService.getModels(name), HttpStatus.OK);
    }

    @PostMapping("/by-row-and-column")
    public ResponseEntity<CellModel> getModelByRowAndColumn(@RequestBody RowAndColumnInputRequest request) {
        return new ResponseEntity<>(cellService.getByRowAndColumn(request), HttpStatus.OK);
    }


}
