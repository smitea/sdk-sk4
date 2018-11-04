package com.ridko.sk4;

/**
 * 消息通知回调接口
 * @author smitea
 * @since 2018-11-03
 */
public interface IMessageCallback {
  /** 通知消息 */
  public void notify(String message,String color);
}
