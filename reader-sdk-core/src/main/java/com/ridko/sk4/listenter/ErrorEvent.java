package com.ridko.sk4.listenter;

/**
 * 错误状态码
 *
 * @author smitea
 * @since 2018-10-30
 */
public enum ErrorEvent {
  CRC_ERROR(1, "校验码错误"),
  OVER_TEMPERATURE(2, "温度过高"),
  OVER_REFLEX(3, "反射过大"),
  OTHER_ERROR(4, "其他错误");

  private int code;
  private String msg;

  ErrorEvent(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public static ErrorEvent toError(int value) {
    for (ErrorEvent event : values()) {
      if (event.code == value) {
        return event;
      }
    }
    return ErrorEvent.OTHER_ERROR;
  }
}
