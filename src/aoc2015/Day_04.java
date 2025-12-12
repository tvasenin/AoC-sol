package aoc2015;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day_04 {

    private static final TaskData TEST_1 = new TaskData(Resources.getResourceAsString("aoc2015/04-test-1.txt"), 609043, 6742839);
    private static final TaskData TEST_2 = new TaskData(Resources.getResourceAsString("aoc2015/04-test-2.txt"), 1048970, 5714438);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2015/04-main.txt"), 254575, 1038736);


    private static final MessageDigest MD5;
    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final byte[] DIGIT_ONES = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    private static final byte[] DIGIT_TENS = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    } ;
    private static int stringSize(int x) {
        int d = 1;
        if (x >= 0) {
            d = 0;
            x = -x;
        }
        int p = -10;
        for (int i = 1; i < 10; i++) {
            if (x > p)
                return i + d;
            p = 10 * p;
        }
        return 10 + d;
    }
    static void getStringBytes(int i, byte[] buf) {
        int index = buf.length;

        int q, r;
        int charPos = index;

        boolean negative = i < 0;
        if (!negative) {
            i = -i;
        }

        // Generate two digits per iteration
        while (i <= -100) {
            q = i / 100;
            r = (q * 100) - i;
            i = q;
            buf[--charPos] = DIGIT_ONES[r];
            buf[--charPos] = DIGIT_TENS[r];
        }

        // We know there are at most two digits left at this point.
        buf[--charPos] = DIGIT_ONES[-i];
        if (i < -9) {
            buf[--charPos] = DIGIT_TENS[-i];
        }

        if (negative) {
            buf[--charPos] = (byte)'-';
        }
    }

    public static TaskSolution solve(String input) {
        long result1 = -1, result2 = -1;
        int cnt = 0;
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] cntBytes = null;
        while (result1 == -1 || result2 == -1) {
            //byte[] cntBytes = String.valueOf(cnt).getBytes(StandardCharsets.UTF_8);
            int size = stringSize(cnt);
            // Reuse existing buffer if possible
            if (cntBytes == null || cntBytes.length < size) {
                cntBytes = new byte[size];
            }
            getStringBytes(cnt, cntBytes);
            // Update and get digest
            MD5.update(inputBytes);
            byte[] md5 = MD5.digest(cntBytes);
            // Check first 4 hexadecimal zeros
            if (md5[0] == 0 && md5[1] == 0) {
                // Check 5-th zero
                if ((md5[2] & 0xF0) == 0 && result1 == -1) {
                    result1 = cnt;
                }
                // Check 5-th and 6-th zeros
                if (md5[2] == 0 && result2 == -1) {
                    result2 = cnt;
                }
            }
            cnt++;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // A few nice examples
        if (!solve("K").equals(TaskSolution.of(77732, 77732))) { throw new AssertionError(); }
        if (!solve("`").equals(TaskSolution.of(20159, 20159))) { throw new AssertionError(); }
        if (!solve("Ƴ").equals(TaskSolution.of(1463, 1463))) { throw new AssertionError(); }

        Helpers.runTask(TEST_1, Day_04::solve);
        Helpers.runTask(TEST_2, Day_04::solve);
        Helpers.runTask(MAIN, Day_04::solve);
    }
}
