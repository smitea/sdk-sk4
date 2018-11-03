package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 通道门进出人数
 * @author smitea
 * @since 2018-11-02
 */
public class ChannelDoorCountNUm implements Serializable {
  /** 入馆人数统计 */
  private int inCount;
  /** 出馆人数统计 */
  private int outCount;

  public ChannelDoorCountNUm(int inCount, int outCount) {
    this.inCount = inCount;
    this.outCount = outCount;
  }

  public int getInCount() {
    return inCount;
  }

  public void setInCount(int inCount) {
    this.inCount = inCount;
  }

  public int getOutCount() {
    return outCount;
  }

  public void setOutCount(int outCount) {
    this.outCount = outCount;
  }
}
