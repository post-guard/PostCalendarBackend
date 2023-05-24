package top.rrricardo.postcalendarbackend.utils.generic;

public class CustomLinkedList<T> {
    private Node head;
    private int size;

    public CustomLinkedList() {
        head = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public void add(T data) {
        var newNode = new Node(data, head);
        head = newNode;
        size++;
    }

    public T find() {
        var node = head;

        while (node != null) {
            if(node.data.equals(data)) {
                return node.data;
            }
            node = node.next;
        }

        return null;
    }

    public int indexOf(T data) {
        var index = 0;
        var node = head;

        while (node != null) {
            if (node.data.equals(data)) {
                return index;
            }

            node = node.next;
        }

        return -1;
    }

    public void remove(int position) {
        if (position >= size) {
            throw new IllegalArgumentException();
        }

        
    }

    private class Node {
        private T data;
        private Node next;

        Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
}




