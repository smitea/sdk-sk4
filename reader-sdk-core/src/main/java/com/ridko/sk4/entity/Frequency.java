package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 跳频频点参数
 * @author smitea
 * @since 2018-10-31
 */
public class Frequency implements Serializable {
  private int msb;
  private int freq;
  private int lsb;

  private int value;

  public int getMsb() {
    return msb;
  }

  public void setMsb(int msb) {
    this.msb = msb;
  }

  public int getFreq() {
    return freq;
  }

  public void setFreq(int freq) {
    this.freq = freq;
  }

  public int getLsb() {
    return lsb;
  }

  public void setLsb(int lsb) {
    this.lsb = lsb;
  }

  public int getValue() {
    return value;
  }

  public Frequency() {
  }

  public Frequency(int value) {
     this.lsb = value & 0xFF;
     this.freq = (value & 0xFF00) >> 8;
     this.msb = (value & 0xFF0000) >> 16;
     this.value = value;
  }

  public Frequency(int msb, int freq, int lsb) {
    this.msb = msb;
    this.freq = freq;
    this.lsb = lsb;
    this.value = (msb << 16) | (freq << 8) | (lsb);
  }
}
