package util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CharField {

    public final char[][] field;
    public final int numRows;
    public final int numCols;

    public static CharField of(String input) {
        char[][] field = input.lines().map(String::toCharArray).toArray(char[][]::new);
        return new CharField(field);
    }

    public CharField(int numRows, int numCols, char charToFill) {
        this.field = new char[numRows][numCols];
        this.numRows = numRows;
        this.numCols = numCols;
        if (charToFill != (char) 0) {
            for (char[] chars : field) {
                Arrays.fill(chars, charToFill);
            }
        }
    }

    private CharField(char[][] field) {
        this.field = field;
        this.numRows = field.length;
        this.numCols = field[0].length;
        boolean allRowsSameLength = Arrays.stream(field)
                .mapToInt(row -> row.length)
                .allMatch(rowLength -> rowLength == numCols);
        if (!allRowsSameLength) {
            throw new IllegalArgumentException("Not all rows have the same length");
        }
    }

    @SuppressWarnings("unused")
    public CharField(CharField otherField) {
        // Clone the supplied CharField
        this.field = Arrays.stream(otherField.field).map(char[]::clone).toArray(char[][]::new);
        this.numRows = otherField.numRows;
        this.numCols = otherField.numCols;
    }

    public CharField transpose() {
        char[][] newField = new char[numCols][numRows];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                newField[c][r] = field[r][c];
            }
        }
        return new CharField(newField);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public char[][] getField() {
        return field;
    }

    public char get(Cell cell) {
        return get(cell.row(), cell.col());
    }

    public char get(int row, int col) {
        return field[row][col];
    }

    public void set(Cell cell, char value) {
        set(cell.row(), cell.col(), value);
    }

    public void set(int row, int col, char value) {
        field[row][col] =  value;
    }

    public Optional<Cell> findFirstCell(char charToFind) {
        for (int r = 0; r < numRows; r++) {
            int c = ArrayUtils.indexOf(field[r], charToFind);
            if (c == ArrayUtils.INDEX_NOT_FOUND) {
                continue;
            }
            return Optional.of(new Cell(r, c));
        }
        return Optional.empty();
    }

    public List<Cell> findAllCells(char charToFind) {
        List<Cell> result = new ArrayList<>();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (field[r][c] == charToFind) {
                    result.add(new Cell(r, c));
                }
            }
        }
        return result;
    }

    public long countInRow(int row, char charToFind) {
        long result = 0;
        for (int c = 0; c < numCols; c++) {
            if (field[row][c] == charToFind) {
                result++;
            }
        }
        return result;
    }

    public boolean isWithinField(Cell cell) {
        return isWithinField(cell.row(), cell.col());
    }

    public boolean isWithinField(int row, int col) {
        return -1 < row && row < numRows && -1 < col && col < numCols;
    }

    public Stream<Character> getNeighborValuesStream(int row, int col, boolean onlyULRD) {
        return getNeighborsStream(row, col, onlyULRD)
                .map(this::get);
    }

    public int countNeighbors(int row, int col, char value, boolean onlyULDR) {
        // Inclusive
        int rowFrom = Math.max(row - 1, 0);
        int rowTo = Math.min(row + 1, numRows - 1);
        int colFrom = Math.max(col - 1, 0);
        int colTo = Math.min(col + 1, numCols - 1);

        int count = 0;
        if (onlyULDR) {
            for (int r = rowFrom; r <= rowTo; r++) {
                if (field[r][col] == value) {
                    count++;
                }
            }
            for (int c = colFrom; c <= colTo; c++) {
                if (field[row][c] == value) {
                    count++;
                }
            }
            // Subtract count for the field itself twice
            count -= field[row][col] == value ? 2 : 0;
        } else {
            for (int r = rowFrom; r <= rowTo; r++) {
                for (int c = colFrom; c <= colTo; c++) {
                    if (field[r][c] == value) {
                        count++;
                    }
                }
            }
            // Subtract count for the field itself
            count -= field[row][col] == value ? 1 : 0;
        }
        return count;
    }

    public Stream<Cell> getNeighborsStream(Cell cell, boolean onlyULRD) {
        return getNeighborsStream(cell.row(), cell.col(), onlyULRD);
    }

    public Stream<Cell> getNeighborsStream(int row, int col, boolean onlyULRD) {
        Stream<Cell> neighborsStream = onlyULRD ? Stream.of(Helpers.getNeighborsULRD(row, col)) : Helpers.getNeighbors8(row, col);
        return neighborsStream
                .filter(this::isWithinField);
    }
}
