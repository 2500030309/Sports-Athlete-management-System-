package sportsanalytics.co2;

public class SegmentTree {
    public static class SegmentNode {
        public int start, end;
        public int sum;
        public int min;
        public int max;
        public SegmentNode left, right;

        public SegmentNode(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private SegmentNode root;
    private int[] data;

    public SegmentTree(int[] arr) {
        if (arr == null || arr.length == 0) {
            this.data = new int[1]; // default empty
        } else {
            this.data = arr.clone();
        }
        this.root = buildTree(0, this.data.length - 1);
    }

    public SegmentNode getRoot() {
        return root;
    }

    public int[] getData() {
        return data;
    }

    private SegmentNode buildTree(int start, int end) {
        SegmentNode node = new SegmentNode(start, end);
        if (start == end) {
            node.sum = data[start];
            node.min = data[start];
            node.max = data[start];
            return node;
        }

        int mid = start + (end - start) / 2;
        node.left = buildTree(start, mid);
        node.right = buildTree(mid + 1, end);

        node.sum = node.left.sum + node.right.sum;
        node.min = Math.min(node.left.min, node.right.min);
        node.max = Math.max(node.left.max, node.right.max);

        return node;
    }

    public void update(int index, int val) {
        if (index < 0 || index >= data.length) return;
        data[index] = val;
        updateRec(root, index, val);
    }

    private void updateRec(SegmentNode node, int index, int val) {
        if (node == null) return;
        
        if (node.start == node.end && node.start == index) {
            node.sum = val;
            node.min = val;
            node.max = val;
            return;
        }

        int mid = node.start + (node.end - node.start) / 2;
        if (index <= mid) {
            updateRec(node.left, index, val);
        } else {
            updateRec(node.right, index, val);
        }

        node.sum = node.left.sum + node.right.sum;
        node.min = Math.min(node.left.min, node.right.min);
        node.max = Math.max(node.left.max, node.right.max);
    }

    public int querySum(int L, int R) {
        return querySumRec(root, L, R);
    }

    private int querySumRec(SegmentNode node, int L, int R) {
        if (node == null || L > node.end || R < node.start) {
            return 0;
        }
        if (L <= node.start && R >= node.end) {
            return node.sum;
        }
        return querySumRec(node.left, L, R) + querySumRec(node.right, L, R);
    }

    public int queryMin(int L, int R) {
        return queryMinRec(root, L, R);
    }

    private int queryMinRec(SegmentNode node, int L, int R) {
        if (node == null || L > node.end || R < node.start) {
            return Integer.MAX_VALUE;
        }
        if (L <= node.start && R >= node.end) {
            return node.min;
        }
        return Math.min(queryMinRec(node.left, L, R), queryMinRec(node.right, L, R));
    }

    public int queryMax(int L, int R) {
        return queryMaxRec(root, L, R);
    }

    private int queryMaxRec(SegmentNode node, int L, int R) {
        if (node == null || L > node.end || R < node.start) {
            return Integer.MIN_VALUE;
        }
        if (L <= node.start && R >= node.end) {
            return node.max;
        }
        return Math.max(queryMaxRec(node.left, L, R), queryMaxRec(node.right, L, R));
    }
}
