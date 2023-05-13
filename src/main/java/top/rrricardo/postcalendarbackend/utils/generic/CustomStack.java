package top.rrricardo.postcalendarbackend.utils.generic;

import top.rrricardo.postcalendarbackend.exceptions.CustomStackEmptyException;

/**
 * 自定义实现的栈数据结构
 * @param <E> 存储的数据类型
 */
public class CustomStack<E> extends CustomList<E> {
    public CustomStack() {

    }

    /**
     * 将一个对象压入栈中
     * @param item 需要添加的对象
     * @return 被添加的对象
     */
    public E push(E item) {
        this.add(item);
        return item;
    }

    /**
     * 获得栈顶的元素
     * 同步调用
     * @return 栈顶的元素
     * @throws CustomStackEmptyException 栈中没有元素引发的异常
     */
    public synchronized E peek() throws CustomStackEmptyException {
        var size = this.getSize();

        if (size == 0) {
            throw new CustomStackEmptyException();
        } else {
            return this.get(size - 1);
        }
    }

    /**
     * 移除栈顶的元素并将其返回
     * @return 栈顶的元素
     * @throws CustomStackEmptyException 栈中没有元素引发的异常
     */
    public synchronized E pop() throws CustomStackEmptyException {
        var item = this.peek();
        this.remove();

        return item;
    }

    /**
     * 判断栈是否为空
     * @return 栈为空返回真 反之返回假
     */
    public boolean empty() {
        return this.getSize() == 0;
    }
}
