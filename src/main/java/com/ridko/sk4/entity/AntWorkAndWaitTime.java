package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 天线工作时间及等待时间参数
 * @author smitea
 * @since 2018-10-31
 */
public class AntWorkAndWaitTime implements Serializable {
  private int ant1WorkTime;
  private int ant2WorkTime;
  private int ant3WorkTime;
  private int ant4WorkTime;
  private int waitTime;

  public AntWorkAndWaitTime() {
  }

  public AntWorkAndWaitTime(int ant1WorkTime, int ant2WorkTime, int ant3WorkTime, int ant4WorkTime, int waitTime) {
    this.ant1WorkTime = ant1WorkTime;
    this.ant2WorkTime = ant2WorkTime;
    this.ant3WorkTime = ant3WorkTime;
    this.ant4WorkTime = ant4WorkTime;
    this.waitTime = waitTime;
  }

  public int getAnt1WorkTime() {
    return ant1WorkTime;
  }

  public void setAnt1WorkTime(int ant1WorkTime) {
    this.ant1WorkTime = ant1WorkTime;
  }

  public int getAnt2WorkTime() {
    return ant2WorkTime;
  }

  public void setAnt2WorkTime(int ant2WorkTime) {
    this.ant2WorkTime = ant2WorkTime;
  }

  public int getAnt3WorkTime() {
    return ant3WorkTime;
  }

  public void setAnt3WorkTime(int ant3WorkTime) {
    this.ant3WorkTime = ant3WorkTime;
  }

  public int getAnt4WorkTime() {
    return ant4WorkTime;
  }

  public void setAnt4WorkTime(int ant4WorkTime) {
    this.ant4WorkTime = ant4WorkTime;
  }

  public int getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }
}
