package com.ridko.sk4.listenter;

/**
 * 连接状态
 *
 * @author smitea
 * @since 2018-10-30
 */
public enum ConnectEvent {
  CONNECTION("正在连接中"),
  CONNECTED("已连接"),
  RETRY_CONNECTION("重试连接"),
  DISCONNECTION("正在断开连接"),
  DISCONNECTED("连接已断开");
  private String msg;

  ConnectEvent(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
