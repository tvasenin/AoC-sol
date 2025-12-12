package aoc2024;

import util.CharField;
import util.Resources;

public class Day_04 {

    private static final String INPUT_TEST = Resources.getResourceAsString("aoc2024/04-test.txt");
    private static final String INPUT = Resources.getResourceAsString("aoc2024/04-main.txt");


    private static boolean isXmas(CharField field, int r1, int c1, int dRow, int dCol) {
        int r2 = r1 + dRow, c2 = c1 + dCol;
        int r3 = r2 + dRow, c3 = c2 + dCol;
        int r4 = r3 + dRow, c4 = c3 + dCol;
        boolean inBounds = 0 <= r4 && r4 <= field.numRows - 1 && 0 <= c4 && c4 <= field.numCols - 1;
        return inBounds && field.get(r1, c1) == 'X' && field.get(r2, c2) == 'M' && field.get(r3, c3) == 'A' && field.get(r4, c4) == 'S';
    }


    private static long getNumXmas(CharField field, int row, int col) {
        int cnt = 0;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                cnt += isXmas(field, row, col, dRow, dCol) ? 1 : 0;
            }
        }
        return cnt;
    }

    private static boolean isXmas2(CharField field, int row, int col) {
        boolean inBounds = 0 < row && row < field.numRows - 1 && 0 < col && col < field.numCols - 1;
        if (!inBounds) {
            return false;
        }
        char ul = field.get(row - 1, col - 1);
        char ur = field.get(row - 1, col + 1);
        char dl = field.get(row + 1, col - 1);
        char dr = field.get(row + 1, col + 1);
        String diag1 = String.valueOf(ul) + field.get(row, col) + dr;
        String diag2 = String.valueOf(ur) + field.get(row, col) + dl;
        boolean ok1 = diag1.equals("MAS") || diag1.equals("SAM");
        boolean ok2 = diag2.equals("MAS") || diag2.equals("SAM");
        return ok1 && ok2;
    }

    public static void main(String[] args) {
        boolean isTest = false;
        //noinspection ConstantValue
        String input = isTest ? INPUT_TEST : INPUT;

        long result1 = 0, result2 = 0;

        CharField field = CharField.of(input);

        for (int r = 0; r < field.numRows; r++) {
            for (int c = 0; c < field.numCols; c++) {
                result1 += getNumXmas(field, r, c);
                result2 += isXmas2(field, r, c) ? 1 : 0;
            }
        }

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (result1 != 2336) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 1831) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}
