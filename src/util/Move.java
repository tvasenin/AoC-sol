package util;

public record Move(Cell cell, Direction dir) {
    public int row() {
        return cell.row();
    }
    public int col() {
        return cell.col();
    }
}
