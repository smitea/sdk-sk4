package com.ridko.sk4.error;

/**
 * 设置参数失败异常信息
 * @author smitea
 * @since 2018-10-31
 */
public class SetException extends RuntimeException {
  public SetException(String message) {
    super(message);
  }
}
