package top.rrricardo.postcalendarbackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    /**
     * 返回HTTP 200响应
     */
    public static <T> ResponseEntity<T> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 返回HTTP 200响应
     */
    public static <T> ResponseEntity<T> ok(T data) {
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * 返回HTTP 201 响应
     */
    public static <T> ResponseEntity<T> created() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 返回HTTP 201 响应
     */
    public static <T> ResponseEntity<T> created(T data) {
        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

    /**
     * 返回HTTP 204 响应
     */
    public static <T> ResponseEntity<T> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 返回HTTP 204 响应
     */
    public static <T> ResponseEntity<T> noContent(T data) {
        return new ResponseEntity<>(data, HttpStatus.NO_CONTENT);
    }

    /**
     * 返回HTTP 400 响应
     */
    public static <T> ResponseEntity<T> badRequest() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * 返回HTTP 400 响应
     */
    public static <T> ResponseEntity<T> badRequest(T data) {
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    /**
     * 返回HTTP 401 响应
     */
    public static <T> ResponseEntity<T> unauthorized() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * 返回HTTP 401 响应
     */
    public static <T> ResponseEntity<T> unauthorized(T data) {
        return new ResponseEntity<>(data, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 返回HTTP 403 响应
     */
    public static <T> ResponseEntity<T> forbidden() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * 返回HTTP 403 响应
     */
    public static <T> ResponseEntity<T> forbidden(T data) {
        return new ResponseEntity<>(data, HttpStatus.FORBIDDEN);
    }

    /**
     * 返回HTTP 404 响应
     */
    public static <T> ResponseEntity<T> notFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 返回HTTP 404 响应
     */
    public static <T> ResponseEntity<T> notFound(T data) {
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }
}
