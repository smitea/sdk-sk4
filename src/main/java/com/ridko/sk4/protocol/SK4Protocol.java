package com.ridko.sk4.protocol;

/**
 * SK4通信协议格式
 *
 * @author smitea
 * @since 2018-10-30
 */
public class SK4Protocol {
  /**
   * 指令头
   */
  private final int head = 0xBB;
  /**
   * 指令类型
   */
  private int type;
  /**
   * 数据位长度
   */
  private int len;
  /**
   * 数据位内容
   */
  private byte[] data;
  /**
   * CRC校验位
   */
  private int crc;
  /**
   * 结束标志位1
   */
  private final int end1 = 0x0D;
  /**
   * 结束标志位2
   */
  private final int end2 = 0x0A;

  public int getHead() {
    return head;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public int getCrc() {
    return crc;
  }

  public void setCrc(int crc) {
    this.crc = crc;
  }

  public int getEnd1() {
    return end1;
  }

  public int getEnd2() {
    return end2;
  }

  public SK4Protocol() {
  }

  public SK4Protocol(int type, int len, byte[] data) {
    this.type = type;
    this.len = len;
    this.data = data;
  }
}
