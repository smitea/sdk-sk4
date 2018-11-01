package com.ridko.sk4;

import com.ridko.sk4.common.HexTools;
import com.ridko.sk4.entity.ChannelValue;
import com.ridko.sk4.entity.Tag;
import com.ridko.sk4.error.UnconnectedException;
import com.ridko.sk4.handler.ProtocolHandler;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ErrorEvent;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.listenter.ITagListenter;
import com.ridko.sk4.promise.Callback;
import com.ridko.sk4.promise.Future;
import com.ridko.sk4.promise.Promise;
import com.ridko.sk4.protocol.Protocol;
import com.ridko.sk4.protocol.ReaderProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接器
 *
 * @author smitea
 * @since 2018-10-30
 */
abstract class AbstractConnection<Option extends SocketAddress> implements IReaderConnection<Option> {
  /** 连接事件监听 */
  private IListenter<ConnectEvent> connectEventIListenter;
  /** 防盗门进出监听 */
  private IListenter<ChannelValue> channelValueIListenter;
  /** 错误信息监听 */
  private IListenter<ErrorEvent> errorEventIListenter;
  /** 询读监听 */
  private ITagListenter tagListenter;

  /** IO启动对象 */
  private Bootstrap bootstrap;
  /** 线程组 */
  private EventLoopGroup eventLoopGroup;

  /** 写通道 */
  private Channel writeChannel = null;
  /** 连接状态标识 */
  private boolean isConnected = false;
  /** 询读状态标识 */
  private boolean isStarted = false;

  /** 指令回调处理集合 */
  private Map<Integer, Protocol> protocolHandlerMap;

  public AbstractConnection() {
    bootstrap = new Bootstrap();
    eventLoopGroup = eventLoopGroup();
    protocolHandlerMap = new ConcurrentHashMap<Integer, Protocol>();
  }

  /** 创建线程组 */
  protected abstract EventLoopGroup eventLoopGroup();

  /** 配置IO启动对象 */
  protected abstract void configBootstrap(Bootstrap bootstrap, EventLoopGroup group, Option param);

  /** 通知网络状态 */
  private void notifyConnectEvent(ConnectEvent event) {
    if (connectEventIListenter != null) {
      connectEventIListenter.notify(event);
    }
  }

  /** 设置连接状态监听器 */
  public void setConnectEventIListenter(IListenter<ConnectEvent> connectEventIListenter) {
    synchronized (AbstractConnection.class) {
      this.connectEventIListenter = connectEventIListenter;
    }
  }

  /** 设置标签巡查监听器 */
  public void setTagListenter(ITagListenter tagListenter) {
    synchronized (AbstractConnection.class) {
      this.tagListenter = tagListenter;
    }
  }

  /** 设置防盗门进出监听器 */
  public void setChannelValueIListenter(IListenter<ChannelValue> channelValueIListenter) {
    synchronized (AbstractConnection.class) {
      this.channelValueIListenter = channelValueIListenter;
    }
  }

  /** 设置错误信息处理监听器 */
  public void setErrorEventIListenter(IListenter<ErrorEvent> errorEventIListenter) {
    this.errorEventIListenter = errorEventIListenter;
  }

  /** 是否已连接 */
  public boolean isConnected() {
    return isConnected;
  }

  /** 是否在询读中 */
  public boolean isStarted() {
    return isStarted;
  }

  public Future<ICommand> connect(Option option) {
    final Promise<ICommand> promise = new Promise<ICommand>();

    final AbstractConnection abstractConnection = this;

    try {
      notifyConnectEvent(ConnectEvent.CONNECTION);

      // 配置启动项
      configBootstrap(bootstrap, eventLoopGroup, option);
      ChannelFuture connect = bootstrap.connect(option);
      // 添加连接监听
      connect.addListener(new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
          if (channelFuture.isSuccess()) {
            // 触发成功消息
            promise.onSuccess(new ReaderClient(abstractConnection));
            // 设置已连接状态
            isConnected = true;

            // 获取写通道
            writeChannel = channelFuture.channel();
            // 获取读通道
            ReaderClientHandler readerClientHandler = writeChannel.pipeline().get(ReaderClientHandler.class);
            // 通知已连接消息
            notifyConnectEvent(ConnectEvent.CONNECTED);

            // 添加错误信息处理
            protocolHandlerMap.put(0xFF, new Protocol() {
              public void readProtocol(ReaderProtocol protocol) {
                byte[] data = protocol.getData();
                int _msb = data[0] << 8;
                int _lsb = data[1];
                int code = _msb | _lsb;
                if (errorEventIListenter != null) {
                  errorEventIListenter.notify(ErrorEvent.toError(code));
                }
              }

              public ReaderProtocol writeProtocol() {
                return null;
              }

              public int resultType() {
                return 0xFF;
              }
            });

            // 心跳包信息处理
            protocolHandlerMap.put(0xFE, new Protocol() {
              public void readProtocol(ReaderProtocol protocol) {
                if (protocol.getLen() == 0x04) {
                  // 发送心跳包
                  notifyConnectEvent(ConnectEvent.HEART_BEAT);
                } else if (protocol.getLen() == 0x01) {
                  if (channelValueIListenter != null) {
                    // 发送通道门检测事件
                    channelValueIListenter.notify(ChannelValue.fromValue(protocol.getData()[0]));
                  }
                }
              }

              @Deprecated
              public ReaderProtocol writeProtocol() {
                return null;
              }

              @Deprecated
              public int resultType() {
                return 0;
              }
            });

            // 设置数据回调处理
            readerClientHandler.setProtocolHandler(new ProtocolHandler() {
              public void handler(ReaderProtocol readerProtocol) {
                // 获取响应指令类型
                int type = readerProtocol.getType();
                // 获取对应指令类型的解析器
                Protocol protocol = protocolHandlerMap.get(type);
                if (protocol != null) {
                  // 解析数据
                  try {
                    protocol.readProtocol(readerProtocol);
                  } catch (Exception ignored) {
                  }
                }
              }
            });

            // 设置连接状态监听
            readerClientHandler.setListenter(connectEventIListenter);
          } else {
            // 触发失败消息
            promise.onFailure(channelFuture.cause());
            // 设置未连接状态
            isConnected = false;
            // 通知已断开连接消息
            notifyConnectEvent(ConnectEvent.DISCONNECTED);
          }
        }
      });
    } catch (Exception e) {
      // 触发失败消息
      promise.onFailure(e);
      // 通知已断开连接消息
      notifyConnectEvent(ConnectEvent.DISCONNECTED);
    }
    return promise;
  }

  public Future<Void> disconnect() {
    final Promise<Void> promise = new Promise<Void>();

    // 通知正在断开连接的消息
    notifyConnectEvent(ConnectEvent.DISCONNECTION);
    // 停止询读
    stop().then(new Callback<Void>() {
      public void onSuccess(Void value) {
        // 关闭线程
        eventLoopGroup.shutdownGracefully();
        bootstrap = null;
        eventLoopGroup = null;
        writeChannel = null;

        isConnected = false;

        promise.onSuccess(value);
      }

      public void onFailure(Throwable value) {
        promise.onFailure(value);
      }
    });
    return promise;
  }

  public void start() {
    send(new Protocol() {
      public void readProtocol(ReaderProtocol protocol) {
        // 解析EPC数据 并回调
        notifyData(protocol.getData());
      }

      public ReaderProtocol writeProtocol() {
        // 开始询读指令
        final ReaderProtocol protocol = new ReaderProtocol();
        protocol.setType(0x17);
        protocol.setLen(0x02);
        protocol.setData(new byte[]{0x00, 0x00});
        protocol.setCrc(0x19);
        return protocol;
      }

      public int resultType() {
        return 0x97;
      }
    });
    // 设置正在询读状态
    isStarted = true;
  }

  public Future<Void> stop() {
    final Promise<Void> promise = new Promise<Void>();
    send(new Protocol() {
      public void readProtocol(ReaderProtocol protocol) {
        // 设置已准备就绪状态
        isStarted = false;
        promise.onSuccess(null);
      }

      public ReaderProtocol writeProtocol() {
        // 设置停止询读指令
        final ReaderProtocol protocol = new ReaderProtocol();
        protocol.setType(0x18);
        protocol.setLen(0x00);
        protocol.setData(null);
        protocol.setCrc(0x18);
        return protocol;
      }

      public int resultType() {
        return 0x98;
      }
    });
    return promise;
  }

  /** 发送指令 */
  protected void send(Protocol protocol) {
    try {
      // 每次发送指令之前需要先停止120ms左右的时间让设备准备执行下一条指令,否则发送指令则在设备上不执行
      Thread.sleep(120);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (writeChannel != null) {
      // 获取发送的指令
      ReaderProtocol readerProtocol = protocol.writeProtocol();
      // 设置CRC校验位
      if (readerProtocol.getCrc() == 0) {
        HexTools.crc16(readerProtocol);
      }
      // 发送指令
      writeChannel.writeAndFlush(readerProtocol);
      // 设置数据回调处理器
      protocolHandlerMap.put(protocol.resultType(), protocol);
    } else {
      throw new UnconnectedException("未连接设备");
    }
  }

  /** 解析EPC数据 */
  private void notifyData(byte[] data) {
    if (tagListenter != null) {
      // 天线号
      int ant = data[data.length - 1];
      if (ant > 8 || ant < 1) {
        return;
      }
      // PC
      int pc = HexTools.convertByteToInt(new byte[]{data[1], data[0]});
      int epcLen = data.length - 5;
      byte[] _epc = new byte[epcLen];
      System.arraycopy(data, 2, _epc, 0, epcLen);
      // EPC码
      String epc = HexTools.byteArrayToHexString(_epc);

      // 场强值
      double rssi = ((data[data.length - 3] << 8) + (int) data[data.length - 2]) / 10.00;

      Tag tag = new Tag(pc, ant, rssi, epc);
      tagListenter.notify(tag);
    }
  }
}
