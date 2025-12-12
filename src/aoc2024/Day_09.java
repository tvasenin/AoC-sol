package aoc2024;

import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Day_09 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2024/09-test.txt"), 1928, 2858);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2024/09-main.txt"), 6340197768906L, 6363913128533L);


    private static long getChecksum1(int numBlocks, int[] fileSizes, int[] filePositions) {
        int numFiles = fileSizes.length;

        int[] list = new int[numBlocks];

        int freeSpaceStart = 0;
        for (int fileId = 0; fileId < numFiles; fileId++) {
            int fileSize = fileSizes[fileId];
            int filePos = filePositions[fileId];
            Arrays.fill(list, freeSpaceStart, filePos, -1);
            freeSpaceStart = filePos + fileSize;
            Arrays.fill(list, filePos, freeSpaceStart, fileId);
        }
        Arrays.fill(list, freeSpaceStart, list.length, -1);

        long checksum = 0;
        int idxTarget = 0;
        int idxSource = list.length - 1;
        while (true) {
            while (idxTarget <= idxSource && list[idxTarget] != -1) {
                checksum += (long) list[idxTarget] * idxTarget;
                idxTarget++;
            }
            while (idxSource > 0 && list[idxSource] == -1) {
                idxSource--;
            }
            if (idxTarget < idxSource) {
                // Update result without updating field
                checksum += (long) list[idxSource] * idxTarget;
                idxSource--;
                idxTarget++;
            } else {
                return checksum;
            }
        }
    }

    public static TaskSolution solve(String input) {
        int numFiles = (input.length() + 1) / 2; // account for possible odd length
        int[] arr = new int[numFiles * 2];
        AtomicInteger idxAtomic = new AtomicInteger(-1);
        input.chars().forEach(c -> arr[idxAtomic.incrementAndGet()] = c - '0');

        int numBlocks = Arrays.stream(arr).sum();

        int[] fileSizes = new int[numFiles];
        int[] filePositions = new int[numFiles];
        int[] freeSpaceSizes = new int[numFiles];
        int[] freeSpacePositions = new int[numFiles];

        int pos = 0;
        int numFreeSpaceRecords = 0;
        for (int fileId = 0; fileId < numFiles; fileId++) {
            int fileSize = arr[fileId * 2];
            int freeSize = arr[fileId * 2 + 1];
            fileSizes[fileId] = fileSize;
            filePositions[fileId] = pos;
            pos += fileSize;
            if (freeSize > 0) {
                freeSpaceSizes[numFreeSpaceRecords] = freeSize;
                freeSpacePositions[numFreeSpaceRecords] = pos;
                numFreeSpaceRecords++;
            }
            pos += freeSize;
        }

        long result1 = getChecksum1(numBlocks, fileSizes, filePositions);

        // We can skip fileId 0, as it's always at the beginning
        for (int id = fileSizes.length - 1; id > 0; id--) {
            int fileSize = fileSizes[id];
            int filePos = filePositions[id];
            // Find first big enough free block and move file
            for (int i = 0; i < numFreeSpaceRecords; i++) {
                int freePos = freeSpacePositions[i];
                if (freePos > filePos) {
                    break;
                }
                int freeSize = freeSpaceSizes[i];
                if (fileSize <= freeSize) {
                    // Update file position
                    filePositions[id] = freePos;
                    // Mark target blocks as used
                    // Do not care about updating free space at old file placement (and around)
                    freeSpacePositions[i] = freePos + fileSize;
                    freeSpaceSizes[i] = freeSize - fileSize;
                    break;
                }
            }
        }

        long result2 = 0;
        for (int id = 0; id < fileSizes.length; id++) {
            int size = fileSizes[id];
            int idxFrom = filePositions[id];
            int idxTo = idxFrom + size;
            int sumConsecutive = (idxTo * (idxTo - 1) - idxFrom * (idxFrom - 1)) / 2;
            result2 += (long) sumConsecutive * id;
        }

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Helpers.runTask(TEST, Day_09::solve, true);
        Helpers.runTask(MAIN, Day_09::solve);
    }
}
