package util;

import org.eclipse.collections.api.block.comparator.primitive.IntComparator;
import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.collector.Collectors2;

import java.util.Arrays;

public class CollectionUtils {

    public static MutableIntList parseToMutableIntList(String input, String delimiterPattern) {
        return Arrays.stream(input.split(delimiterPattern)).collect(Collectors2.collectInt(Integer::parseInt, IntLists.mutable::empty));
    }

    public static MutableIntSet parseToMutableIntSet(String input, String delimiterPattern) {
        return Arrays.stream(input.split(delimiterPattern)).collect(Collectors2.collectInt(Integer::parseInt, IntSets.mutable::empty));
    }

    public static boolean isSorted(final IntList list, final IntComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator should not be null.");
        }

        if (list == null || list.size() < 2) {
            return true;
        }

        int previous = list.get(0);
        final int n = list.size();
        for (int i = 1; i < n; i++) {
            final int current = list.get(i);
            if (comparator.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
}
