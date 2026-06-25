package sportsanalytics.co1;

import sportsanalytics.model.Athlete;
import java.util.ArrayList;
import java.util.List;

public class AthleteBST {
    public static class Node {
        public Athlete athlete;
        public Node left, right;

        public Node(Athlete athlete) {
            this.athlete = athlete;
        }
    }

    private Node root;

    public Node getRoot() {
        return root;
    }

    public void insert(Athlete athlete) {
        root = insertRec(root, athlete);
    }

    private Node insertRec(Node root, Athlete athlete) {
        if (root == null) {
            return new Node(athlete);
        }
        if (athlete.getAthleteId() < root.athlete.getAthleteId()) {
            root.left = insertRec(root.left, athlete);
        } else if (athlete.getAthleteId() > root.athlete.getAthleteId()) {
            root.right = insertRec(root.right, athlete);
        } else {
            // Already exists, update it
            root.athlete = athlete;
        }
        return root;
    }

    public void delete(int athleteId) {
        root = deleteRec(root, athleteId);
    }

    private Node deleteRec(Node root, int athleteId) {
        if (root == null) return null;

        if (athleteId < root.athlete.getAthleteId()) {
            root.left = deleteRec(root.left, athleteId);
        } else if (athleteId > root.athlete.getAthleteId()) {
            root.right = deleteRec(root.right, athleteId);
        } else {
            // Node with only one child or no child
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Node with two children: Get the inorder successor (smallest in the right subtree)
            root.athlete = minValue(root.right);

            // Delete the inorder successor
            root.right = deleteRec(root.right, root.athlete.getAthleteId());
        }
        return root;
    }

    private Athlete minValue(Node root) {
        Athlete minv = root.athlete;
        while (root.left != null) {
            minv = root.left.athlete;
            root = root.left;
        }
        return minv;
    }

    public Athlete search(int athleteId) {
        Node result = searchRec(root, athleteId);
        return result != null ? result.athlete : null;
    }

    private Node searchRec(Node root, int athleteId) {
        if (root == null || root.athlete.getAthleteId() == athleteId) {
            return root;
        }
        if (athleteId < root.athlete.getAthleteId()) {
            return searchRec(root.left, athleteId);
        }
        return searchRec(root.right, athleteId);
    }

    public List<Athlete> getInOrder() {
        List<Athlete> list = new ArrayList<>();
        inOrderRec(root, list);
        return list;
    }

    private void inOrderRec(Node node, List<Athlete> list) {
        if (node != null) {
            inOrderRec(node.left, list);
            list.add(node.athlete);
            inOrderRec(node.right, list);
        }
    }

    public List<Athlete> getPreOrder() {
        List<Athlete> list = new ArrayList<>();
        preOrderRec(root, list);
        return list;
    }

    private void preOrderRec(Node node, List<Athlete> list) {
        if (node != null) {
            list.add(node.athlete);
            preOrderRec(node.left, list);
            preOrderRec(node.right, list);
        }
    }

    public List<Athlete> getPostOrder() {
        List<Athlete> list = new ArrayList<>();
        postOrderRec(root, list);
        return list;
    }

    private void postOrderRec(Node node, List<Athlete> list) {
        if (node != null) {
            postOrderRec(node.left, list);
            postOrderRec(node.right, list);
            list.add(node.athlete);
        }
    }

    public void clear() {
        root = null;
    }
}
