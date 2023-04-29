package top.rrricardo.postcalendarbackend.utils.generic;

public class AvlTreeNode<T extends Comparable<? super T>> {
    /**
     * 存储的数据
     */
    T data;
    /**
     * 节点的高度
     */
    int height;
    AvlTreeNode<T> leftNode;
    AvlTreeNode<T> rightNode;
    AvlTreeNode<T> parentNode;
    public AvlTreeNode(T data) {
        this.data = data;
        this.leftNode = null;
        this.rightNode = null;
        this.parentNode = null;
        this.height = 0;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    /**
     * 计算平衡因子
     * @param node 需要计算的节点
     * @return 平衡因子
     */
    static <T extends Comparable<? super T>> int CalculateBalanceFactor(AvlTreeNode<T> node) {
        var leftHeight = node.leftNode != null ? node.leftNode.height : 0;
        var rightHeight = node.rightNode != null ? node.rightNode.height : 0;

        return leftHeight - rightHeight;
    }
}
