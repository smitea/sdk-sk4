package com.ridko.sk4.error;

/**
 * 参数超出范围异常
 * @author smitea
 * @since 2018-10-31
 */
public class OutOfRangeException extends RuntimeException {
  public OutOfRangeException(String message) {
    super(message);
  }
}
