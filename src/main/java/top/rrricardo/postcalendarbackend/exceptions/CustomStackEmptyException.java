package top.rrricardo.postcalendarbackend.exceptions;

/**
 * 栈中没有数据引发的异常
 */
public class CustomStackEmptyException extends Exception {
    public CustomStackEmptyException() {
        super();
    }

    public CustomStackEmptyException(String message) {
        super(message);
    }
}
