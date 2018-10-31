package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * 锁定标签参数
 *
 * @author smitea
 * @since 2018-10-31
 */
public class LockParam implements Serializable {
  private boolean isUser;
  private boolean isTID;
  private boolean isEPC;
  private boolean isAccessPwd;
  private boolean isKillPwd;

  private final byte ldlsb = (byte) 0xA0;

  private LockType lockType;

  public boolean isUser() {
    return isUser;
  }

  public void setUser(boolean user) {
    isUser = user;
  }

  public boolean isTID() {
    return isTID;
  }

  public void setTID(boolean TID) {
    isTID = TID;
  }

  public boolean isEPC() {
    return isEPC;
  }

  public void setEPC(boolean EPC) {
    isEPC = EPC;
  }

  public boolean isAccessPwd() {
    return isAccessPwd;
  }

  public void setAccessPwd(boolean accessPwd) {
    isAccessPwd = accessPwd;
  }

  public boolean isKillPwd() {
    return isKillPwd;
  }

  public void setKillPwd(boolean killPwd) {
    isKillPwd = killPwd;
  }

  public int getLdlsb() {
    return ldlsb;
  }

  public LockType getLockType() {
    return lockType;
  }

  public void setLockType(LockType lockType) {
    this.lockType = lockType;
  }

  public byte[] getCommand() {
    boolean[] banks = new boolean[]{isUser, isTID, isEPC, isAccessPwd, isKillPwd};

    byte ldmsb = 0x00;
    byte ld = (byte) getLockType().getValue();

    for (int i = 0; i < banks.length; i++) {
      if (banks[i]) {
        ldmsb += (0x01) << i;
      }
    }
    return new byte[]{ldmsb,ld,ldlsb};
  }
}
