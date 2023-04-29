package top.rrricardo.postcalendarbackend.utils.generic;

import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;

/**
 * 平衡二叉树
 * @param <T> 节点中携带的数据
 */
public class AvlTree<T extends Comparable<? super T>> {
    private AvlTreeNode<T> root = null;
    // 最大允许的不平衡参数
    private static final int AllowedImbalance = 1;

    public void insert(T data) throws AvlNodeRepeatException {
        var newNode = new AvlTreeNode<T>(data);

        if (root == null) {
            // 空树
            root = newNode;
        } else {
            insert(root, newNode);

            // 插入之后重新计算每个节点的高度
            reCalculateHeight(root);

            var imBalanceTree = findMinImbalanceNode(newNode);

            if (imBalanceTree != null) {
                // 发现了不平衡的地方
                rotate(newNode, imBalanceTree);
            }
        }
    }

    @Override
    public String toString() {
        if (root == null) {
            return "";
        }

        return preorderPrint(root).toString();
    }

    /**
     * 前序遍历打印节点
     * @param node 节点
     * @return 字符串构建类
     */
    private StringBuilder preorderPrint(AvlTreeNode<T> node) {
        var builder = new StringBuilder();

        if (node.leftNode != null) {
            builder.append(preorderPrint(node.leftNode));
        }

        builder.append(node.data).append(' ');

        if (node.rightNode != null) {
            builder.append(preorderPrint(node.rightNode));
        }

        return builder;
    }

    /**
     * 插入节点
     * @param parent 父节点
     * @param node 需要插入的节点
     */
    private void insert(AvlTreeNode<T> parent, AvlTreeNode<T> node) throws AvlNodeRepeatException {
        var compareResult = node.data.compareTo(parent.data);

        if (compareResult == 0) {
            // 节点重复
            throw new AvlNodeRepeatException(node.data.toString());
        } else if (compareResult < 0) {
            // 新插入的节点应该在左侧
            if (parent.leftNode == null) {
                parent.leftNode = node;
                node.parentNode = parent;
            } else {
                // 递归调用
                insert(parent.leftNode, node);
            }
        } else {
            // 新插入的节点应该在右侧
            if (parent.rightNode == null) {
                parent.rightNode = node;
                node.parentNode = parent;
            } else {
                insert(parent.rightNode, node);
            }
        }
    }

    /**
     * 找到树中最小的不平衡子树
     * @param node 新插入的节点
     * @return 不平衡子树的根节点
     */
    private AvlTreeNode<T> findMinImbalanceNode(AvlTreeNode<T> node) {
        var parent = node.parentNode;

        if (parent != null) {
            var balanceFactor = Math.abs(AvlTreeNode.CalculateBalanceFactor(parent));
            if (balanceFactor > AllowedImbalance) {
                return parent;
            } else {
                // 继续向上递归查找
                return findMinImbalanceNode(parent);
            }
        } else {
            var balanceFactor = Math.abs(AvlTreeNode.CalculateBalanceFactor(node));

            if (balanceFactor > AllowedImbalance) {
                return node;
            }
        }

        return null;
    }

    /**
     * 节点向右旋转
     * @param node 不平衡的节点
     */
    private void rightRotate(AvlTreeNode<T> node) {
        var parent = node.parentNode;
        var left = node.leftNode;


        if (parent != null) {
            // 修改当前节点的父节点指向当前节点的左子节点
            if (node == parent.leftNode) {
                parent.leftNode = node.leftNode;
            } else if (node == parent.rightNode) {
                // 虽然从逻辑上说，子节点不是左子节点就是右子节点
                parent.rightNode = node.leftNode;
            }
        } else {
            // 如果没有父节点
            // 说明是根节点
            root = left;
        }

        // 处理左子节点的右子节点
        var leftNodeRight = left.rightNode;
        if (leftNodeRight != null) {
            leftNodeRight.parentNode = node;
            node.leftNode = leftNodeRight;
        } else {
            // 注意这里
            node.leftNode = null;
        }

        // 颠倒左子节点和当前节点的父子关系
        left.parentNode = node.parentNode;
        node.parentNode = left;
        left.rightNode = node;

        reCalculateHeight(root);
    }

    /**
     * 节点向左旋转
     * @param node 需要旋转的节点
     */
    private void leftRotate(AvlTreeNode<T> node) {
        var parent = node.parentNode;
        var right = node.rightNode;

        if (parent != null) {
            if (node == parent.leftNode) {
                parent.leftNode = right;
            } else if (node == parent.rightNode) {
                parent.rightNode = right;
            }
        } else {
            root = right;
        }

        var rightNodeLeft = right.leftNode;
        if (rightNodeLeft != null) {
            rightNodeLeft.parentNode = node;
            node.rightNode = rightNodeLeft;
        } else {
            node.rightNode = null;
        }

        right.parentNode = node.parentNode;
        node.parentNode = right;
        right.leftNode = node;

        reCalculateHeight(root);
    }

    /**
     * 执行旋转
     * @param newNode 新插入的节点
     * @param tree 不平衡子树的根节点
     */
    private void rotate(AvlTreeNode<T> newNode, AvlTreeNode<T> tree) {
        /*
        插入之后可能出现的四种情况
        - 左左 右旋纠正
        - 左右 左旋更正为左左
        - 右右 左旋纠正
        - 右左 右旋更正为右右
         */

        var parent = newNode.parentNode;
        var balanceFactor = AvlTreeNode.CalculateBalanceFactor(tree);
        if (balanceFactor > 0) {
            // 左边的高于右边
            if (newNode != parent.leftNode) {
                // 左右的插入情况
                leftRotate(parent);
            }

            rightRotate(tree);
        } else if (balanceFactor < 0) {
            // 右边高于左边
            if (newNode != parent.rightNode) {
                // 右左的插入情况
                rightRotate(parent);
            }

            leftRotate(tree);
        }
    }


    /**
     * 重新计算树中每个节点的高度
     * @param node 需要计算的节点
     */
    private void reCalculateHeight(AvlTreeNode<T> node) {
        if (node.leftNode != null && node.rightNode != null) {
            reCalculateHeight(node.leftNode);
            reCalculateHeight(node.rightNode);

            node.height = Math.max(node.leftNode.height, node.rightNode.height) + 1;
        } else if (node.leftNode != null) {
            reCalculateHeight(node.leftNode);

            node.height = node.leftNode.height + 1;
        } else if (node.rightNode != null) {
            reCalculateHeight(node.rightNode);

            node.height = node.rightNode.height + 1;
        } else {
            node.height = 1;
        }
    }
}
