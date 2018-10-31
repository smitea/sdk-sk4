package com.ridko.sk4.entity;

/**
 * 频率区域
 * @author smitea
 * @since 2018-10-31
 */
public enum  FrequencyRegion {
  CHINA_1(0x01),
  CHINA_2(0x02),
  EUROPE(0x03),
  USA(0x04),
  KOREA(0x05),
  JAPAN(0x06);
  private int value;

  FrequencyRegion(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static FrequencyRegion fromValue(int value) {
    for (FrequencyRegion event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return FrequencyRegion.CHINA_1;
  }
}
