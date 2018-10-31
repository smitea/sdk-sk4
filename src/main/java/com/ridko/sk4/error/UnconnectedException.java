package com.ridko.sk4.error;

/**
 * 设备未连接异常信息
 * @author smitea
 * @since 2018-10-31
 */
public class UnconnectedException extends RuntimeException {
  public UnconnectedException(String message) {
    super(message);
  }
}
