package sportsanalytics.co5;

import java.util.function.ToIntFunction;

public class CountingSort {
    /**
     * Sorts primitive integer arrays using counting sort.
     */
    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) return;
        int min = arr[0];
        int max = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
            if (val > max) max = val;
        }

        // Handle possible overflow or extremely large range
        long rangeTest = (long) max - min + 1;
        if (rangeTest > 10000000) {
            throw new IllegalArgumentException("Key range too large for Counting Sort (" + rangeTest + ")");
        }

        int range = (int) rangeTest;
        int[] count = new int[range];
        int[] output = new int[arr.length];

        for (int val : arr) {
            count[val - min]++;
        }

        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            output[count[arr[i] - min] - 1] = arr[i];
            count[arr[i] - min]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    /**
     * Sorts an array of objects based on an integer key extracted via keyExtractor.
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(T[] arr, ToIntFunction<? super T> keyExtractor) {
        if (arr == null || arr.length < 2) return;
        int min = keyExtractor.applyAsInt(arr[0]);
        int max = min;
        for (T val : arr) {
            int key = keyExtractor.applyAsInt(val);
            if (key < min) min = key;
            if (key > max) max = key;
        }

        long rangeTest = (long) max - min + 1;
        if (rangeTest > 10000000) {
            throw new IllegalArgumentException("Key range too large for Counting Sort (" + rangeTest + ")");
        }

        int range = (int) rangeTest;
        int[] count = new int[range];
        T[] output = (T[]) new Object[arr.length];

        for (T val : arr) {
            count[keyExtractor.applyAsInt(val) - min]++;
        }

        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            int key = keyExtractor.applyAsInt(arr[i]);
            output[count[key - min] - 1] = arr[i];
            count[key - min]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }
}
