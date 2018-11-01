package com.ridko.sk4;

import java.net.SocketAddress;

/**
 * 获取读写器连接器
 * @author smitea
 * @since 2018-11-01
 */
public class ReaderConnectionBuild {
  public static IReaderConnection<SerialParam> createSerialConnection(){
    return new SerialConnection();
  }

  public static IReaderConnection<SocketAddress> createTcpConnection(){
    return new TcpConnection();
  }
}
