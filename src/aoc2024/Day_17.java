package aoc2024;

import org.apache.commons.lang3.StringUtils;
import util.Helpers;
import util.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Day_17 {

    private static final String INPUT = Resources.getResourceAsString("aoc2024/17-main.txt");


    @SuppressWarnings("ParameterCanBeLocal")
    private static List<Integer> emulate1(long A, long B, long C) {
        List<Integer> outs = new ArrayList<>();
        //:start
        do {
            // bst A // 2,4
            B = A % 8;
            // bxl 1 // 1,1
            B = B ^ 1;
            // cdv B // 7,5
            C = A >> B;
            // bxl 4 // 1,4
            B = B ^ 4;
            // adv 3 // 0,3
            A = A >> 3;
            // bxc   // 4,5
            B = B ^ C;
            // out B // 5,5
            outs.add((int) (B % 8));
        } while (A != 0);
        //jnz :start // 3,0
        return outs;
    }

    private static long emulate2(int[] program) {
        long A = 1;
        for (int idx = program.length - 1; idx >= 0 ; idx--) {
            A = findMinOutput(A, program, idx);
            A = A << 3;
        }
        return A >> 3;
    }

    private static long findMinOutput(long minA, int[] program, int idxProg) {
        do {
            long A = minA;
            long B, C;
            int idx = idxProg;
            do {
                B = A % 8;
                B = B ^ 1;
                C = A >> B;
                B = B ^ 4;
                A = A >> 3;
                B = B ^ C;
                int out = (int) (B % 8);
                if (out != program[idx]) {
                    break;
                }
                idx++;
            } while (A != 0);
            if (idx == program.length) {
                return minA;
            }
            minA++;
        } while (true);
    }

    @SuppressWarnings("unused")
    private static String emulateTest1(int A, int B, int C) {
        List<Integer> outs = new ArrayList<>();
        //:start
        do {
            // adv 1 // 0, 1
            A = A / (1 << 1);
            // out A // 5, 4
            outs.add(A % 8);
        } while (A != 0);
        //jnz :start
        return StringUtils.join(outs, ",");
    }


    public static void main(String[] args) {
        String input = INPUT;

        Instant start = Instant.now();

        String[] s0 = StringUtils.split(input, " \n");
        int startA = Integer.parseInt(s0[2]);
        int startB = Integer.parseInt(s0[5]);
        int startC = Integer.parseInt(s0[8]);

        String result1 = StringUtils.join(emulate1(startA, startB, startC), ",");
        int[] program = Helpers.parseIntArray(s0[10], ",");

        long result2 = emulate2(program);

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Time: " + timeElapsed + " ms");

        //noinspection ConstantValue
        if (input.equals(INPUT)) {
            if (!result1.equals("5,1,4,0,5,1,0,2,6")) {
                System.out.println("Wrong Result 1");
            }
            if (result2 != 202322936867370L) {
                System.out.println("Wrong Result 2");
            }
        }
        System.out.println("Result (Part 1): " + result1);
        System.out.println("Result (Part 2): " + result2);
    }
}
