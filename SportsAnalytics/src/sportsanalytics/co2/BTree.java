package sportsanalytics.co2;

import java.util.ArrayList;
import java.util.List;

public class BTree {
    public static class BTreeNode {
        public int[] keys;
        public int t; // Minimum degree
        public BTreeNode[] children;
        public int n; // Current number of keys
        public boolean leaf;

        public BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new int[2 * t - 1];
            this.children = new BTreeNode[2 * t];
            this.n = 0;
        }

        public void traverse(List<Integer> list) {
            int i;
            for (i = 0; i < this.n; i++) {
                if (!this.leaf) {
                    children[i].traverse(list);
                }
                list.add(keys[i]);
            }
            if (!this.leaf) {
                children[i].traverse(list);
            }
        }

        public BTreeNode search(int k) {
            int i = 0;
            while (i < n && k > keys[i]) {
                i++;
            }
            if (i < n && keys[i] == k) {
                return this;
            }
            if (leaf) {
                return null;
            }
            return children[i].search(k);
        }

        public void insertNonFull(int k) {
            int i = n - 1;
            if (leaf) {
                while (i >= 0 && keys[i] > k) {
                    keys[i + 1] = keys[i];
                    i--;
                }
                keys[i + 1] = k;
                n = n + 1;
            } else {
                while (i >= 0 && keys[i] > k) {
                    i--;
                }
                if (children[i + 1].n == 2 * t - 1) {
                    splitChild(i + 1, children[i + 1]);
                    if (keys[i + 1] < k) {
                        i++;
                    }
                }
                children[i + 1].insertNonFull(k);
            }
        }

        public void splitChild(int i, BTreeNode y) {
            BTreeNode z = new BTreeNode(y.t, y.leaf);
            z.n = t - 1;
            for (int j = 0; j < t - 1; j++) {
                z.keys[j] = y.keys[j + t];
            }
            if (!y.leaf) {
                for (int j = 0; j < t; j++) {
                    z.children[j] = y.children[j + t];
                }
            }
            y.n = t - 1;
            for (int j = n; j >= i + 1; j--) {
                children[j + 1] = children[j];
            }
            children[i + 1] = z;
            for (int j = n - 1; j >= i; j--) {
                keys[j + 1] = keys[j];
            }
            keys[i] = y.keys[t - 1];
            n = n + 1;
        }
    }

    private BTreeNode root;
    private final int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    public BTreeNode getRoot() {
        return root;
    }

    public List<Integer> traverse() {
        List<Integer> list = new ArrayList<>();
        if (root != null) {
            root.traverse(list);
        }
        return list;
    }

    public boolean search(int k) {
        return root != null && root.search(k) != null;
    }

    public void insert(int k) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = k;
            root.n = 1;
        } else {
            if (root.n == 2 * t - 1) {
                BTreeNode s = new BTreeNode(t, false);
                s.children[0] = root;
                s.splitChild(0, root);
                int i = 0;
                if (s.keys[0] < k) {
                    i++;
                }
                s.children[i].insertNonFull(k);
                root = s;
            } else {
                root.insertNonFull(k);
            }
        }
    }

    public void clear() {
        root = null;
    }
}
