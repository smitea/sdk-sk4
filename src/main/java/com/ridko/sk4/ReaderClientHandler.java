package com.ridko.sk4;

import com.ridko.sk4.handler.ProtocolHandler;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.protocol.ReaderProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 通道处理器
 *
 * @author smitea
 */
class ReaderClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

  private ProtocolHandler protocolHandler;

  private IListenter<ConnectEvent> listenter;

  public void setProtocolHandler(ProtocolHandler protocolHandler) {
    this.protocolHandler = protocolHandler;
  }

  public void setListenter(IListenter<ConnectEvent> listenter) {
    this.listenter = listenter;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 发生异常后关闭
    if (listenter != null) {
      if (cause instanceof ConnectTimeoutException) {
        listenter.notify(ConnectEvent.DISCONNECTION);
      } else if (cause instanceof java.io.IOException) {
        listenter.notify(ConnectEvent.DISCONNECTION);
      }
    }
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (listenter != null) {
      listenter.notify(ConnectEvent.DISCONNECTION);
    }
    super.channelInactive(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if(listenter!=null){
      listenter.notify(ConnectEvent.CONNECTED);
    }
    super.channelActive(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    if (listenter != null) {
      listenter.notify(ConnectEvent.CONNECTION);
    }
    super.channelRegistered(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    if (listenter != null) {
      listenter.notify(ConnectEvent.DISCONNECTED);
    }
    super.channelUnregistered(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
    if (protocolHandler != null) {
      try {
        int HEAD = byteBuf.readByte();
        int TYPE = byteBuf.readByte();
        int LEN = byteBuf.readByte();
        byte[] data = new byte[LEN & 0xFF];
        byteBuf.readBytes(data);
        int CRC = byteBuf.readByte();

        ReaderProtocol readerProtocol = new ReaderProtocol();
        readerProtocol.setData(data);
        readerProtocol.setCrc(CRC & 0xFF);
        readerProtocol.setType(TYPE & 0xFF);
        // 回调处理
        protocolHandler.handler(readerProtocol);
      } catch (Exception ignored) {
      }
    }
  }
}
