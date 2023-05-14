package top.rrricardo.postcalendarbackend.utils.generic;


import java.util.Arrays;

public class Heap<E extends Comparable<? super E>> {
    private Object[] queue;
    private int size = 0;

    private static final int defaultInitialCapacity = 11;

    /**
     * 新建一个小根堆
     * @param initialCapacity 初始的堆容量
     */
    public Heap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("非法的初始容量： " + initialCapacity);
        }

        queue = new Object[initialCapacity];
    }

    /**
     * 新建一个小根堆
     */
    public Heap() {
        this(defaultInitialCapacity);
    }

    public boolean add(E e) {
        return offer(e);
    }

    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        var i = size;

        if (i >= queue.length) {
            // 扩容队列
            grow();
        }

        shiftUp(i, e);
        size = i + 1;

        return true;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        return (E) queue[0];
    }

    @SuppressWarnings("unchecked")
    public E poll() {
        final var q = queue;
        final E result = (E)queue[0];

        if (result != null) {
            final var n = size - 1;
            size = n;
            final E element = (E) q[n];
            q[n] = null;
            if (n > 0) {
                shiftDown(0, element);
            }
        }

        return result;
    }

    public int getSize() {
        return size;
    }

    /**
     * 扩容数组
     */
    private void grow() {
        var oldCapacity = queue.length;

        var newCapacity = oldCapacity < 64 ? oldCapacity + 2 : oldCapacity + oldCapacity >> 1;
        if (newCapacity < 0) {
            throw new OutOfMemoryError();
        }

        queue = Arrays.copyOf(queue, newCapacity);
    }

    /**
     * 上浮操作
     * @param position 欲插入的位置
     * @param element 欲插入的元素
     */
    @SuppressWarnings("unchecked")
    private void shiftUp(int position, E element) {
        // 使用本地变量提高速度
        var q = queue;

        while (position > 0) {
            // 父节点的位置
            var parent = (position - 1) >>> 1;
            var parentElement = (E)q[parent];

            if (element.compareTo(parentElement) >= 0) {
                // 当前节点大于父节点
                break;
            }
            q[position] = parentElement;
            position = parent;
        }

        q[position] = element;
    }

    /**
     * 下沉操作
     * @param position 欲插入的位置
     * @param element 欲插入的元素
     */
    @SuppressWarnings("unchecked")
    private void shiftDown(int position, E element) {
        var q = queue;

        var half = size >>> 1;

        while (position < half) {
            // 假设左子是最小节点
            var child = (position << 1 ) + 1;
            E least = (E)q[child];
            // 判断右字是否小于左子
            var right = child + 1;
            if (right < size && least.compareTo((E) q[right]) > 0) {
                least = (E) q[right];
                child = right;
            }

            if (element.compareTo(least) <= 0) {
                break;
            }
            q[position] = least;
            position = child;
        }
        q[position] = element;
    }
}
