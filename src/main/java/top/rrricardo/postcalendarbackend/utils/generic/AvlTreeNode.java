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

    /**
     * 获得当前姐弟年的平衡因子
     * @return 当前
     */
    public int getBalanceFactor() {
        var leftHeight = leftNode != null ? leftNode.height : 0;
        var rightHeight = rightNode != null ? rightNode.height : 0;

        return leftHeight - rightHeight;
    }
    @Override
    public String toString() {
        return data.toString();
    }
}
