package com.ridko.sk4.entity;

/**
 * 工作模式
 *
 * @author smitea
 * @since 2018-11-01
 */
public enum WorkMode {
  /** 标准状态 */
  NONE(0x00),
  /** EAS 模式 */
  EAS_MODE(0x01),
  /** AUTO 模式 */
  AUTO_MODE(0x02),
  /** 韦根输出模式 */
  WIEGANG_MODE(0x03),
  /** 过滤模式 */
  FLITER_MODE(0x04),
  /** 触发模式 */
  TIGGER_MODE(0x05),
  /** 通道门模式 */
  CHANNEL_GATE_MODE(0x06),
  /** 存储模式 */
  STORE_MODE(0x07);
  private int value;

  WorkMode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static WorkMode fromValue(int value) {
    for (WorkMode event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return WorkMode.NONE;
  }
}
