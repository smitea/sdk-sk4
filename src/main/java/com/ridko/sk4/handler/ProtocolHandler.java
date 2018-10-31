package com.ridko.sk4.handler;

import com.ridko.sk4.protocol.SK4Protocol;

/**
 * 指令分发器
 * @author smitea
 * @since 2018-10-30
 */
public interface ProtocolHandler {
  public void handler(SK4Protocol sk4Protocol);
}
