package com.promobrain.common.response;

/**
 * 统一 API 响应结构。
 * 第一版先保证前后端契约稳定，后续接入异常码、国际化或链路追踪时也从这里扩展。
 */
public record Result<T>(boolean success, String code, String message, T data) {

    /**
     * 成功响应，适合返回业务数据。
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, "OK", "success", data);
    }

    /**
     * 成功响应，适合无返回体的写操作。
     */
    public static Result<Void> ok() {
        return new Result<>(true, "OK", "success", null);
    }

    /**
     * 失败响应入口，后续可统一接入业务错误码枚举。
     */
    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(false, code, message, null);
    }
}
