package com.ridko.sk4;

import com.ridko.sk4.protocol.ReaderDecoder;
import com.ridko.sk4.protocol.ReaderEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;

/**
 * @author smitea
 * @since 2018-10-30
 */
public class TcpConnectionI extends AbstractConnectionI<SocketAddress> {
  protected EventLoopGroup eventLoopGroup() {
    return new NioEventLoopGroup(2);
  }

  protected void configBootstrap(Bootstrap bootstrap, EventLoopGroup group, SocketAddress param) {
    bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, true)
            // 设置接收缓冲区大小(minimum值为发送数据的最小长度<head(1)+type(1)+>len(1)+crc(1)+end1(1)+end2(1)> initial值为单条指令响应最大长度<该值越大会影响到EPC码的读取速率>)
            .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(6, 100, 100))
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline cp = socketChannel.pipeline();
                cp.addLast(new ReaderEncoder());
                cp.addLast(new ReaderDecoder(0xBB, 0x0D, 0x0A));
                cp.addLast(new ReaderClientHandler());
              }
            });
  }
}
