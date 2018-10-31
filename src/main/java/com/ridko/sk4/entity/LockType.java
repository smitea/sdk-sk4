package com.ridko.sk4.entity;

/**
 * 锁定方式
 * @author smitea
 * @since 2018-10-31
 */
public enum  LockType {
  LOCK(0x01),
  UNLOCK(0x02),
  LOCK_FOREVER(0x03),
  UNLOCK_FOREVER(0x04);
  private int value;

  LockType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
