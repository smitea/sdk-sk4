package com.ridko.sk4.entity;

/**
 * 模块通讯波特率
 * @author smitea
 * @since 2018-11-01
 */
public enum BaudRate {
  B_9600(0),
  B_19200(1),
  B_38400(2),
  B_57600(3),
  B_115200(4);

  private int value;

  BaudRate(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static BaudRate fromValue(int value) {
    for (BaudRate event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return BaudRate.B_115200;
  }
}
