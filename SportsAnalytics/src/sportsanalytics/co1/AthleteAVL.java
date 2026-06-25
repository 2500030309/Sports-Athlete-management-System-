package sportsanalytics.co1;

import sportsanalytics.model.Athlete;
import java.util.ArrayList;
import java.util.List;

public class AthleteAVL {
    public static class Node {
        public Athlete athlete;
        public int height;
        public Node left, right;

        public Node(Athlete athlete) {
            this.athlete = athlete;
            this.height = 1;
        }
    }

    private Node root;

    public Node getRoot() {
        return root;
    }

    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    private int getBalance(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    public void insert(Athlete athlete) {
        root = insertRec(root, athlete);
    }

    private int compare(Athlete a1, Athlete a2) {
        if (a1.getPerformanceRating() != a2.getPerformanceRating()) {
            return Double.compare(a1.getPerformanceRating(), a2.getPerformanceRating());
        }
        return Integer.compare(a1.getAthleteId(), a2.getAthleteId());
    }

    private Node insertRec(Node node, Athlete athlete) {
        if (node == null) {
            return new Node(athlete);
        }

        int comp = compare(athlete, node.athlete);
        if (comp < 0) {
            node.left = insertRec(node.left, athlete);
        } else if (comp > 0) {
            node.right = insertRec(node.right, athlete);
        } else {
            // Duplicate ID and rating
            return node;
        }

        // Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor of this ancestor node to check if it became unbalanced
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && compare(athlete, node.left.athlete) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && compare(athlete, node.right.athlete) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && compare(athlete, node.left.athlete) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && compare(athlete, node.right.athlete) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void delete(Athlete athlete) {
        root = deleteRec(root, athlete);
    }

    private Node deleteRec(Node root, Athlete athlete) {
        if (root == null) return null;

        int comp = compare(athlete, root.athlete);
        if (comp < 0) {
            root.left = deleteRec(root.left, athlete);
        } else if (comp > 0) {
            root.right = deleteRec(root.right, athlete);
        } else {
            // Node with only one child or no child
            if ((root.left == null) || (root.right == null)) {
                Node temp = root.left != null ? root.left : root.right;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp; // Copy the contents of the non-empty child
                }
            } else {
                // Node with two children: Get the inorder successor
                Node temp = minValueNode(root.right);

                // Copy the inorder successor's data to this node
                root.athlete = temp.athlete;

                // Delete the inorder successor
                root.right = deleteRec(root.right, temp.athlete);
            }
        }

        if (root == null) return null;

        // Update height
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // Balance Factor
        int balance = getBalance(root);

        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0) {
            return rightRotate(root);
        }

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0) {
            return leftRotate(root);
        }

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public List<Athlete> getLeaderboard() {
        List<Athlete> list = new ArrayList<>();
        reverseInOrder(root, list); // Descending order (Right, Node, Left)
        return list;
    }

    private void reverseInOrder(Node node, List<Athlete> list) {
        if (node != null) {
            reverseInOrder(node.right, list);
            list.add(node.athlete);
            reverseInOrder(node.left, list);
        }
    }

    public void clear() {
        root = null;
    }
}
