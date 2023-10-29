package com.example.googletabletest.exceptions;


public class CellCreateOrUpdateException extends RuntimeException {
    private static final String CELL_CREATE_EXCEPTION = "cannot create or find cell with this parameter %s and id %s";
    private static final String CELL_UPDATE_EXCEPTION = "Cannot update cell with this parameter %s";
    private static final String ROW_AND_COLUMN_EXCEPTION = "row %s or column %s is not in diapason between 1-4 or A-D, or created before";

    public CellCreateOrUpdateException(String value, Long id) {
        super(String.format(CELL_CREATE_EXCEPTION, value, id));
    }

    public CellCreateOrUpdateException(String value, Integer id) {
        super(String.format(CELL_CREATE_EXCEPTION, value, id));
    }

    public CellCreateOrUpdateException(String value) {
        super(String.format(CELL_UPDATE_EXCEPTION, value));
    }

    public CellCreateOrUpdateException(String row, String column) {
        super(String.format(ROW_AND_COLUMN_EXCEPTION, row, column));
    }

}
