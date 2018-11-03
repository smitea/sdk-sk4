package com.ridko.sk4.entity;

/**
 * 通道门进出返回值
 *
 * @author smitea
 * @since 2018-11-01
 */
public enum ChannelValue {
  IN(0x00),
  OUT(0x01),
  ERROR(0x02);

  private int value;

  ChannelValue(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static ChannelValue fromValue(int value) {
    for (ChannelValue event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return ChannelValue.ERROR;
  }
}
