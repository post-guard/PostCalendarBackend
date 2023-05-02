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
        var newNode = new AvlTreeNode<>(data);

        if (root == null) {
            // 空树
            root = newNode;
        } else {
            insert(root, newNode);
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

                reCalculateHeight(root);
            } else {
                // 递归调用
                insert(parent.leftNode, node);
            }
        } else {
            // 新插入的节点应该在右侧
            if (parent.rightNode == null) {
                parent.rightNode = node;
                node.parentNode = parent;

                reCalculateHeight(root);
            } else {
                insert(parent.rightNode, node);
            }
        }

        balanceTree(parent);
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
     * 将树调整平衡
     * @param node 新插入/删除节点的父节点
     */
    private void balanceTree(AvlTreeNode<T> node) {
        /*
        插入之后可能出现的四种情况
        - 左左 右旋纠正
        - 左右 左旋更正为左左
        - 右右 左旋纠正
        - 右左 右旋更正为右右
        删除中也具有类似的规律
         */

        if (node == null) {
            return;
        }

        var balanceFactor = node.getBalanceFactor();

        if (balanceFactor > AllowedImbalance) {
            // 左边高于右边
            if (height(node.leftNode.rightNode) > height(node.leftNode.leftNode)) {
                // 左右的情形
                leftRotate(node.leftNode);
            }

            rightRotate(node);
        } else if (balanceFactor < -AllowedImbalance) {
            // 右边高于左边
            if (height(node.rightNode.leftNode) > height(node.rightNode.rightNode)) {
                // 右左的情形
                rightRotate(node.rightNode);
            }

            leftRotate(node);
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

    /**
     * 寻找当前子树的最小节点
     * @param node 需要寻找的子树头结点
     * @return 最小节点
     */
    private AvlTreeNode<T> findMinNode(AvlTreeNode<T> node) {
        if (node == null) {
            return null;
        } else if (node.leftNode == null) {
            return node;
        }

        return findMinNode(node.leftNode);
    }

    /**
     * 寻找当前子树的最大节点
     * @param node 需要寻找的子树根节点
     * @return 最大节点
     */
    private AvlTreeNode<T> findMaxNode(AvlTreeNode<T> node) {
        if (node == null) {
            return null;
        } else if (node.rightNode == null) {
            return node;
        }

        return findMaxNode(node.rightNode);
    }

    /**
     * 返回指定节点的高度
     * @param node 需要获得高度的节点
     * @return 指定节点的高度，如果为空返回0
     */
    private int height(AvlTreeNode<T> node) {
        return node != null ? node.height : 0;
    }
}
