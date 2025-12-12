package util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class CharFieldLinear {
    public final char[] field;
    public final int stride;
    public final int numRows;

    public static CharFieldLinear of(String input) {
        return of(input, 0, (char) 0);
    }

    public static CharFieldLinear of(String input, int padSize, char padChar) {
        List<String> lines = input.lines().toList();
        int numRowsSrc = lines.size();
        int strideSrc = lines.getFirst().length();
        CharFieldLinear field = new CharFieldLinear(numRowsSrc + padSize * 2, strideSrc + padSize * 2, padChar);
        int destPos = (field.stride * padSize) + padSize; // go to the top left position
        char[] innerField = field.field;
        for (String line : lines) {
            char[] lineArr = line.toCharArray();
            if (lineArr.length != strideSrc) {
                throw new IllegalArgumentException("Not all source rows have the same length");
            }
            System.arraycopy(lineArr, 0, innerField, destPos, strideSrc);
            destPos += field.stride;
        }

        return field;
    }

    public CharFieldLinear(int numRows, int numCols, char charToFill) {
        this.field = new char[numRows * numCols];
        if (charToFill != 0) {
            Arrays.fill(field, charToFill);
        }
        this.numRows = numRows;
        this.stride = numCols;
    }

    private CharFieldLinear(char[][] fieldFrom) {
        this.numRows = fieldFrom.length;
        this.stride = fieldFrom[0].length;
        boolean allRowsSameLength = Arrays.stream(fieldFrom)
                .mapToInt(row -> row.length)
                .allMatch(rowLength -> rowLength == stride);
        if (!allRowsSameLength) {
            throw new IllegalArgumentException("Not all source rows have the same length");
        }
        char[] fieldTo = new char[numRows * stride];
        int destPos = 0;
        for (int row = 0; row < numRows; row++) {
            System.arraycopy(fieldFrom[row], 0, fieldTo, destPos, stride);
            destPos += stride;
        }
        this.field = fieldTo;
    }

    public CharFieldLinear(CharFieldLinear otherField) {
        // Clone the supplied CharField
        this.field = Arrays.copyOf(otherField.field, otherField.field.length);
        this.numRows = otherField.numRows;
        this.stride = otherField.stride;
    }
//
//    public CharFieldLinear transpose() {
//        char[][] newField = new char[numCols][numRows];
//        for (int r = 0; r < numRows; r++) {
//            for (int c = 0; c < numCols; c++) {
//                newField[c][r] = field[r][c];
//            }
//        }
//        return new CharFieldLinear(newField);
//    }

    public void copyFrom(CharFieldLinear otherField) {
        if (numRows != otherField.numRows || stride != otherField.stride) {
            throw new IllegalArgumentException("Incompatible types");
        }
        System.arraycopy(otherField.field, 0, field, 0, field.length);
    }

    public CharFieldLinear pad(int borderSize, char borderFill) {
        CharFieldLinear paddedCopy = new CharFieldLinear(numRows + borderSize * 2, stride + borderSize * 2, borderFill);
        int srcPos = 0;
        int dstPos = paddedCopy.stride + borderSize;
        for (int row = borderSize; row < paddedCopy.numRows - borderSize; row++) {
            System.arraycopy(field, srcPos, paddedCopy.field, dstPos, stride);
            srcPos += stride;
            dstPos += paddedCopy.stride;
        }
        return paddedCopy;
    }

    public void replaceAllExcept(char newValue, char... skipValues) {
        for (int cell = 0; cell < field.length; cell++) {
            char c = field[cell];
            boolean shouldSkip = false;
            for (char skipValue : skipValues) {
                if (c == skipValue) {
                    shouldSkip = true;
                    break;
                }
            }
            if (!shouldSkip) {
                field[cell] = newValue;
            }
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return stride;
    }

    public char[] getField() {
        return field;
    }

    public char get(Cell cell) {
        return get(cell.row(), cell.col());
    }

    public char get(int row, int col) {
        return get(row * stride + col);
    }

    public char get(int idx) { return field[idx]; }

    public void set(Cell cell, char value) {
        set(cell.row(), cell.col(), value);
    }

    public void set(int row, int col, char value) {
        set(row * stride + col, value);
    }

    public void set(int idx, char value) { field[idx] = value; }

    public int findFirstCellIdx(char charToFind) {
        return ArrayUtils.indexOf(field, charToFind);
    }

    public boolean isWithinField(Cell cell) {
        return isWithinField(cell.row(), cell.col());
    }

    public boolean isWithinField(int row, int col) {
        return (-1 < col) && (col < stride) && (-1 < row) && (row < numRows);
    }

    public boolean isWithinField(int idx) {
        return (-1 < idx) && (idx < field.length);
    }

//    public Stream<Character> getNeighborValuesStream(int row, int col, boolean onlyULRD) {
//        return getNeighborsStream(row, col, onlyULRD)
//                .map(this::get);
//    }
//
//    public int countNeighbors(int row, int col, char value, boolean onlyULDR) {
//        // Inclusive
//        int rowFrom = Math.max(row - 1, 0);
//        int rowTo = Math.min(row + 1, numRows - 1);
//        int colFrom = Math.max(col - 1, 0);
//        int colTo = Math.min(col + 1, numCols - 1);
//
//        int count = 0;
//        if (onlyULDR) {
//            for (int r = rowFrom; r <= rowTo; r++) {
//                if (field[r][col] == value) {
//                    count++;
//                }
//            }
//            for (int c = colFrom; c <= colTo; c++) {
//                if (field[row][c] == value) {
//                    count++;
//                }
//            }
//            // Subtract count for the field itself twice
//            count -= field[row][col] == value ? 2 : 0;
//        } else {
//            for (int r = rowFrom; r <= rowTo; r++) {
//                for (int c = colFrom; c <= colTo; c++) {
//                    if (field[r][c] == value) {
//                        count++;
//                    }
//                }
//            }
//            // Subtract count for the field itself
//            count -= field[row][col] == value ? 1 : 0;
//        }
//        return count;
//    }
//
//    public Stream<Cell> getNeighborsStream(Cell cell, boolean onlyULDR) {
//        return getNeighborsStream(cell.row(), cell.col(), onlyULDR);
//    }
//
//    public Stream<Cell> getNeighborsStream(int row, int col, boolean onlyULDR) {
//        Stream<Cell> neighborsStream = onlyULDR ? Helpers.getNeighborsULRD(row, col) : Helpers.getNeighbors8(row, col);
//        return neighborsStream
//                .filter(this::isWithinField);
//    }

    public int getNextCellIdxNoCheck(int cellIdx, Direction direction) {
        return getNextCellIdxNoCheck(cellIdx, direction.ordinal());
    }

    public int getNextCellIdxNoCheck(int cellIdx, int directionOrdinal) {
        return getNextCellIdxNoCheck(cellIdx, directionOrdinal, 1);
    }

    public int getNextCellIdxNoCheck(int cellIdx, Direction direction, int numSteps) {
        return getNextCellIdxNoCheck(cellIdx, direction.ordinal(), numSteps);
    }

    public int getNextCellIdxNoCheck(int cellIdx, int directionOrdinal, int numSteps) {
        return switch (directionOrdinal) {
            case 0 -> cellIdx - (numSteps * stride);
            case 1 -> cellIdx - numSteps;
            case 2 -> cellIdx + (numSteps * stride);
            case 3 -> cellIdx + numSteps;
            default -> throw new IllegalStateException("Unexpected direction ordinal: " + directionOrdinal);
        };
    }

    public int getNextCellIdxWithinField(int cellIdx, Direction direction) {
        return getNextCellIdxWithinField(cellIdx, direction.ordinal());
    }

    public int getNextCellIdxWithinField(int cellIdx, int directionOrdinal) {
        // Optimize for a single step
        int newIdx = switch (directionOrdinal) {
            case 0 -> cellIdx - stride; // Boundary check will be done later
            case 1 -> cellIdx % stride != 0 ? cellIdx - 1 : -1;
            case 2 -> cellIdx + stride; // Boundary check will be done later
            case 3 -> cellIdx % stride != stride - 1 ? cellIdx + 1 : -1;
            default -> -1;
        };
        return isWithinField(newIdx) ? newIdx : -1;
    }

    public int getNextCellIdxWithinField(int cellIdx, Direction direction, int numSteps) {
        return getNextCellIdxWithinField(cellIdx, direction.ordinal(), numSteps);
    }

    public int getNextCellIdxWithinField(int cellIdx, int directionOrdinal, int numSteps) {
        if (directionOrdinal == 1 /* Direction.L.ordinal() */ && ((cellIdx % stride) - numSteps < 0)) {
            return -1;
        }
        if (directionOrdinal == 3 /* Direction.R.ordinal() */ && ((cellIdx % stride) + numSteps >= stride)) {
            return -1;
        }
        int newIdx = getNextCellIdxNoCheck(cellIdx, directionOrdinal, numSteps);
        if (!isWithinField(newIdx)) {
            return -1;
        }
        return newIdx;
    }
}
