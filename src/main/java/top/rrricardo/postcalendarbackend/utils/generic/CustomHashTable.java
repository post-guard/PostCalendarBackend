package top.rrricardo.postcalendarbackend.utils.generic;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 自行实现的哈希表
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CustomHashTable<K, V> implements Iterable<Map.Entry<K, V>> {
    private static float loadFactor = 0.75f;
    private static final int maxArraySize = Integer.MAX_VALUE - 8;

    /**
     * 存储链表的列表
     */
    private LinkedPair<?, ?>[] table;

    /**
     * 存储键值对的数量
     */
    private int count;

    /**
     * 阈值
     * 判断是否需要调整列表的大小
     */
    private int threshold;

    public CustomHashTable(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if (Float.isNaN(loadFactor) || loadFactor <= 0) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        CustomHashTable.loadFactor = loadFactor;
        table = new LinkedPair<? ,?>[initialCapacity];

        threshold = (int)(initialCapacity * loadFactor);
    }

    public CustomHashTable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public CustomHashTable() {
        //默认的构造参数
        // 大小是11
        // 填充因子是0.75
        this(11, 0.75f);
    }

    /**
     * 获得存储键值对的数量
     * @return 存储键值对的数量
     */
    public synchronized int getSize() {
        return count;
    }

    /**
     * 判断表是否为空
     * @return 为空为真，反之为假
     */
    public synchronized boolean isEmpty() {
        return count == 0;
    }

    /**
     * 获得一个键对应的值
     * @param key 指定的键
     * @return 获得的值，如果没有该键为null
     */
    @SuppressWarnings("unchecked")
    public synchronized V get(Object key) {
        var tab = table;
        var hash = key.hashCode();
        var index = (hash & Integer.MAX_VALUE) % tab.length;

        // 遍历链表
        for(var node = tab[index]; node != null; node = node.nextPair) {
            if ((node.hashCode == hash) && node.key.equals(key)) {
                return (V)node.value;
            }
        }
        return null;
    }

    /**
     * 添加一个键值对
     * @param key 需要添加的键
     * @param value 需要添加的值
     * @return 如果添加的key已存在，返回被替换的值，反之返回null
     */
    public synchronized V put(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }

        var t = table;
        var hash = key.hashCode();
        var index = (hash & Integer.MAX_VALUE) % t.length;

        @SuppressWarnings("unchecked")
        var pair = (LinkedPair<K ,V>)t[index];
        // 首先遍历链表
        // 如果找到就修改值
        for (; pair != null; pair = pair.nextPair) {
            if ((pair.hashCode == hash) && pair.key.equals(key)) {
                var old = pair.value;
                pair.value = value;
                return old;
            }
        }

        // 没有在链表中找到
        // 就添加键值对
        addLinkedPair(hash, key, value, index);
        return null;
    }

    /**
     * 移除指定的键
     * @param key 需要移除的键
     * @return 返回被移除的对象，如果不存在则返回null
     */
    public synchronized V remove(Object key) {
        var t = table;
        var hash = key.hashCode();
        var index = (hash & Integer.MAX_VALUE) % t.length;

        @SuppressWarnings("unchecked")
        var pair = (LinkedPair<K ,V>)t[index];
        LinkedPair<K, V> previousPair = null;

        for(; pair != null; previousPair = pair, pair = pair.nextPair) {
            if ((pair.hashCode == hash) && pair.key.equals(key)) {
                // 找到节点
                if (previousPair == null) {
                    // 是链表中的头结点
                    t[index] = null;
                } else {
                    previousPair.nextPair = pair.nextPair;
                }

                count--;
                var oldValue = pair.value;
                pair.value = null;
                return oldValue;
            }
        }

        return null;
    }

    /**
     * 清除列表中的数量
     */
    public synchronized void clear() {
        var t = table;
        for (var i = t.length - 1; i > 0; i--) {
            t[i] = null;
        }
        count = 0;
    }

    /**
     * 扩大哈希表
     */
    @SuppressWarnings("unchecked")
    protected void rehash() {
        var oldCapacity = table.length;
        var oldTable = table;

        var newCapacity = oldCapacity * 2 + 1;
        if (newCapacity > maxArraySize) {
            if (oldCapacity == maxArraySize) {
                return;
            }
            newCapacity = maxArraySize;
        }
        threshold = (int)(newCapacity * loadFactor);

        var newTable = new LinkedPair<?, ?>[newCapacity];
        table = newTable;

        for (var i = oldCapacity - 1; i > 0; i--) {
            for (var old = (LinkedPair<K, V>)oldTable[i]; old != null;) {
                var index = (old.hashCode & Integer.MAX_VALUE) % newCapacity;

                var pair = old;
                old = old.nextPair;
                // 这里处理一下链表
                pair.nextPair = (LinkedPair<K, V>) newTable[index];
                newTable[index] = pair;
            }
        }
    }

    /**
     * 链表中添加一个节点
     * @param hash 键哈希值
     * @param key 键
     * @param value 值
     * @param index 数组的索引
     */
    private void addLinkedPair(int hash, K key, V value, int index) {
        var t = table;

        if (count >= threshold) {
            // 超过阈值
            rehash();

            t = table;
            hash = key.hashCode();
            index = (hash & Integer.MAX_VALUE) % t.length;
        }

        // 创建新节点
        @SuppressWarnings("unchecked")
        var pair = (LinkedPair<K ,V>)t[index];
        // 这里是在链表的头部添加节点
        t[index] = new LinkedPair<>(hash, key, value, pair);
        count++;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new CustomIterator();
    }


    /**
     * 链表法存储哈希表中的值对象
     * @param <K> 键类型
     * @param <V> 值类型
     */
    private static class LinkedPair<K, V> implements Map.Entry<K, V> {
        K key;
        V value;

        /**
         * 哈希值
         */
        int hashCode;

        /**
         * 下一个节点
         */
        LinkedPair<K ,V> nextPair;

        protected LinkedPair(int hash, K key, V value, LinkedPair<K ,V> next) {
            this.hashCode = hash;
            this.key = key;
            this.value = value;
            this.nextPair = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V v) {
            value = v;
            return v;
        }


        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof LinkedPair<?, ?> pair)) {
                return false;
            }

            return key == null ? pair.getKey() == null : key.equals(pair.getKey()) &&
                    (value == null ? pair.getValue() == null : value.equals(pair.getValue()));
        }
    }

    private class CustomIterator implements Iterator<Map.Entry<K, V>> {
        private final LinkedPair<?, ?>[] list = table;
        int index = list.length;
        LinkedPair<?, ?> pair = null;


        @Override
        public boolean hasNext() {
            // 使用本地变量以提高速度
            var p = pair;
            var i = index;

            while (p == null && i > 0) {
                i--;
                p = list[i];
            }
            pair = p;
            index = i;

            return p != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map.Entry<K, V> next() {
            // 使用本地变量提供速度
            var p = pair;
            var i = index;

            while (p == null && i > 0) {
                i--;
                p = list[i];
            }

            pair = p;
            index = i;

            if (p != null) {
                pair = p.nextPair;
                return (Map.Entry<K, V>)p;
            }
            throw new NoSuchElementException();
        }
    }
}
