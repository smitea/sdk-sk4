package com.ridko.sk4;

import com.ridko.sk4.protocol.ReaderDecoder;
import com.ridko.sk4.protocol.ReaderEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig;

/**
 * 串口连接
 *
 * @author smitea
 * @since 2018-10-30
 */
class SerialConnection extends AbstractConnection<SerialParam> {

  protected EventLoopGroup eventLoopGroup() {
    return new OioEventLoopGroup(2);
  }

  protected void configBootstrap(Bootstrap bootstrap, EventLoopGroup group, final SerialParam serialParam) {
    bootstrap.group(group)
            .channel(RxtxChannel.class)
            // 设置接收缓冲区大小(minimum值为发送数据的最小长度<head(1)+type(1)+>len(1)+crc(1)+end1(1)+end2(1)> initial值为单条指令响应最大长度<该值越大会影响到EPC码的读取速率>)
            .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(6, 100, 100))
            .handler(new ChannelInitializer<RxtxChannel>() {
              @Override
              public void initChannel(RxtxChannel channel) throws Exception {
                RxtxChannelConfig rxtxChannelConfig = channel.config();
                rxtxChannelConfig.setBaudrate(serialParam.getBaudrate());
                rxtxChannelConfig.setDatabits(RxtxChannelConfig.Databits.valueOf(serialParam.getDatabits()));
                rxtxChannelConfig.setStopbits(RxtxChannelConfig.Stopbits.valueOf(serialParam.getStopbits()));
                rxtxChannelConfig.setParitybit(RxtxChannelConfig.Paritybit.valueOf(serialParam.getParity()));

                ChannelPipeline cp = channel.pipeline();
                cp.addLast(new ReaderEncoder());
                cp.addLast(new ReaderDecoder(0xBB, 0x0D, 0x0A));
                cp.addLast(new ReaderClientHandler());
              }
            });
  }
}
