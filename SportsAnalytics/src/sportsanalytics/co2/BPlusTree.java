package sportsanalytics.co2;

import java.util.ArrayList;
import java.util.List;

public class BPlusTree {
    private static final int M = 4; 
    
    public static abstract class Node {
        public int numKeys;
        public int[] keys = new int[M];
        public Node parent;
    }

    public static class InternalNode extends Node {
        public Node[] children = new Node[M + 1];
    }

    public static class LeafNode extends Node {
        public LeafNode next;
        public LeafNode prev;
        
        public LeafNode() {
            this.next = null;
            this.prev = null;
        }
    }

    private Node root;
    private LeafNode firstLeaf;

    public BPlusTree() {
        this.root = new LeafNode();
        this.firstLeaf = (LeafNode) this.root;
    }

    public Node getRoot() {
        return root;
    }

    public LeafNode getFirstLeaf() {
        return firstLeaf;
    }

    public boolean search(int key) {
        LeafNode leaf = findLeafNode(key);
        for (int i = 0; i < leaf.numKeys; i++) {
            if (leaf.keys[i] == key) return true;
        }
        return false;
    }

    public List<Integer> rangeQuery(int lower, int upper) {
        List<Integer> result = new ArrayList<>();
        LeafNode leaf = findLeafNode(lower);
        while (leaf != null) {
            for (int i = 0; i < leaf.numKeys; i++) {
                int key = leaf.keys[i];
                if (key >= lower && key <= upper) {
                    result.add(key);
                } else if (key > upper) {
                    return result;
                }
            }
            leaf = leaf.next;
        }
        return result;
    }

    private LeafNode findLeafNode(int key) {
        Node node = root;
        while (node instanceof InternalNode) {
            InternalNode internal = (InternalNode) node;
            int i = 0;
            while (i < internal.numKeys && key >= internal.keys[i]) {
                i++;
            }
            node = internal.children[i];
        }
        return (LeafNode) node;
    }

    public void insert(int key) {
        LeafNode leaf = findLeafNode(key);
        
        // Insert key in sorted order, preserving duplicates
        int i = leaf.numKeys - 1;
        while (i >= 0 && leaf.keys[i] > key) {
            leaf.keys[i + 1] = leaf.keys[i];
            i--;
        }
        leaf.keys[i + 1] = key;
        leaf.numKeys++;

        if (leaf.numKeys == M) {
            // Split leaf node
            LeafNode sibling = new LeafNode();
            int splitIndex = M / 2;
            
            // Move half elements to sibling
            for (int j = splitIndex; j < M; j++) {
                sibling.keys[j - splitIndex] = leaf.keys[j];
                sibling.numKeys++;
            }
            leaf.numKeys = splitIndex;

            // Link sibling leaf nodes
            sibling.next = leaf.next;
            if (leaf.next != null) {
                leaf.next.prev = sibling;
            }
            leaf.next = sibling;
            sibling.prev = leaf;

            // Insert parent
            insertInParent(leaf, sibling.keys[0], sibling);
        }
    }

    private void insertInParent(Node left, int key, Node right) {
        if (left == root) {
            InternalNode newRoot = new InternalNode();
            newRoot.keys[0] = key;
            newRoot.children[0] = left;
            newRoot.children[1] = right;
            newRoot.numKeys = 1;
            root = newRoot;
            left.parent = root;
            right.parent = root;
            return;
        }

        InternalNode parent = (InternalNode) left.parent;
        int index = 0;
        while (index < parent.numKeys && parent.children[index] != left) {
            index++;
        }

        // Shift elements
        for (int i = parent.numKeys; i > index; i--) {
            parent.children[i + 1] = parent.children[i];
        }
        for (int i = parent.numKeys - 1; i >= index; i--) {
            parent.keys[i + 1] = parent.keys[i];
        }

        parent.keys[index] = key;
        parent.children[index + 1] = right;
        parent.numKeys++;
        right.parent = parent;

        if (parent.numKeys == M) {
            // Split internal node
            InternalNode sibling = new InternalNode();
            int splitIndex = M / 2;
            int parentKey = parent.keys[splitIndex];

            // Move elements to sibling
            for (int j = splitIndex + 1; j < M; j++) {
                sibling.keys[j - (splitIndex + 1)] = parent.keys[j];
                sibling.numKeys++;
            }
            for (int j = splitIndex + 1; j <= M; j++) {
                sibling.children[j - (splitIndex + 1)] = parent.children[j];
                sibling.children[j - (splitIndex + 1)].parent = sibling;
            }
            parent.numKeys = splitIndex;

            insertInParent(parent, parentKey, sibling);
        }
    }

    public void clear() {
        this.root = new LeafNode();
        this.firstLeaf = (LeafNode) this.root;
    }
}
