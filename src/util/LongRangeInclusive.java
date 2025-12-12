package util;

public record LongRangeInclusive(long from, long to) {

    public LongRangeInclusive {
        if (to <= from) {
            throw new IllegalArgumentException();
        }
    }

    public static LongRangeInclusive of(long from, long to) {
        return new LongRangeInclusive(from, to);
    }

    public boolean isOverlappedBy(final LongRangeInclusive otherRange) {
        if (otherRange == null) {
            return false;
        }
        boolean isOutside = (otherRange.to <= from) || (otherRange.from >= to);
        return !isOutside;
    }

    public LongRangeInclusive intersectionWith(final LongRangeInclusive other) {
        if (!this.isOverlappedBy(other)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot calculate intersection with non-overlapping range %s", other));
        }
        if (this.equals(other)) {
            return this;
        }
        long min = Math.max(from, other.from);
        long max = Math.min(to, other.to);
        return of(min, max);
    }

    public LongRangeInclusive shiftBy(long shift) {
        return shift == 0 ? this : new LongRangeInclusive(from + shift, to + shift);
    }
}
