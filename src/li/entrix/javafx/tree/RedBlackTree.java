package li.entrix.javafx.tree;

import li.entrix.javafx.tree.view.NodeColor;

import java.util.Comparator;

import static li.entrix.javafx.tree.view.NodeColor.BLACK;
import static li.entrix.javafx.tree.view.NodeColor.RED;

public class RedBlackTree<K,V> extends BinarySearchTree<K,V> {

    private Comparator<K> comparator;


    public RedBlackTree() {
    }

    public RedBlackTree(Comparator<K> comparator) {
        super(comparator);
    }

    public boolean add(K key, V data) {
        RBTreeNode newNode = (RBTreeNode) super.addAndReturn(key, data);
        if (newNode != null) {
            insertRepairTree(newNode);
            TreeUtils.composeNodes(this,null);
            return true;
        }
        return false;
    }

    protected TreeNode createNode(K key, V data, TreeNode parent) {
        return new RBTreeNode(key, data, parent);
    }

    private void insertRepairTree(RBTreeNode node) {
        if (node.parent == null) {
            insertCaseOne(node);
        }
        else if (node.getParent().cl == BLACK) {
            insertCaseTwo(node);
        }
        else if (node.getUncle() != null && node.getUncle().cl == RED) {
            insertCaseThree(node);
        }
        else {
            insertCaseFour(node);
        }
    }

    private void insertCaseOne(RBTreeNode node) {
        if (node.parent == null)
            node.cl = BLACK;
    }

    private void insertCaseTwo(RBTreeNode node) {
        return;
    }

    private void insertCaseThree(RBTreeNode node) {
        node.getParent().cl = BLACK;
        node.getUncle().cl = BLACK;
        node.getGrandParent().cl = RED;
        insertRepairTree(node.getGrandParent());
    }

    private void insertCaseFour(RBTreeNode node) {
        RBTreeNode parent = (RBTreeNode) node.parent, grandParent = node.getGrandParent();
        if (grandParent.left != null && node == grandParent.left.right) {
            TreeUtils.rotateLeft(this, parent);
            node = node.getLeft();
        }
        else if (grandParent.right != null && node == grandParent.right.left) {
            TreeUtils.rotateRight(this, parent);
            node = node.getRight();
        }

        insertCaseFourStep2(node);
    }

    private void insertCaseFourStep2(RBTreeNode node) {
        RBTreeNode parent = node.getParent(), grandParent = node.getGrandParent();

        if (node == parent.left)
            TreeUtils.rotateRight(this, grandParent);
        else
            TreeUtils.rotateLeft(this, grandParent);

        parent.cl = BLACK;
        grandParent.cl = RED;
    }


    @Override
    public boolean remove(K key) {
        boolean result = super.remove(key);

        TreeUtils.composeNodes(this,null);

        return result;
    }

    @Override
    protected void swapNodes(TreeNode node, TreeNode child) {
        super.swapNodes(node, child);
        RBTreeNode rbNode = (RBTreeNode) node, rbChild = (RBTreeNode) child;
        NodeColor cl = rbChild.cl;
        rbChild.cl = rbNode.cl;
        rbNode.cl = cl;
    }

    @Override
    protected void removeAndReturn(TreeNode node) {
        RBTreeNode rbNode = (RBTreeNode) node;
        if (rbNode.left == null && rbNode.right == null) {
            if(rbNode.cl == BLACK)
                deleteCaseOne(rbNode);

            if (rbNode.parent != null && rbNode.parent.left == rbNode)
                rbNode.parent.left = null;
            else if (rbNode.parent != null)
                rbNode.parent.right = null;
        }
        else {
            RBTreeNode child = rbNode.getLeft() != null ?
                    rbNode.getLeft() : rbNode.getRight();

            replaceNode(rbNode, child);

            if (child.parent == null) root = child;
            if (rbNode.cl == BLACK) {
                if (child.cl == RED)
                    child.cl = BLACK;
                else
                    deleteCaseOne(child);
            }
        }
    }

    private void deleteCaseOne(RBTreeNode node) {
        if (node.parent != null)
            deleteCaseTwo(node);
    }

    private void deleteCaseTwo(RBTreeNode node) {
        RBTreeNode sibling = node.getSibling();

        if (sibling.cl == RED) {
            node.getParent().cl = RED;
            sibling.cl = BLACK;
            if (node == node.parent.left)
                TreeUtils.rotateLeft(this, node.parent);
            else
                TreeUtils.rotateRight(this, node.parent);
        }
        deleteCaseThree(node);
    }

    private void deleteCaseThree(RBTreeNode node) {
        RBTreeNode sibling = node.getSibling();

        if (node.getParent().cl == BLACK && sibling.cl == BLACK &&
                (sibling.left == null || sibling.getLeft().cl == BLACK) &&
                (sibling.right == null || sibling.getRight().cl == BLACK)) {
            sibling.cl = RED;
            deleteCaseOne(node.getParent());
        }
        else
            deleteCaseFour(node);
    }

    private void deleteCaseFour(RBTreeNode node) {
        RBTreeNode sibling = node.getSibling();

        try {
            if (node.getParent().cl == RED && sibling.cl == BLACK &&
                    (sibling.left == null || sibling.getLeft().cl == BLACK) &&
                    (sibling.right == null || sibling.getRight().cl == BLACK)) {
                sibling.cl = RED;
                node.getParent().cl = BLACK;
            } else
                deleteCaseFive(node);
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private void deleteCaseFive(RBTreeNode node) {
        RBTreeNode sibling = node.getSibling();

        if (sibling.cl == BLACK) {
            if (node == node.parent.left &&
                    (sibling.right == null || sibling.getRight().cl == BLACK) &&
                    (sibling.left != null && sibling.getLeft().cl == RED)) {
                sibling.cl = RED;
                sibling.getLeft().cl = BLACK;
                TreeUtils.rotateRight(this, (sibling));
            }
            else if (node == node.parent.right &&
                    (sibling.left == null || sibling.getLeft().cl == BLACK) &&
                    (sibling.right != null && sibling.getRight().cl == RED)) {
                sibling.cl = RED;
                sibling.getRight().cl = BLACK;
                TreeUtils.rotateLeft(this, sibling);
            }
            deleteCaseSix(node);
        }
    }

    private void deleteCaseSix(RBTreeNode node) {
        RBTreeNode sibling = node.getSibling();

        sibling.cl = node.getParent().cl;
        node.getParent().cl = BLACK;

        if (node == node.parent.left) {
            sibling.getRight().cl = BLACK;
            TreeUtils.rotateLeft(this, node.parent);
        }
        else {
            sibling.getLeft().cl = BLACK;
            TreeUtils.rotateRight(this, node.parent);
        }
    }

//    private void composeNodes(Queue<TreeNode> swapNodes) {
//        List<FillingEntry> swapEntries = new LinkedList<>();
//        if (swapNodes != null) {
//            while (!swapNodes.isEmpty()) {
//                TreeNode t1 = swapNodes.poll();
//                TreeNode t2 = swapNodes.poll();
//                swapEntries.add(new FillingEntry(t1.key, t2.key, null));
//            }
//        }
//        List<FillingEntry> entries = new LinkedList<>();
//        if (root == null) {
//            controller.redrawTree(entries);
//            return;
//        }
//        Deque<TreeNode> deque = new LinkedList<>();
//        deque.push(root);
//        TreeNode next;
//        while (!deque.isEmpty()) {
//            next = deque.pop();
//            entries.add(
//                    new FillingEntry(next.parent == null ? null : next.parent.key, next.key, ((RBTreeNode)next).cl));
//            if (next.left != null) deque.push(next.left);
//            if (next.right != null) deque.push(next.right);
//        }
//        if (!swapEntries.isEmpty())
//            controller.swapShapes(swapEntries, entries);
//        else
//            controller.redrawTree(entries);
//    }

    class RBTreeNode extends BinarySearchTree<K,V>.TreeNode {

        NodeColor cl = RED;


        RBTreeNode(K key, V data, TreeNode parent) {
            super(key, data, parent);
        }

        RBTreeNode getLeft() {
            return (RBTreeNode)left;
        }

        RBTreeNode getRight() {
            return (RBTreeNode)right;
        }

        RBTreeNode getParent() {
            return (RBTreeNode)parent;
        }
        RBTreeNode getGrandParent() {
            return parent == null ? null : (RBTreeNode)parent.parent;
        }

        RBTreeNode getUncle() {
            if (getGrandParent() == null) return null;
            return ((RBTreeNode)parent).getSibling();
        }

        RBTreeNode getSibling() {
            if (parent == null) return null;
            if (this == parent.left)
                return (RBTreeNode)parent.right;
            else
                return (RBTreeNode)parent.left;
        }
    }
}
