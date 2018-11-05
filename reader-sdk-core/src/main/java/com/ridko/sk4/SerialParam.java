package com.ridko.sk4;

import io.netty.channel.rxtx.RxtxDeviceAddress;

/**
 * 串口连接参数
 *
 * @author smitea
 * @since 2018-10-30
 */
public class SerialParam extends RxtxDeviceAddress {
  /** 端口号 */
  private String portName;
  /** 波特率 */
  private int baudrate = 115200;
  /** 数据位 */
  private int databits = 8;
  /** 停止位 */
  private int stopbits = 1;
  /** 校验位 */
  private int parity = 0;

  public SerialParam(String value) {
    super(value);
    this.portName = value;
  }

  public String getPortName() {
    return portName;
  }

  public void setPortName(String portName) {
    this.portName = portName;
  }

  public int getBaudrate() {
    return baudrate;
  }

  public void setBaudrate(int baudrate) {
    this.baudrate = baudrate;
  }

  public int getDatabits() {
    return databits;
  }

  public void setDatabits(int databits) {
    this.databits = databits;
  }

  public int getStopbits() {
    return stopbits;
  }

  public void setStopbits(int stopbits) {
    this.stopbits = stopbits;
  }

  public int getParity() {
    return parity;
  }

  public void setParity(int parity) {
    this.parity = parity;
  }

  public String address() {
    return this.portName;
  }

  @Override
  public String value() {
    return this.portName;
  }
}
