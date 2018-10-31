package com.ridko.sk4.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * SK4解码器
 * Netty内置DelimiterDecoder解码器无法满足当前数据解码速率的要求，故以此来作为默认的数据解码器，两位数据结束符在查找到后被丢弃
 *
 * @author smitea
 */
public class SK4Decoder extends ByteToMessageDecoder {

    private volatile int head;
    private volatile int end1;
    private volatile int end2;

    /**
     * 初始化解码器
     *
     * @param head 数据头标识
     * @param end1 数据结束符1
     * @param end2 数据结束符2
     */
    public SK4Decoder(int head, int end1, int end2) {
        this.head = head;
        this.end1 = end1;
        this.end2 = end2;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 获取当前缓冲区读取索引
        int readerIndex = in.readerIndex();
        // 获取当前缓冲区大小
        int length = in.capacity();

        // 记录查找到的数据头位置
        int headIndex = -1;
        // 记录查找到的数据结束符位置
        int endIndex = -1;
        for (int bit = readerIndex; bit < length; bit++) {
            // 获取数据内容
            byte aByte = in.getByte(bit);
            // 查找数据头
            if ((aByte & 0xFF) == this.head) {
                headIndex = bit;
            }
            // 查找数据结束符
            if (aByte == this.end1 && (bit + 1 < length)) {
                byte end2 = in.getByte(bit + 1);
                if (end2 == this.end2) {
                    endIndex = bit;
                }
            }
            // 截取数据
            if (headIndex != -1 && endIndex > (bit - 1)) {
                // 设置缓冲区读索引
                in.setIndex(headIndex, bit);
                ByteBuf data = in.readBytes(endIndex - headIndex);

                System.out.println("receive:"+ ByteBufUtil.hexDump(data).toUpperCase());

                out.add(data);
            }
        }
    }
}
