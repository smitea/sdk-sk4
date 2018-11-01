package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * EAS参数
 * @author smitea
 * @since 2018-11-01
 */
public class EAS implements Serializable {
  /** EAS 区选择的字节（0-12） */
  private int bit;
  /** EAS 的值 （0x00~0xFF）*/
  private int value;

  public EAS() {
  }

  public EAS(int bit, int value) {
    this.bit = bit;
    this.value = value;
  }

  public int getBit() {
    return bit;
  }

  public void setBit(int bit) {
    this.bit = bit;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
