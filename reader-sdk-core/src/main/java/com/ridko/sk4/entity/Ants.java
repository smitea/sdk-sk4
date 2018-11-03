package com.ridko.sk4.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 天线参数列表
 * @author smitea
 * @since 2018-10-31
 */
public class Ants implements Serializable{

  private List<Ant> ants = new ArrayList<Ant>();

  public Ants add(int index,boolean on){
    ants.add(new Ant(index,on));
    return this;
  }

  public Ants add(Ant ... ants){
    this.ants.addAll(Arrays.asList(ants));
    return this;
  }

  public List<Ant> getAnts() {
    return ants;
  }

  /** 天线参数 */
  public class Ant implements Serializable {
    /** 天线索引 */
    private int index;
    /** 是否打开 */
    private boolean on;

    public Ant() {
    }

    public Ant(int index, boolean on) {
      this.index = index;
      this.on = on;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public boolean isOn() {
      return on;
    }

    public void setOn(boolean on) {
      this.on = on;
    }
  }
}


