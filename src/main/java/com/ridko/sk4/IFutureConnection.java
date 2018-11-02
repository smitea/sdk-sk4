package com.ridko.sk4;

import com.ridko.sk4.promise.Future;

import java.net.SocketAddress;

/**
 * 连接器
 *
 * @author smitea
 * @since 2018-10-30
 */
public interface IFutureConnection<Option extends SocketAddress> {
  /**
   * 是否已连接
   *
   * @return 已连接返回true, 已断开返回false
   */
  public boolean isConnected();

  /**
   * 连接设备
   * @return 返回Future
   */
  public Future<ICommand> connect(Option option);

  /**
   * 断开设备
   * @return 返回Future
   */
  public void disconnect();
}
