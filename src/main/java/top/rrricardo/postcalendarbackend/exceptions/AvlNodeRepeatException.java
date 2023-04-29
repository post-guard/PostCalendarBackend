package top.rrricardo.postcalendarbackend.exceptions;

/**
 * Avl树中的节点重复错误
 */
public class AvlNodeRepeatException extends Exception {
    public AvlNodeRepeatException() {
        super();
    }

    public AvlNodeRepeatException(String message) {
        super(message);
    }
}
