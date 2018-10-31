package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 标签数据(筛选)
 * @author smitea
 * @since 2018-10-31
 */
public class TagData implements Serializable {
  /** 标签数据 */
  private byte[] data;
  /** 天线号 */
  private int ant;

  public TagData() {
  }

  public TagData(byte[] data, int ant) {
    this.data = data;
    this.ant = ant;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public int getAnt() {
    return ant;
  }

  public void setAnt(int ant) {
    this.ant = ant;
  }
}
