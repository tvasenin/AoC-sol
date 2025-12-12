package util;

import java.util.Comparator;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class ArrayTools {

    public static int indexOfTheMaxByStream(int[] array) {
        return indexOfTheMaxByStream(array, 0, array.length);
    }

    public static int indexOfTheMaxByStream(int[] array, int idxFrom, int idxTo) {
        return IntStream.range(idxFrom, idxTo)
                .boxed()
                .max(Comparator.comparingInt(i -> array[i]))
                .orElse(-1);
    }
}
