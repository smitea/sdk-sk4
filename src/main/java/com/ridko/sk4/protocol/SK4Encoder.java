package com.ridko.sk4.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * SK4数据编码器
 * 将自定义数据协议内容按照指定格式发送给设备
 * @author smitea
 * @see SK4Protocol
 */
public class SK4Encoder extends MessageToByteEncoder<SK4Protocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SK4Protocol sk4Protocol, ByteBuf out) throws Exception {
        // 写入指令头
        out.writeByte(sk4Protocol.getHead());
        // 写入指令类型
        out.writeByte(sk4Protocol.getType());
        // 写入指令长度
        out.writeByte(sk4Protocol.getLen());
        // 写入数据位
        if (sk4Protocol.getData() != null) {
            out.writeBytes(sk4Protocol.getData());
        }
        // 写入CRC位
        out.writeByte(sk4Protocol.getCrc());
        // 写入结束位1
        out.writeByte(sk4Protocol.getEnd1());
        // 写入结束位2
        out.writeByte(sk4Protocol.getEnd2());

        System.out.println("send: "+ByteBufUtil.hexDump(out).toUpperCase());
    }
}
