package com.ridko.sk4;

/**
 * 消息处理回调
 * @author smitea
 * @since 2018-11-03
 */
public abstract class AbstactMessageCallback {
  private IMessageCallback messageCallback;

  /**
   * 通知消息到消息面板
   * @param message 消息内容
   * @param color 消息显示颜色
   */
  protected void notify(String message,String color){
    synchronized (AbstactMessageCallback.class) {
      if (messageCallback != null) {
        messageCallback.notify(message, color);
      }
    }
  }

  /** 设置消息回调监听 */
  public void setMessageCallback(IMessageCallback messageCallback) {
    this.messageCallback = messageCallback;
  }
}
