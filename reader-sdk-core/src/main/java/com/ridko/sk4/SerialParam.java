package com.ridko.sk4;

import gnu.io.CommPortIdentifier;
import io.netty.channel.rxtx.RxtxDeviceAddress;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

  /**
   * 查找所有可用串口
   *
   * @return 可用串口名称列表
   */
  @SuppressWarnings("unchecked")
  public static List<String> findPort() {
    // 获得当前所有可用串口
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    ArrayList<String> portNameList = new ArrayList<String>();
    // 将可用串口名添加到List并返回该List
    while (portList.hasMoreElements()) {
      String portName = portList.nextElement().getName();
      portNameList.add(portName);
    }
    return portNameList;
  }

  public String address() {
    return this.portName;
  }
}
