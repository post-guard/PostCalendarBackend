package top.rrricardo.postcalendarbackend.utils.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * 自行实现的数组列表
 * @param <E>
 */
public class CustomList<E> implements Iterable<E>  {
    /**
     * 数组的默认大小
     */
    private static final int DefaultCapacity = 10;

    private static final int MaxArraySize = Integer.MAX_VALUE - 8;

    /**
     * 共享的空数组实例
     * 使所有的空列表对象使用一个共同的队列列表
     */
    private static final Object[] EmptyElementArray = {};

    /**
     * 实际存储元素的数组
     */
    private Object[] elements;

    /**
     * 当前列表中元素的个数
     */
    private int size;

    /**
     * 获得当前列表中存储对象的数量
     * @return 当前列表中存储对象的数量
     */
    public int getSize() {
        return size;
    }

    /**
     * 创建新的列表对象
     * @param initialCapacity 初始列表的容量
     * @throws IllegalArgumentException 指定的容量非法
     */
    public CustomList(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity > 0 && initialCapacity < MaxArraySize) {
            elements = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            elements = EmptyElementArray;
        } else {
            throw new IllegalArgumentException("初始化数组大小必须大于或者等于0");
        }
    }

    /**
     * 创建新的列表对象
     */
    public CustomList() {
        elements = EmptyElementArray;
    }

    /**
     * 创建新的列表对象
     * @param collection 从指定的列表中导入数据
     */
    public CustomList(Collection<? extends E> collection) {
        elements = collection.toArray();
        size = collection.size();

        if (size == 0) {
            elements = EmptyElementArray;
        }
    }

    /**
     * 判断列表中是否包含指定的对象
     * @param target 需要查找的对象
     * @return 是否包含
     */
    public boolean contains(Object target) {
        return indexOf(target) >= 0;
    }

    /**
     * 获得指定对象在列表中的索引
     * @param target 需要查找的对象
     * @return 对象的索引 如果不存在为-1
     */
    public int indexOf(Object target) {
        return indexOfRange(target, 0, size);
    }

    /**
     * 在指定的列表范围内查找对象
     * @param target 需要查找的对象
     * @param begin 开始查找的位置
     * @param end 结束查找的位置
     * @return 指定对象的索引 如果不存在为-1
     */
    public int indexOfRange(Object target, int begin, int end) {
        if ((begin < 0 || end < 0)
        && (begin >= size || end >= size)
        && (begin > end)) {
            throw new IllegalArgumentException();
        }

        var data = elements;

        if (target == null) {
            for (var i = begin; i < end; i++) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (var i = begin; i < end; i++) {
                if(target.equals(data[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 获得实际的对象数组
     * @return 存储的对象数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    /**
     * 获得在指定索引的元素
     * @param index 指定的索引
     * @return 列表中指定索引的元素
     * @throws ArrayIndexOutOfBoundsException 指定的索引不合法
     */
    public E get(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return elementAt(index);
    }

    /**
     * 设置在指定索引上元素的值
     * @param index 指定的索引
     * @param element 需要指定的元素
     * @return 指定索引上原始值
     */
    public E set(int index, E element) {
        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        var oldValue = elementAt(index);
        elements[index] = element;

        return oldValue;
    }

    /**
     * 返回遍历用的迭代器
     * @return 迭代器对象
     */
    public Iterator<E> iterator() {
        return new CustomIterator();
    }

    /**
     * 在列表的末尾添加元素
     * @param element 需要添加的元素
     */
    public void add(E element) {
        add(element, size);
    }

    /**
     * 在指定的索引上添加对象
     * @param element 需要添加的对象
     * @param index 指定的索引
     */
    public void add(E element, int index) {
        if (index > size || size < 0) {
            throw new IndexOutOfBoundsException();
        }

        final int s = size;

        if (s == elements.length) {
            elements = grow();
        }

        System.arraycopy(elements, index, elements, index + 1, s - index);
        elements[index] = element;

        size = s + 1;
    }

    /**
     * 在指定的索引上移除元素
     * @param index 指定的索引
     * @return 移除索引上的元素
     */
    public E remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        final var newSize = size - 1;
        final var oldValue = elementAt(index);
        if (newSize > index) {
            System.arraycopy(elements, index + 1, elements, index, newSize - index);
        }

        elements[index] = null;
        size = newSize;

        if (size < elements.length / 2) {
            var newCapacity = elements.length / 2;
            elements = Arrays.copyOf(elements, newCapacity);
        }

        return oldValue;
    }

    /**
     * 移除列表末尾的元素
     * @return 被移除的元素
     */
    public E remove() {
        return remove(size - 1);
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int index) {
        return (E)elements[index];
    }

    /**
     * 实现的迭代器对象
     */
    private class CustomIterator implements Iterator<E> {
        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (cursor >= size) {
                throw new ArrayIndexOutOfBoundsException();
            }

            var result = (E)elements[cursor];
            cursor++;
            return result;
        }
    }

    private Object[] grow() {
        var newCapacity = size == 0 ? DefaultCapacity : 2 * size;

        // 直接溢出了
        if (newCapacity < 0) {
            throw new OutOfMemoryError();
        }

        newCapacity = Math.min(newCapacity, MaxArraySize);

        return Arrays.copyOf(elements, newCapacity);
    }
}
