package top.rrricardo.postcalendarbackend.utils.generic;

import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.CustomStackEmptyException;

import java.util.Iterator;

/**
 * 平衡二叉树
 *
 * @param <T> 节点中携带的数据
 */
public class AvlTree<T extends Comparable<? super T>> implements Iterable<T> {
    private AvlTreeNode<T> root = null;
    // 最大允许的不平衡参数
    private static final int AllowedImbalance = 1;

    /**
     * 缓存🌳中对象的有序列表
     */
    private CustomList<T> sortedList = new CustomList<>();
    private boolean modified = false;

    /**
     * 想平衡树中插入一个节点
     *
     * @param data 需要插入节点的数据
     * @throws AvlNodeRepeatException 同平衡树中的数据冲突
     */
    public void insert(T data) throws AvlNodeRepeatException {
        var newNode = new AvlTreeNode<>(data);

        if (root == null) {
            // 空树
            root = newNode;
        } else {
            insert(root, newNode);
        }
        modified = true;
    }

    /**
     * 从节点中移除一个数据
     *
     * @param data 需要移除的数据
     */
    public void remove(T data) {
        remove(root, data);
        modified = true;
    }

    /**
     * 在树上查找指定的元素
     *
     * @param target 需要查找的元素
     * @return 找到的元素，如果为空说明未找到
     */
    public T find(T target) {
        return find(target, root);
    }

    public CustomList<T> selectRange(T begin, T end) {
        var result = new CustomList<T>();

        for (T data : this) {
            if (data.compareTo(begin) >= 0 && data.compareTo(end) <= 0) {
                result.add(data);
            }

            if (data.compareTo(end) > 0) {
                break;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        if (root == null) {
            return "";
        }

        return preorderPrint(root).toString();
    }

    /**
     * 得到利用Avl树排序的有序列表
     * 利用缓存提高性能
     * @return 树中元素的有序列表
     */
    public CustomList<T> toSortedList() {
        if (modified) {
            var stack = new CustomStack<AvlTreeNode<T>>();
            var result = new CustomList<T>();

            // 初始化栈
            appendNext(stack, root);

            while (!stack.empty()) {
                try {
                    var node = stack.pop();

                    if (node.rightNode != null) {
                        appendNext(stack, node.rightNode);
                    }

                    result.add(node.data);
                } catch (CustomStackEmptyException ignored) {

                }
            }

            modified = false;
            return result;
        } else {
            return sortedList;
        }

    }

    @Override
    public Iterator<T> iterator() {
        sortedList = toSortedList();

        return sortedList.iterator();
    }

    /**
     * 前序遍历打印节点
     *
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
     *
     * @param parent 父节点
     * @param node   需要插入的节点
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
     * 从树中移除一个节点
     *
     * @param node       需要移除节点所在子树的根节点
     * @param removeData 需要移除的数据
     */
    private void remove(AvlTreeNode<T> node, T removeData) {
        if (node == null) {
            return;
        }

        var result = removeData.compareTo(node.data);

        if (result > 0) {
            remove(node.rightNode, removeData);
        } else if (result < 0) {
            remove(node.leftNode, removeData);
        }

        if (removeData.equals(node.data)) {
            // 当前节点和需要删除的数据大小一致
            // 确认两个数据一致
            if (node.leftNode != null && node.rightNode != null) {
                // 需要移除节点的左右子树都存在
                node.data = findMinNode(node.rightNode).data;
                // 移除被移动到parent的原始节点
                remove(node.rightNode, node.data);
            } else {
                var parent = node.parentNode;

                if (parent != null) {
                    // 当前节点不是根节点
                    if (node == parent.leftNode) {
                        parent.leftNode = node.leftNode != null ? node.leftNode : node.rightNode;

                        if (parent.leftNode != null) {
                            parent.leftNode.parentNode = parent;
                        }
                    } else if (node == parent.rightNode) {
                        parent.rightNode = node.leftNode != null ? node.leftNode : node.rightNode;

                        if (parent.rightNode != null) {
                            parent.rightNode.parentNode = parent;
                        }
                    }

                    node = parent;
                } else {
                    // 当前节点是根节点
                    root = node.leftNode != null ? node.leftNode : node.rightNode;
                    if (root != null) {
                        root.parentNode = null;
                    }

                    node = root;
                }
            }
        }

        balanceTree(node);
    }

    /**
     * 在树上查找指定的元素
     *
     * @param target 需要查找的元素
     * @param tree   需要查找的树
     * @return 查找的结果，如果为null则为未找到
     */
    private T find(T target, AvlTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        var result = target.compareTo(tree.data);

        if (result < 0) {
            return find(target, tree.leftNode);
        }

        if (result > 0) {
            return find(target, tree.rightNode);
        }

        return tree.data;
    }

    /**
     * 节点向右旋转
     *
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
     *
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
     *
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
     *
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
     *
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

    private void appendNext(CustomStack<AvlTreeNode<T>> stack, AvlTreeNode<T> root) {
        if (root != null) {
            var node = root;
            stack.push(node);
            node = node.leftNode;

            while (node != null) {
                stack.push(node);
                node = node.leftNode;
            }
        }
    }

    /**
     * 返回指定节点的高度
     *
     * @param node 需要获得高度的节点
     * @return 指定节点的高度，如果为空返回0
     */
    private int height(AvlTreeNode<T> node) {
        return node != null ? node.height : 0;
    }
}
