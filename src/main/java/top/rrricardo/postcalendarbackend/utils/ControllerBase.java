package top.rrricardo.postcalendarbackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;

public class ControllerBase {
    protected static <T> ResponseEntity<ResponseDTO<T>> ok() {
        return new ResponseEntity<>(new ResponseDTO<>("操作成功"), HttpStatus.OK);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> ok(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.OK);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> ok(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("操作成功", data),
                HttpStatus.OK);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> ok(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.OK);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> created() {
        return new ResponseEntity<>(new ResponseDTO<>("创建成功"), HttpStatus.CREATED);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> created(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.CREATED);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> created(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("创建成功", data),
                HttpStatus.CREATED);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> created(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.CREATED);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> noContent() {
        return new ResponseEntity<>(new ResponseDTO<>("删除成功"), HttpStatus.NO_CONTENT);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> noContent(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.NO_CONTENT);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> noContent(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("删除成功", data),
                HttpStatus.NO_CONTENT);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> noContent(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.NO_CONTENT);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> badRequest() {
        return new ResponseEntity<>(new ResponseDTO<>("请求错误"), HttpStatus.BAD_REQUEST);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> badRequest(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.BAD_REQUEST);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> badRequest(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("请求错误", data),
                HttpStatus.BAD_REQUEST);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> badRequest(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.BAD_REQUEST);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> forbidden() {
        return new ResponseEntity<>(new ResponseDTO<>("权限错误，禁止访问"), HttpStatus.FORBIDDEN);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> forbidden(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.FORBIDDEN);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> forbidden(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("权限错误，禁止访问", data),
                HttpStatus.FORBIDDEN);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> forbidden(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.FORBIDDEN);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> notFound() {
        return new ResponseEntity<>(new ResponseDTO<>("访问的资源不存在"), HttpStatus.NOT_FOUND);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> notFound(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.NOT_FOUND);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> notFound(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("访问的资源不存在", data),
                HttpStatus.NOT_FOUND);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> notFound(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.NOT_FOUND);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> internalError() {
        return new ResponseEntity<>(new ResponseDTO<>("服务器内部错误，请联系管理员"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> internalError(String message) {
        return new ResponseEntity<>(new ResponseDTO<>(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> internalError(T data) {
        return new ResponseEntity<>(new ResponseDTO<>("服务器内部错误，请联系管理员", data),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected static <T> ResponseEntity<ResponseDTO<T>> internalError(String message, T data) {
        return new ResponseEntity<>(new ResponseDTO<>(message, data),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
