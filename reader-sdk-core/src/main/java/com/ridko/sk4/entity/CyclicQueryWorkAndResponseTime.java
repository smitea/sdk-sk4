package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 循环查询标签工作时间及间断时间设置
 *
 * @author smitea
 * @since 2018-10-31
 */
public class CyclicQueryWorkAndResponseTime implements Serializable {
  /** 当前工作时间设置 */
  private int workTime;
  /** 间断时间设置 */
  private int interruptedTime;

  public CyclicQueryWorkAndResponseTime() {
  }

  public CyclicQueryWorkAndResponseTime(int workTime, int interruptedTime) {
    this.workTime = workTime;
    this.interruptedTime = interruptedTime;
  }

  public int getWorkTime() {
    return workTime;
  }

  public void setWorkTime(int workTime) {
    this.workTime = workTime;
  }

  public int getInterruptedTime() {
    return interruptedTime;
  }

  public void setInterruptedTime(int interruptedTime) {
    this.interruptedTime = interruptedTime;
  }
}
