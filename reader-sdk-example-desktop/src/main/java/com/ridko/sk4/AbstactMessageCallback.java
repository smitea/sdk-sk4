package com.ridko.sk4;

/**
 * @author smitea
 * @since 2018-11-03
 */
public abstract class AbstactMessageCallback {
  private IMessageCallback messageCallback;

  protected void notify(String message,String color){
    synchronized (AbstactMessageCallback.class) {
      if (messageCallback != null) {
        messageCallback.notify(message, color);
      }
    }
  }

  public void setMessageCallback(IMessageCallback messageCallback) {
    this.messageCallback = messageCallback;
  }
}
