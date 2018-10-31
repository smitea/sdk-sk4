package com.ridko.sk4.error;

/**
 * 获取参数失败异常信息
 * @author smitea
 * @since 2018-10-31
 */
public class GetException extends RuntimeException {
  public GetException(String message) {
    super(message);
  }
}
