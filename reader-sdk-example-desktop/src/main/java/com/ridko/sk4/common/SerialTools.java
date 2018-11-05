package com.ridko.sk4.common;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 串口工具
 * @author smitea
 * @since 2018-11-05
 */
public class SerialTools {
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
}
