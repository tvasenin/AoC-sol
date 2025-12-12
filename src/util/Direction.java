package util;

public enum Direction {
    U, L, D, R;

    public Direction reverse() {
        return switch (this) {
            case U -> D;
            case L -> R;
            case D -> U;
            case R -> L;
        };
    }

    public Direction rotateCW() {
        return switch (this) {
            case U -> R;
            case L -> U;
            case D -> L;
            case R -> D;
        };
    }

    public Direction rotateCCW() {
        return switch (this) {
            case U -> L;
            case L -> D;
            case D -> R;
            case R -> U;
        };
    }
}
