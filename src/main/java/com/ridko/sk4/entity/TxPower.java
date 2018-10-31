package com.ridko.sk4.entity;

import com.ridko.sk4.error.OutOfRangeException;

import java.io.Serializable;

/**
 * 功率参数
 * @author smitea
 * @since 2018-10-31
 */
public class TxPower implements Serializable {
  /** 开环状态 true 开环| false 闭环 */
  private boolean isLoop = false;
  /** 读功率 */
  private int readPower;
  /** 写功率 */
  private int writePower;

  public boolean isLoop() {
    return isLoop;
  }

  public void setLoop(boolean loop) {
    isLoop = loop;
  }

  public int getReadPower() {
    return readPower;
  }

  public void setReadPower(int readPower) {
    if(readPower < 5 || readPower > 30){
      throw new OutOfRangeException("the value must be 5-30 dbm ");
    }
    this.readPower = readPower;
  }

  public int getWritePower() {
    return writePower;
  }

  public void setWritePower(int writePower) {
    if(writePower < 5 || writePower > 30){
      throw new OutOfRangeException("the value must be 5-30 dbm ");
    }
    this.writePower = writePower;
  }

  public TxPower() {
  }

  public TxPower(boolean isLoop, int readPower, int writePower) {
    this.isLoop = isLoop;
    this.readPower = readPower;
    this.writePower = writePower;
  }
}
