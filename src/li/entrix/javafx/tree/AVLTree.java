package li.entrix.javafx.tree;

import java.util.function.Function;

import static li.entrix.javafx.tree.AVLTree.BalanceFactor.*;

public class AVLTree<K,V> extends BinarySearchTree<K,V> {

    enum BalanceFactor {
        NEGATIVE(-1), ZERO(0), POSITIVE(1);

        private int val;

        BalanceFactor(int val) {
            this.val = val;
        }

        BalanceFactor valueOf(int val) {
            switch (val) {
                case -1:
                    return NEGATIVE;
                case 0:
                    return ZERO;
                case 1:
                    return POSITIVE;
                default:
                    return null;
            }
        }
    };

    @Override
    public boolean add(K key, V data) {
        AVLTreeNode newNode = (AVLTreeNode) super.addAndReturn(key, data);
        if (newNode != null) {
            insertRepairTree(newNode);
            TreeUtils.composeNodes(this, null);
            return true;
        }
        return false;
    }

    protected TreeNode createNode(K key, V data, TreeNode parent) {
        return new AVLTreeNode(key, data, parent);
    }

//    private AVLTreeNode adjustParentLink(AVLTreeNode rotatedNode, AVLTreeNode parentNode) {
//        AVLTreeNode grandParent = rotatedNode.getParent();
//        if (grandParent == null)
//            root = rotatedNode;
//        else if (parentNode == grandParent.left)
//            grandParent.left = rotatedNode;
//        else
//            grandParent.right = rotatedNode;
//        return parentNode;
//    }

    private AVLTreeNode leftRotation(AVLTreeNode newNode,
                                     Function<AVLTreeNode, AVLTreeNode> negativeCondition,
                                     Function<AVLTreeNode, AVLTreeNode> zeroCondition,
                                     Function<AVLTreeNode, AVLTreeNode> tailCondition) {
        System.out.println("left rotation");
        AVLTreeNode parentNode = newNode.getParent();

        AVLTreeNode result = negativeCondition.apply(newNode);
        if (result != null) return result;

        result = zeroCondition.apply(newNode);
        if (result != null) return result;

        AVLTreeNode rotatedNode;

        if (newNode.bf == NEGATIVE) {
            rotatedNode = (AVLTreeNode) TreeUtils.rotateLeft(this,
                    TreeUtils.rotateRight(this, newNode).parent);
            switch (rotatedNode.bf) {
                case POSITIVE:
                    parentNode.bf = NEGATIVE;
                    rotatedNode .bf = ZERO;
                    break;
                case ZERO:
                    parentNode.bf = rotatedNode.bf = ZERO;
                    break;
                default:
                    parentNode.bf = ZERO;
                    rotatedNode.bf = POSITIVE;
            }
            rotatedNode.bf = ZERO;
        } else {
            rotatedNode = (AVLTreeNode) TreeUtils.rotateLeft(this, parentNode);
            switch (rotatedNode.bf) {
                case ZERO:
                    rotatedNode.bf = NEGATIVE;
                    parentNode.bf = POSITIVE;
                    break;
                default:
                    parentNode.bf = rotatedNode.bf = ZERO;
                    break;
            }
        }
        return tailCondition.apply(rotatedNode);
    }

    private AVLTreeNode rightRotation(AVLTreeNode newNode,
                                      Function<AVLTreeNode, AVLTreeNode> zeroCondition,
                                      Function<AVLTreeNode, AVLTreeNode> positiveCondition,
                                      Function<AVLTreeNode, AVLTreeNode> tailCondition) {
        System.out.println("right rotation");
        AVLTreeNode parentNode = newNode.getParent();

        AVLTreeNode result = zeroCondition.apply(newNode);
        if (result != null) return result;

        result = positiveCondition.apply(newNode);
        if (result != null) return result;

        AVLTreeNode rotatedNode;

        if (newNode.bf == POSITIVE) {
            rotatedNode = (AVLTreeNode) TreeUtils.rotateRight(this,
                    TreeUtils.rotateLeft(this, newNode).parent);
            switch (rotatedNode.bf) {
                case NEGATIVE:
                    parentNode.bf = POSITIVE;
                    rotatedNode .bf = ZERO;
                    break;
                case ZERO:
                    parentNode.bf = rotatedNode.bf = ZERO;
                    break;
                default:
                    parentNode.bf = ZERO;
                    rotatedNode.bf = NEGATIVE;
            }
            rotatedNode.bf = ZERO;
        } else {
            rotatedNode = (AVLTreeNode) TreeUtils.rotateRight(this, parentNode);
            switch (rotatedNode.bf) {
                case ZERO:
                    rotatedNode.bf = POSITIVE;
                    parentNode.bf = NEGATIVE;
                    break;
                default:
                    parentNode.bf = rotatedNode.bf = ZERO;
                    break;
            }
        }
        return tailCondition.apply(rotatedNode);
    }

    private AVLTreeNode insertRepairTree(AVLTreeNode node) {
        System.out.println("repair li.entrix.javafx.tree");
        if (node.parent == null) return node;

        if (node == node.parent.right)
            return leftRotation(node,
                    n -> {
                        if (n.getParent().bf == NEGATIVE) {
                            n.getParent().bf = ZERO;
                            return n;
                        }
                        return null;
                    },
                    z -> {
                        if (z.getParent().bf == ZERO) {
                            z.getParent().bf = POSITIVE;
                            return insertRepairTree(z.getParent());
                        }
                        return null;
                    },
                    t -> t);
        else
            return rightRotation(node,
                    z -> {
                        if (z.getParent().bf == POSITIVE) {
                            z.getParent().bf = ZERO;
                            return z;
                        }
                        return null;
                    },
                    p -> {
                        if (p.getParent().bf == ZERO) {
                            p.getParent().bf = NEGATIVE;
                            return insertRepairTree(p.getParent());
                        }
                        return null;
                    },
                    t -> t);
    }

    @Override
    public boolean remove(K key) {
        boolean result = super.remove(key);

        TreeUtils.composeNodes(this,null);

        return result;
    }

    protected void swapNodes(TreeNode node, TreeNode child) {
        super.swapNodes(node, child);
        AVLTreeNode avlNode = (AVLTreeNode) node, avlChild = (AVLTreeNode) child;
        BalanceFactor bf = avlChild.bf;
        avlChild.bf = avlNode.bf;
        avlNode.bf = bf;
    }

    @Override
    protected void removeAndReturn(TreeNode node) {
        AVLTreeNode parent =  ((AVLTreeNode)node).getParent();
        if (node.left == null && node.right == null) {
            deletionRepairTree((AVLTreeNode) node);
            super.removeAndReturn(node);
        }
        else {
            deletionRepairTree((AVLTreeNode)node);
            super.removeAndReturn(node);
        }
    }

    private AVLTreeNode deletionRepairTree(AVLTreeNode node) {
        if (node.parent == null) return node;
        BalanceFactor bf;

        if (node == node.parent.left) {
            bf = node.getParent().getRight() != null ?
                    node.getParent().getRight().bf : null;
            return leftRotation(node,
                    n -> {
                        if (n.getParent().bf == NEGATIVE) {
                            n.getParent().bf = ZERO;
                            return deletionRepairTree(n.getParent());
                        }
                        return null;
                    },
                    z -> {
                        if (z.getParent().bf == ZERO) {
                            z.getParent().bf = POSITIVE;
                            return z;
                        }
                        return null;
                    },
                    t -> bf == ZERO ? t : deletionRepairTree(t));
        }
        else {
            bf = node.getParent().getLeft() != null ?
                    node.getParent().getLeft().bf : null;
            return rightRotation(node,
                    z -> {
                        if (z.getParent().bf == ZERO) {
                            z.getParent().bf = NEGATIVE;
                            return z;
                        }
                        return null;
                    },
                    p -> {
                        if (p.getParent().bf == POSITIVE) {
                            p.getParent().bf = ZERO;
                            return deletionRepairTree(p.getParent());
                        }
                        return null;
                    },
                    t -> bf == ZERO ? t : deletionRepairTree(t));
        }
    }

    private class AVLTreeNode extends BinarySearchTree<K,V>.TreeNode {

        BalanceFactor bf = ZERO;

        public AVLTreeNode(K key, V data, TreeNode parent) {
            super(key, data, parent);
        }

        AVLTreeNode getLeft() {
            return (AVLTreeNode)left;
        }

        AVLTreeNode getRight() {
            return (AVLTreeNode)right;
        }

        AVLTreeNode getParent() {
            return (AVLTreeNode)parent;
        }
        AVLTreeNode getGrandParent() {
            return parent == null ? null : (AVLTreeNode)parent.parent;
        }

        AVLTreeNode getSibling() {
            if (parent == null) return null;
            if (this == parent.left)
                return (AVLTreeNode)parent.right;
            else
                return (AVLTreeNode)parent.left;
        }
    }
}
