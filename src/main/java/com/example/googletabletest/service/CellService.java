package com.example.googletabletest.service;

import com.example.googletabletest.exceptions.CellCreateOrUpdateException;
import com.example.googletabletest.mapper.CellMapper;
import com.example.googletabletest.model.CellModel;
import com.example.googletabletest.model.CellModelResultOutput;
import com.example.googletabletest.model.request.CellRequest;
import com.example.googletabletest.model.request.RowAndColumnInputRequest;
import com.example.googletabletest.repository.CellRepository;
import com.example.googletabletest.repository.entity.Cell;
import com.example.googletabletest.repository.entity.TableEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CellService {
    private final CellRepository cellRepository;
    private final TableService tableService;

    private final CellMapper cellMapper;

    @Transactional
    public CellModel createCell(Long tableID, CellRequest cellModel) {
        checkOnNullAndBlankInCreateMethod(tableID, cellModel);
        cellModel.setRow(cellModel.getRow().toUpperCase());

        if (cellRepository.existsByColumnNumberAndRowAndTableEntityId(cellModel.getColumnNumber(), cellModel.getRow(),tableID)) {
            throw new CellCreateOrUpdateException(cellModel.getRow(), String.valueOf(cellModel.getColumnNumber()));
        }

        validateCellCoordinates(cellModel);
        TableEntity tableEntity = tableService.findById(tableID);

        Cell cellEntity = new Cell();

        cellEntity.setTableEntity(tableEntity);
        updateCellFilds(cellModel, cellEntity);
        cellRepository.save(cellEntity);


        return cellMapper.toCellModel(cellEntity);
    }


    @Transactional
    public CellModel updateCellFormula(CellRequest cellRequest) {
        cellRequest.setRow(cellRequest.getRow().toUpperCase());

        checkOnNullAndBlankInUpdateMethod(cellRequest);
        validateCellCoordinates(cellRequest);

        Cell cellEntity = findById(cellRequest.getId());
        updateCellFilds(cellRequest, cellEntity);


        return cellMapper.toCellModel(cellEntity);
    }

    public CellModel getByRowAndColumn(RowAndColumnInputRequest request) {
        if (request.getRow() == null || request.getRow().isBlank()) {
            throw new CellCreateOrUpdateException(request.getRow(), request.getColumnNumber());
        }
        Cell cell = getCellByColumnAndRow(request.getColumnNumber(), request.getRow());

        return cellMapper.toCellModel(cell);

    }

    public List<CellModelResultOutput> getModels(String name) {
        return cellRepository.findAllByTableEntityName(name)
                .stream()
                .map(cellMapper::toCellModelResultOutput)
                .toList();
    }

    private void checkOnNullAndBlankInUpdateMethod(CellRequest cellRequest) {
        if (cellRequest == null ||
                cellRequest.getFormula() == null || cellRequest.getFormula().isBlank() ||
                cellRequest.getColumnNumber() == null ||
                cellRequest.getRow() == null || cellRequest.getRow().isBlank()) {
            throw new CellCreateOrUpdateException(String.valueOf(cellRequest.getId()));
        }
    }

    private void checkOnNullAndBlankInCreateMethod(Long tableID, CellRequest cellModel) {
        if (cellModel == null || tableID == null ||
                cellModel.getFormula() == null || cellModel.getFormula().isBlank() ||
                cellModel.getColumnNumber() == null ||
                cellModel.getRow() == null || cellModel.getRow().isBlank()) {
            throw new CellCreateOrUpdateException(cellModel.getFormula(), tableID);
        }
    }

    private void updateCellFilds(CellRequest cellRequest, Cell cellEntity) {
        cellEntity.setRow(cellRequest.getRow());
        cellEntity.setColumnNumber(cellRequest.getColumnNumber());

        if (cellRequest.getFormula().charAt(0) == '=') {
            double result = evaluateFormula(cellRequest.getFormula());
            cellEntity.setResult(result);
            cellEntity.setFormula(cellRequest.getFormula());

        } else if (cellRequest.getFormula().isBlank()) {
            cellEntity.setResult(0.0);
            cellEntity.setFormula("0.0");
        } else {
            double result;
            try {
                result = Double.parseDouble(cellRequest.getFormula());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("the Form is not a digit !.");
            }
            cellEntity.setResult(result);
            cellEntity.setFormula(String.valueOf(result));
        }
    }

    private Cell getCellByColumnAndRow(Integer column, String row) {
        return cellRepository.findByColumnNumberAndAndRow(column, row.trim())
                .orElseThrow(() -> new CellCreateOrUpdateException(row, column));
    }

    private Cell findById(Long id) {
        return cellRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("cell not found by id " + id));
    }


    private void validateCellCoordinates(CellRequest cellModel) {
        String row = cellModel.getRow();
        int column = cellModel.getColumnNumber();
        if (row == null || !row.matches("[A-D]") || column < 1 || column > 4) {
            throw new CellCreateOrUpdateException(cellModel.getRow(), cellModel.getColumnNumber().toString());
        }
    }

    private double evaluateFormula(String formula) {

        formula = replaceCellReferencesWithValues(formula);
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == ' ') {
                continue;
            }

            if (Character.isDigit(c)) {
                i = processNumber(values, formula, i);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                processClosingParenthesis(values, operators);
            } else if (isOperator(c)) {
                processOperator(c, values, operators);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private String replaceCellReferencesWithValues(String formula) {
        Pattern pattern = Pattern.compile("([A-Z]+[0-9]+)");
        Matcher matcher = pattern.matcher(formula);

        List<String> cellTokens = new ArrayList<>();
        while (matcher.find()) {
            String cellReference = matcher.group(1);
            cellTokens.add(cellReference);
        }

        for (String cellReference : cellTokens) {
            String row = cellReference.replaceAll("[^A-Z]", "");
            String column = cellReference.replaceAll("[^0-9]", "");

            Cell referencedCell = getCellByColumnAndRow(Integer.parseInt(column), row);

            if (referencedCell != null) {
                formula = formula.replaceAll(  cellReference, referencedCell.getResult().toString());
            } else {
                throw new IllegalArgumentException("Invalid cell reference: " + cellReference);
            }
        }

        return formula.replaceAll("=","");
    }


    private int processNumber(Stack<Double> values, String formula, int i) {
        StringBuilder num = new StringBuilder();
        while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
            num.append(formula.charAt(i));
            i++;
        }
        i--;
        values.push(Double.parseDouble(num.toString()));
        return i;
    }

    private void processClosingParenthesis(Stack<Double> values, Stack<Character> operators) {
        while (operators.peek() != '(') {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        operators.pop();
    }

    private void processOperator(char c, Stack<Double> values, Stack<Character> operators) {
        while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        operators.push(c);
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }


    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    // Обработка деления на ноль
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
