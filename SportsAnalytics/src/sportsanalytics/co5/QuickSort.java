package sportsanalytics.co5;

import java.util.Comparator;

public class QuickSort {
    /**
     * Sorts the array using the quick sort algorithm.
     */
    public static <T> void sort(T[] arr, Comparator<? super T> comp) {
        if (arr == null || arr.length < 2) return;
        quickSort(arr, 0, arr.length - 1, comp);
    }

    private static <T> void quickSort(T[] arr, int low, int high, Comparator<? super T> comp) {
        if (low < high) {
            int pi = partition(arr, low, high, comp);
            quickSort(arr, low, pi - 1, comp);
            quickSort(arr, pi + 1, high, comp);
        }
    }

    private static <T> int partition(T[] arr, int low, int high, Comparator<? super T> comp) {
        // Median-of-three pivot selection
        int mid = low + (high - low) / 2;
        if (comp.compare(arr[mid], arr[low]) < 0) swap(arr, low, mid);
        if (comp.compare(arr[high], arr[low]) < 0) swap(arr, low, high);
        if (comp.compare(arr[high], arr[mid]) < 0) swap(arr, mid, high);

        swap(arr, mid, high); // Pivot is now at high
        T pivot = arr[high];

        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comp.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
