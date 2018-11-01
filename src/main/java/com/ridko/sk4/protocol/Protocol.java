package com.ridko.sk4.protocol;

/**
 * 解析SK4操作指令数据接口
 *
 * @author smitea
 */
public interface Protocol {
    /**
     * 解析数据位内容
     *
     * @param protocol SK4数据协议
     */
    void readProtocol(ReaderProtocol protocol);

    /**
     * 发送的指令内容
     *
     * @return 返回SK4数据协议
     */
    ReaderProtocol writeProtocol();

    /**
     * 获取数据响应的指令类型
     *
     * @return 返回数据响应的指令类型
     */
    int resultType();
}
