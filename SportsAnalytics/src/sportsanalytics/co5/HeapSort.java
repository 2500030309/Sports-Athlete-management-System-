package sportsanalytics.co5;

import java.util.Comparator;

public class HeapSort {
    /**
     * Sorts the array using the heap sort algorithm.
     */
    public static <T> void sort(T[] arr, Comparator<? super T> comp) {
        if (arr == null || arr.length < 2) return;
        int n = arr.length;

        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, comp);
        }

        // One by one extract an element from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            swap(arr, 0, i);

            // call max heapify on the reduced heap
            heapify(arr, i, 0, comp);
        }
    }

    private static <T> void heapify(T[] arr, int n, int i, Comparator<? super T> comp) {
        int largest = i; // Initialize largest as root
        int l = 2 * i + 1; // left = 2*i + 1
        int r = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (l < n && comp.compare(arr[l], arr[largest]) > 0) {
            largest = l;
        }

        // If right child is larger than largest so far
        if (r < n && comp.compare(arr[r], arr[largest]) > 0) {
            largest = r;
        }

        // If largest is not root
        if (largest != i) {
            swap(arr, i, largest);

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest, comp);
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
