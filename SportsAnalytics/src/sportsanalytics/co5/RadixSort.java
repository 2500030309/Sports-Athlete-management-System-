package sportsanalytics.co5;

import java.util.function.ToIntFunction;

public class RadixSort {
    /**
     * Sorts primitive integer arrays using radix sort (LSD approach).
     * Automatically handles negative values by shifting range to positive.
     */
    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) return;

        // Shift values to positive range if there are negative numbers
        int min = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
        }

        if (min < 0) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] -= min;
            }
        }

        // Find the maximum value to know the number of digits
        int max = arr[0];
        for (int val : arr) {
            if (val > max) max = val;
        }

        // Do counting sort for every digit.
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countSort(arr, exp);
        }

        // Shift back to original range if we shifted earlier
        if (min < 0) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] += min;
            }
        }
    }

    private static void countSort(int[] arr, int exp) {
        int[] output = new int[arr.length];
        int[] count = new int[10];

        for (int val : arr) {
            count[(val / exp) % 10]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    /**
     * Sorts an object array using radix sort based on integer keys.
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(T[] arr, ToIntFunction<? super T> keyExtractor) {
        if (arr == null || arr.length < 2) return;

        int min = keyExtractor.applyAsInt(arr[0]);
        for (T val : arr) {
            int key = keyExtractor.applyAsInt(val);
            if (key < min) min = key;
        }

        // Extract shifted keys (all >= 0)
        int[] tempKeys = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            tempKeys[i] = keyExtractor.applyAsInt(arr[i]) - min;
        }

        int max = tempKeys[0];
        for (int val : tempKeys) {
            if (val > max) max = val;
        }

        // Index mapping to sort objects stably
        int[] indices = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            indices[i] = i;
        }

        for (int exp = 1; max / exp > 0; exp *= 10) {
            indices = countSortIndices(tempKeys, indices, exp);
        }

        // Re-arrange the original object array based on sorted indices
        T[] output = (T[]) new Object[arr.length];
        for (int i = 0; i < arr.length; i++) {
            output[i] = arr[indices[i]];
        }
        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    private static int[] countSortIndices(int[] tempKeys, int[] indices, int exp) {
        int[] outputIndices = new int[indices.length];
        int[] count = new int[10];

        for (int idx : indices) {
            int digit = (tempKeys[idx] / exp) % 10;
            count[digit]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = indices.length - 1; i >= 0; i--) {
            int idx = indices[i];
            int digit = (tempKeys[idx] / exp) % 10;
            outputIndices[count[digit] - 1] = idx;
            count[digit]--;
        }
        return outputIndices;
    }
}
