package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 分支器天线工作功率参数
 *
 * @author smitea
 * @since 2018-11-01
 */
public class BranchAntPowerParam implements Serializable {
  /** 天线索引 */
  private int index;
  /** 功率大小（单位 dbm，范围 1dbm—30dbm） */
  private int power;

  public BranchAntPowerParam(int index, int power) {
    this.index = index;
    this.power = power;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getPower() {
    return power;
  }

  public void setPower(int power) {
    this.power = power;
  }

  public static int indexOfPower(int index, BranchAntPowerParam... params) {
    if (params != null) {
      for (BranchAntPowerParam _param : params) {
        if (_param.index == index) {
          return _param.power;
        }
      }
    }
    return -1;
  }
}
