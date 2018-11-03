package com.ridko.sk4.entity;

/**
 * 数据的bank号
 * @author smitea
 * @since 2018-10-31
 */
public enum BankNo {
  EPC(0x01),
  TID(0x02),
  USER(0x03),
  EXTEND(0x04);

  private int value;

  BankNo(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static BankNo fromValue(int value) {
    for (BankNo event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return BankNo.EPC;
  }
}
