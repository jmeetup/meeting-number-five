package li.entrix.javafx.tree;

import java.util.Comparator;

public class BinarySearchTree<K,V> {

    protected TreeNode root;

    private Comparator<K> comparator;

    public BinarySearchTree() {
    }

    public BinarySearchTree(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public boolean add(K key, V data) {
        TreeNode newNode = addAndReturn(key, data);

//        insertRepairTree(newNode);
//        composeNodes(null);
        return newNode == null;
    }

    protected TreeNode addAndReturn(K key, V data) {
        TreeNode newNode = null;
        if (data == null) return null;
        if (root == null) {
            newNode = root = createNode(key, data, newNode);
        }
        else {
            TreeNode node = findClosestNode(key);
            if (node.key.equals(key)) return null;
            newNode = createNode(key, data, node);
            if (compare(key, node.key) > 0)
                node.right = newNode;
            else
                node.left = newNode;
        }

        return newNode;
    }

    protected TreeNode createNode(K key, V data, TreeNode parent) {
        return new TreeNode(key, data, parent);
    }

    private int compare(K keyOne, K keyTwo) {
        return comparator != null ?
                comparator.compare(keyTwo, keyTwo) :
                ((Comparable<? super K>)keyOne).compareTo((K)keyTwo);
    }

    private TreeNode findClosestNode(K key) {
        TreeNode next = root, previous = null;
        while (next != null) {
            int res = compare(key, next.key);
            if (res == 0) break;
            previous = next;
            next = res > 0 ?
                    next.right : next.left;
        }
        return next == null ? previous : next;
    }

    public V find(K key) {
        TreeNode node = findClosestNode(key);
        if (node != null && node.key.equals(key)) {
            return node.data;
        }
        return null;
    }

    protected void replaceNode(TreeNode node, TreeNode child) {
        child.parent = node.parent;
        if (node.parent != null && node == node.parent.left) {
            node.parent.left = child;
        }
        else if (node.parent != null)
            node.parent.right = child;
        else
            root = child;
    }

    public boolean remove(K key) {
        TreeNode node = findClosestNode(key);
        if (node == null) return false;
        if (!node.key.equals(key)) return true;
        if (root == node && root.left == null && root.right == null) {
            root = null;
            return true;
        }

        if (node.left != null && node.right != null) {
            // case when we have two non-leaf children
            TreeNode child = getSuccessor(node);
            swapNodes(node, child);
        }

//        composeNodes(null);
        removeAndReturn(node);

        return true;
    }

    protected void removeAndReturn(TreeNode node) {
        if (node.left == null && node.right == null) {
            if (node.parent != null && node.parent.left == node)
                node.parent.left = null;
            else if (node.parent != null)
                node.parent.right = null;
        }
        else {
            TreeNode child = node.left != null ?
                    node.left : node.right;

            replaceNode(node, child);

            if (child.parent == null) root = child;
        }
    }

    private TreeNode getSuccessor(TreeNode node) {
        TreeNode next = node.right;
        while (next.left != null) next = next.left;
        return next;
    }

    protected void  swapNodes(TreeNode node, TreeNode child) {
        TreeNode swap;
        node.left.parent = child;
        child.left = node.left;
        node.left = null;

        if (node.parent != null && node == node.parent.left) {
            node.parent.left = child;
        }
        else if (node.parent != null)
            node.parent.right = child;
        else
            root = child;

        if (node.right != child) {
            child.parent.left = node;
            node.right.parent = child;
            swapRight(node, child);
            swapParent(node, child);
        }
        else {
            node.right = child.right;
            child.right = node;
            child.parent = node.parent;
            node.parent = child;
        }
    }

    private void swapParent(TreeNode n1, TreeNode n2) {
        TreeNode sw = n1.parent;
        n1.parent = n2.parent;
        n2.parent = sw;
    }

    private void swapRight(TreeNode n1, TreeNode n2) {
        TreeNode sw = n1.right;
        n1.right = n2.right;
        n2.right = sw;
    }

    protected class TreeNode {
        public K key;
        public V data;

        public TreeNode parent;
        public TreeNode left = null;
        public TreeNode right = null;

        public TreeNode(K key, V data, TreeNode parent) {
            this.key = key;
            this.data = data;
            this.parent = parent;
        }
    }
}
