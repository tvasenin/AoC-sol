package util;

public class GridWalker {
    private int row, col;
    private Direction direction;

    public GridWalker(Cell startCell, Direction direction) {
        this(startCell.row(), startCell.col(), direction);
    }

    public GridWalker(int startRow, int startCol, Direction direction) {
        this.row = startRow;
        this.col = startCol;
        this.direction = direction;
    }

    public Cell getCell() {
        return new Cell(row, col);
    }

    public Direction getDirection() {
        return direction;
    }

    public void moveForward(int numSteps) {
        switch (direction) {
            case U -> row -= numSteps;
            case L -> col -= numSteps;
            case D -> row += numSteps;
            case R -> col += numSteps;
        };
    }

    public void rotateCW() {
        direction = direction.rotateCW();
    }

    public void rotateCCW() {
        direction = direction.rotateCCW();
    }
}
