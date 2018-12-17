package li.entrix.javafx.tree;

import li.entrix.javafx.tree.view.FillingEntry;
import li.entrix.javafx.tree.view.TreeController;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TreeUtils {

    private static TreeController controller;

    public static void setController(TreeController controller) {
        TreeUtils.controller = controller;
    }

    public static <K,V> BinarySearchTree<K,V>.TreeNode rotateLeft(BinarySearchTree<K,V> tree, BinarySearchTree<K,V>.TreeNode node) {
        BinarySearchTree<K,V>.TreeNode nnew = node.right, parent = node.parent;
        if (nnew == null) return node;
        node.right = nnew.left;
        nnew.left = node;
        node.parent = nnew;
        if (node.right != null)
            node.right.parent = node;
        if (parent != null) {
            if (node == parent.left)
                parent.left = nnew;
            else if (node == parent.right)
                parent.right = nnew;
        }
        nnew.parent = parent;
        if (tree.root == node) tree.root = nnew;
        return nnew;
    }

    public static <K,V> BinarySearchTree<K,V>.TreeNode rotateRight(BinarySearchTree<K,V> tree, BinarySearchTree<K,V>.TreeNode node) {
        BinarySearchTree<K,V>.TreeNode nnew = node.left, parent = node.parent;
        if (nnew == null) return node;
        node.left = nnew.right;
        nnew.right = node;
        node.parent = nnew;
        if (node.left != null)
            node.left.parent = node;
        if (parent != null) {
            if (node == parent.left)
                parent.left = nnew;
            else if (node == parent.right)
                parent.right = nnew;
        }
        nnew.parent = parent;
        if (tree.root == node) tree.root = nnew;
        return nnew;
    }

    public static  <K,V> void composeNodes(BinarySearchTree<K,V> tree, Queue<BinarySearchTree.TreeNode> swapNodes) {
        List<FillingEntry> swapEntries = new LinkedList<>();
        if (swapNodes != null) {
            while (!swapNodes.isEmpty()) {
                BinarySearchTree.TreeNode t1 = swapNodes.poll();
                BinarySearchTree.TreeNode t2 = swapNodes.poll();
                swapEntries.add(new FillingEntry(t1.key, t2.key, null));
            }
        }
        List<FillingEntry> entries = new LinkedList<>();
        if (tree.root == null) {
            controller.redrawTree(entries);
            return;
        }
        Deque<BinarySearchTree.TreeNode> deque = new LinkedList<>();
        deque.push(tree.root);
        BinarySearchTree.TreeNode next;
        while (!deque.isEmpty()) {
            next = deque.pop();
            entries.add(
                    new FillingEntry(next.parent == null ? null : next.parent.key, next.key, (next instanceof RedBlackTree.RBTreeNode) ? ((RedBlackTree.RBTreeNode)next).cl : null));
            if (next.left != null) deque.push(next.left);
            if (next.right != null) deque.push(next.right);
        }
        if (!swapEntries.isEmpty())
            controller.swapShapes(swapEntries, entries);
        else
            controller.redrawTree(entries);
    }
}
