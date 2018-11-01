package com.ridko.sk4.entity;

/**
 * Qt操作参数
 *
 * @author smitea
 * @since 2018-11-01
 */
public enum QtOperation {
  /** QT 命令执行后，无操作 */
  NONE(0x00),
  /** QT 命令后马上执行 Read 操作 */
  READ(0x01),
  /** QT 命令后马上执行 Write 操作 */
  WRITE(0x02);
  private int value;
  private byte[] data;

  QtOperation(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public static QtOperation fromValue(int value) {
    for (QtOperation event : values()) {
      if (event.value == value) {
        return event;
      }
    }
    return QtOperation.NONE;
  }
}
