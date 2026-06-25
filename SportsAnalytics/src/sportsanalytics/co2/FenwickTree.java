package sportsanalytics.co2;

public class FenwickTree {
    private final int[] tree;
    private final int[] originalArray;

    public FenwickTree(int[] arr) {
        if (arr == null) {
            this.originalArray = new int[0];
            this.tree = new int[1];
        } else {
            this.originalArray = arr.clone();
            this.tree = new int[arr.length + 1];
            for (int i = 0; i < arr.length; i++) {
                add(i + 1, arr[i]);
            }
        }
    }

    public int[] getTree() {
        return tree;
    }

    public int[] getOriginalArray() {
        return originalArray;
    }

    // Adds val to element at 1-based index idx
    private void add(int idx, int val) {
        while (idx < tree.length) {
            tree[idx] += val;
            idx += idx & (-idx);
        }
    }

    // Updates element at 0-based index to newVal
    public void update(int index, int newVal) {
        if (index < 0 || index >= originalArray.length) return;
        int diff = newVal - originalArray[index];
        originalArray[index] = newVal;
        add(index + 1, diff);
    }

    // Prefix sum from index 0 to index (0-based)
    public int query(int index) {
        int sum = 0;
        int idx = index + 1;
        while (idx > 0) {
            sum += tree[idx];
            idx -= idx & (-idx);
        }
        return sum;
    }

    // Range sum query for range [L, R] (0-based indices)
    public int rangeQuery(int L, int R) {
        if (L < 0 || R >= originalArray.length || L > R) return 0;
        return query(R) - (L > 0 ? query(L - 1) : 0);
    }
}
