package com.ridko.sk4;

import java.net.SocketAddress;

/**
 * 读写器连接接口
 *
 * @author smitea
 * @since 2018-11-01
 */
public interface IReaderConnection<Option extends SocketAddress> extends IControl, IFutureConnection<Option>,INotification {
}
