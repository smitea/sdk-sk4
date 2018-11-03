package com.ridko.sk4.entity;

import java.io.Serializable;

/**
 * Gen2参数
 *
 * @author smitea
 * @since 2018-10-31
 */
public class Gen2 implements Serializable {

  /** Q 设置 */
  private Q qValue;
  /** startQ 设置 */
  private int startQ;
  /** MinQ 设置 */
  private int minQ;
  /** MaxQ 设置 */
  private int maxQ;

  private Select select = Select.ALL_B_DEFAULT;
  private Session session = Session.S0;
  private Target target = Target.A_B;

  public Q getqValue() {
    return qValue;
  }

  public void setqValue(Q qValue) {
    this.qValue = qValue;
  }

  public int getStartQ() {
    return startQ;
  }

  public void setStartQ(int startQ) {
    this.startQ = startQ;
  }

  public int getMinQ() {
    return minQ;
  }

  public void setMinQ(int minQ) {
    this.minQ = minQ;
  }

  public int getMaxQ() {
    return maxQ;
  }

  public void setMaxQ(int maxQ) {
    this.maxQ = maxQ;
  }

  public Select getSelect() {
    return select;
  }

  public void setSelect(Select select) {
    this.select = select;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public Target getTarget() {
    return target;
  }

  public void setTarget(Target target) {
    this.target = target;
  }

  /** Q设置 */
  public enum Q {
    /** 固定 Q 算法 */
    FIXED(0),
    /** 动态 Q 算法 */
    DYNAMIC(1);
    private int value;

    Q(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }


    public static Q fromValue(int value) {
      for (Q event : values()) {
        if (event.value == value) {
          return event;
        }
      }
      return Q.FIXED;
    }
  }

  /** query命令的sel参数 */
  public enum Select {
    /** All B’ */
    ALL_B_DEFAULT(0),
    /** All B’ */
    ALL_B(1),
    /** ~SL B’ */
    SL_B_DEFAULT(2),
    /** SL B’ */
    SL_B(3);
    private int value;

    Select(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }


    public static Select fromValue(int value) {
      for (Select event : values()) {
        if (event.value == value) {
          return event;
        }
      }
      return Select.ALL_B_DEFAULT;
    }
  }

  /** query命令的session参数 */
  public enum Session {
    /** S0 B’ */
    S0(0),
    /** S1 B’ */
    S1(1),
    /** S2 B’ */
    S2(2),
    /** S3 B’ */
    S3(3);
    private int value;

    Session(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static Session fromValue(int value) {
      for (Session event : values()) {
        if (event.value == value) {
          return event;
        }
      }
      return Session.S0;
    }
  }

  /** query命令的Target 参数 */
  public enum Target {
    /** A B’ */
    A_B(0),
    /** B B’ */
    B_B(1);
    private int value;

    public int getValue() {
      return value;
    }

    Target(int value) {
      this.value = value;
    }

    public static Target fromValue(int value) {
      for (Target event : values()) {
        if (event.value == value) {
          return event;
        }
      }
      return Target.A_B;
    }
  }
}
