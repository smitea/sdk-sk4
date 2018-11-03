package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * Qt参数
 * @author smitea
 * @since 2018-11-01
 */
public class QtParam implements Serializable {
  /** 是否启用近距离控制 */
  private boolean isCloseControl;
  /** 是否使用 Public Memory Map */
  private boolean isEnabledPublicMemoryMap;

  public QtParam() {
  }

  public QtParam(boolean isCloseControl, boolean isEnabledPublicMemoryMap) {
    this.isCloseControl = isCloseControl;
    this.isEnabledPublicMemoryMap = isEnabledPublicMemoryMap;
  }

  public boolean isCloseControl() {
    return isCloseControl;
  }

  public void setCloseControl(boolean closeControl) {
    isCloseControl = closeControl;
  }

  public boolean isEnabledPublicMemoryMap() {
    return isEnabledPublicMemoryMap;
  }

  public void setEnabledPublicMemoryMap(boolean enabledPublicMemoryMap) {
    isEnabledPublicMemoryMap = enabledPublicMemoryMap;
  }
}
