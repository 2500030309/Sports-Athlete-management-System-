package sportsanalytics.co5;

import java.util.Comparator;

public class MergeSort {
    /**
     * Sorts the array using the merge sort algorithm.
     */
    public static <T> void sort(T[] arr, Comparator<? super T> comp) {
        if (arr == null || arr.length < 2)
            return;
        mergeSort(arr, 0, arr.length - 1, comp);
    }

    private static <T> void mergeSort(T[] arr, int left, int right, Comparator<? super T> comp) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid, comp);
            mergeSort(arr, mid + 1, right, comp);
            merge(arr, left, mid, right, comp);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void merge(T[] arr, int left, int mid, int right, Comparator<? super T> comp) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        T[] L = (T[]) new Object[n1];
        T[] R = (T[]) new Object[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (comp.compare(L[i], R[j]) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) {
            arr[k++] = L[i++];
        }
        while (j < n2) {
            arr[k++] = R[j++];
        }
    }
}
