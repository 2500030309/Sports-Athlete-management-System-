package sportsanalytics.co5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SorterBenchmark {
    /**
     * Runs benchmarking for the 5 sorting algorithms and standard java sort.
     * Returns a map with times in milliseconds.
     */
    public static Map<String, Double> runBenchmark(int size) {
        int[] originalInts = new int[size];
        Random rand = new Random(42); // fixed seed for reproducibility
        for (int i = 0; i < size; i++) {
            originalInts[i] = rand.nextInt(100000); // elements in range [0, 99999]
        }

        Map<String, Double> results = new HashMap<>();

        // 1. Merge Sort
        int[] mergeInput = originalInts.clone();
        Integer[] mergeBoxed = box(mergeInput);
        long start = System.nanoTime();
        MergeSort.sort(mergeBoxed, Integer::compareTo);
        long end = System.nanoTime();
        results.put("Merge Sort", (end - start) / 1e6); // convert to ms

        // 2. Quick Sort
        int[] quickInput = originalInts.clone();
        Integer[] quickBoxed = box(quickInput);
        start = System.nanoTime();
        QuickSort.sort(quickBoxed, Integer::compareTo);
        end = System.nanoTime();
        results.put("Quick Sort", (end - start) / 1e6);

        // 3. Heap Sort
        int[] heapInput = originalInts.clone();
        Integer[] heapBoxed = box(heapInput);
        start = System.nanoTime();
        HeapSort.sort(heapBoxed, Integer::compareTo);
        end = System.nanoTime();
        results.put("Heap Sort", (end - start) / 1e6);

        // 4. Counting Sort
        int[] countingInput = originalInts.clone();
        start = System.nanoTime();
        CountingSort.sort(countingInput);
        end = System.nanoTime();
        results.put("Counting Sort", (end - start) / 1e6);

        // 5. Radix Sort
        int[] radixInput = originalInts.clone();
        start = System.nanoTime();
        RadixSort.sort(radixInput);
        end = System.nanoTime();
        results.put("Radix Sort", (end - start) / 1e6);

        // 6. Java Arrays.sort (dual-pivot quicksort / timsort)
        int[] systemInput = originalInts.clone();
        start = System.nanoTime();
        Arrays.sort(systemInput);
        end = System.nanoTime();
        results.put("Built-in Sort", (end - start) / 1e6);

        return results;
    }

    private static Integer[] box(int[] arr) {
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            boxed[i] = arr[i];
        }
        return boxed;
    }
}
