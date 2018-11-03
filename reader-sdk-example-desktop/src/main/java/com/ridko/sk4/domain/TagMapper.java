package com.ridko.sk4.domain;

import javafx.beans.property.*;

/**
 * @author smitea
 * @since 2018-11-03
 */
public class TagMapper {
  private IntegerProperty num = new SimpleIntegerProperty();
  private IntegerProperty count = new SimpleIntegerProperty();
  private StringProperty time = new SimpleStringProperty();
  private IntegerProperty ant = new SimpleIntegerProperty();
  private StringProperty epc = new SimpleStringProperty();
  private IntegerProperty pc = new SimpleIntegerProperty();
  private DoubleProperty rssi = new SimpleDoubleProperty();

  public TagMapper() {
  }

  public TagMapper(IntegerProperty ant, StringProperty epc, IntegerProperty pc, DoubleProperty rssi) {
    this.ant = ant;
    this.epc = epc;
    this.pc = pc;
    this.rssi = rssi;
  }

  public TagMapper(IntegerProperty num, IntegerProperty count, StringProperty time, IntegerProperty ant, StringProperty epc, IntegerProperty pc, DoubleProperty rssi) {
    this.num = num;
    this.count = count;
    this.time = time;
    this.ant = ant;
    this.epc = epc;
    this.pc = pc;
    this.rssi = rssi;
  }

  public int getNum() {
    return num.get();
  }

  public IntegerProperty numProperty() {
    return num;
  }

  public void setNum(int num) {
    this.num.set(num);
  }

  public int getCount() {
    return count.get();
  }

  public IntegerProperty countProperty() {
    return count;
  }

  public void setCount(int count) {
    this.count.set(count);
  }

  public String getTime() {
    return time.get();
  }

  public StringProperty timeProperty() {
    return time;
  }

  public void setTime(String time) {
    this.time.set(time);
  }

  public int getAnt() {
    return ant.get();
  }

  public IntegerProperty antProperty() {
    return ant;
  }

  public void setAnt(int ant) {
    this.ant.set(ant);
  }

  public String getEpc() {
    return epc.get();
  }

  public StringProperty epcProperty() {
    return epc;
  }

  public void setEpc(String epc) {
    this.epc.set(epc);
  }

  public int getPc() {
    return pc.get();
  }

  public IntegerProperty pcProperty() {
    return pc;
  }

  public void setPc(int pc) {
    this.pc.set(pc);
  }

  public double getRssi() {
    return rssi.get();
  }

  public DoubleProperty rssiProperty() {
    return rssi;
  }

  public void setRssi(double rssi) {
    this.rssi.set(rssi);
  }
}
