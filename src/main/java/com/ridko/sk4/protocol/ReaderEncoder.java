package com.ridko.sk4.protocol;

import com.ridko.sk4.common.PropertyTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * SK4数据编码器
 * 将自定义数据协议内容按照指定格式发送给设备
 * @author smitea
 * @see ReaderProtocol
 */
public class ReaderEncoder extends MessageToByteEncoder<ReaderProtocol> {

    private boolean isDebug =  PropertyTools.getProperty("sk4.debug", false);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ReaderProtocol readerProtocol, ByteBuf out) throws Exception {
        // 写入指令头
        out.writeByte(readerProtocol.getHead());
        // 写入指令类型
        out.writeByte(readerProtocol.getType());
        // 写入指令长度
        out.writeByte(readerProtocol.getLen());
        // 写入数据位
        if (readerProtocol.getData() != null) {
            out.writeBytes(readerProtocol.getData());
        }
        // 写入CRC位
        out.writeByte(readerProtocol.getCrc());
        // 写入结束位1
        out.writeByte(readerProtocol.getEnd1());
        // 写入结束位2
        out.writeByte(readerProtocol.getEnd2());

        if(isDebug) {
            System.out.println("send: " + ByteBufUtil.hexDump(out).toUpperCase());
        }
    }
}
