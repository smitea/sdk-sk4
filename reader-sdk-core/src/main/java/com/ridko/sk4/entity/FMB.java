package com.ridko.sk4.entity;

/**
 * 过滤数据类型
 * @author smitea
 * @since 2018-10-31
 */
public enum FMB {
  EPC(0x00),
  TID(0x01);
  private int value;

  FMB(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static FMB fromValue(int value) {
    for (FMB event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return FMB.EPC;
  }
}
