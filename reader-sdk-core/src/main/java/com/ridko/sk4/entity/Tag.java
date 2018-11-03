package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 标签数据
 * @author smitea
 * @since 2018-10-30
 */
public class Tag implements Serializable{
  /** PC号 */
  private int pc;
  /** 天线号 */
  private int ant;
  /** 场强值 */
  private double rssi;
  /** EPC码 */
  private String epc;

  public Tag() {
  }

  public Tag(int pc, int ant, double rssi, String epc) {
    this.pc = pc;
    this.ant = ant;
    this.rssi = rssi;
    this.epc = epc;
  }

  public int getPc() {
    return pc;
  }

  public void setPc(int pc) {
    this.pc = pc;
  }

  public int getAnt() {
    return ant;
  }

  public void setAnt(int ant) {
    this.ant = ant;
  }

  public double getRssi() {
    return rssi;
  }

  public void setRssi(double rssi) {
    this.rssi = rssi;
  }

  public String getEpc() {
    return epc;
  }

  public void setEpc(String epc) {
    this.epc = epc;
  }
}
