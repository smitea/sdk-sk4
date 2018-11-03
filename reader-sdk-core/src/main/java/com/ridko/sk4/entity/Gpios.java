package com.ridko.sk4.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GPIO参数
 * @author smitea
 * @since 2018-10-31
 */
public class Gpios implements Serializable {

  private List<Gpio> gpios = new ArrayList<Gpio>();

  public Gpios add(int index,boolean hight){
    gpios.add(new Gpio(index,hight));
    return this;
  }

  public Gpios add(Gpio... gpios){
    this.gpios.addAll(Arrays.asList(gpios));
    return this;
  }

  public List<Gpio> gpios() {
    return gpios;
  }

  public class Gpio{
    /** GPIO索引 */
    private int index;
    /** 电平值 */
    private boolean hight;

    public Gpio(int index, boolean hight) {
      this.index = index;
      this.hight = hight;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public boolean isHight() {
      return hight;
    }

    public void setHight(boolean hight) {
      this.hight = hight;
    }
  }
}
