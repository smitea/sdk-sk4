package com.ridko.sk4.entity;

import com.ridko.sk4.common.HexTools;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class FrequencyTest {

  @Test
  public void testHex() {
//    Frequency frequency = new Frequency(921250);
//    Frequency frequency1 = new Frequency(0x0E, 0x0E, 0xA2);

//    byte[] datas = new byte[]{0x05, 0x0E, 0x0E, (byte) 0xA2, 0x0E, 0x1A, (byte) 0xD7};
//    int len = datas[0] & 0xFF;
//    for (int index = 1; index < len + 1; index += 3) {
//      if(index+2 < datas.length) {
//        int msb = datas[index] & 0xFF;
//        int freq = datas[index + 1] & 0xFF;
//        int lsb = datas[index + 2] & 0xFF;
//
//      }
//    }

    Gen2 gen2 = new Gen2();
    byte[] data = new byte[]{0x01, 0x40, (byte) 0xF0, 0x00};

    gen2.setqValue(Gen2.Q.fromValue(data[0]));
    gen2.setStartQ(((data[1] >> 4) & 0x0F));
    gen2.setMinQ((data[1] & 0x0F));
    gen2.setMaxQ(((data[2] >> 4) & 0x0F));
    gen2.setSelect(Gen2.Select.fromValue(data[3] >> 6));
    gen2.setSession(Gen2.Session.fromValue(data[3] >> 4));
    gen2.setTarget(Gen2.Target.fromValue(data[3] >> 3));

    System.out.println(String.format("Q 设置:%s \t startQ 设置:%d \t MinQ 设置:%d \t MaxQ 设置:%d \t select:%d \t session:%d \t tagret:%d\t",
            gen2.getqValue() == Gen2.Q.DYNAMIC ? "动态Q算法" : "固定Q算法",
            gen2.getStartQ(),
            gen2.getMinQ(),
            gen2.getMaxQ(),
            gen2.getSelect().getValue(),
            gen2.getSession().getValue(),
            gen2.getTarget().getValue()));
//    System.out.println(String.format("MSB:%d FREQ:%d LSB:%d", frequency.getMsb(), frequency.getFreq(), frequency.getLsb()));
  }

  @Test
  public void testAnt() {
    Ants ants = new Ants();
    byte data0 = 0x3F;
    for (int bit = 0; bit < 8; bit++) {
      int _bit = (1 << bit);
      if ((data0 & _bit) == _bit) {
        ants.add(bit + 1, true);
      } else {
        ants.add(bit + 1, false);
      }
    }
    List<Ants.Ant> _ants = ants.getAnts();
    for (Ants.Ant ant : _ants) {
      System.out.println(String.format("天线号:%d 状态:%s \t", ant.getIndex(), ant.isOn() ? "开启" : "关闭"));
    }
  }

  @Test
  public void testTemp() {
    int _value = HexTools.convertByteToInt(new byte[]{(byte) 0xF0, 0x60});
//          int temperature = ((msb << 8) | (lsb & 0xFF)) & 0xFF;
    _value = _value / 100;
    if (_value < 0) {
      _value = ~_value + 1;
    }
    System.out.println(_value);
  }

  @Test
  public void testTag() {
    byte[] data = new byte[]{0x01, 0x00, 0x03, 0x12, 0x34, 0x56, 0x78, 0x01};
    int len = 0x08;
    int ant = data[len - 1];
    byte[] _data = new byte[len - 4];

    int bit = 0;
    for (int _bit = 3; _bit < len - 1; _bit++) {
      _data[bit] = data[_bit];
      bit++;
    }

    System.out.println(String.format("ANT:%d DATA:%s", ant, HexTools.byteArrayToHexString(_data)));
  }

  @Test
  public void testHex1() {
    int waitTime = 10000;
    byte data8 = (byte) ((waitTime & 0xFF00) >> 8);
    byte data9 = (byte) (waitTime & 0x00FF);

    byte[] data = new byte[]{0x02, 0x40, 0x00, (byte) 0x92, 0x04, (byte) 0xDE, 0x1E, (byte) 0xD6, 0x00, 0x00};
    AntWorkAndWaitTime antWorkAndWaitTime = new AntWorkAndWaitTime(
            ((data[0] & 0xFF00) << 8) | ((data[1] & 0xFF)),
            ((data[2] & 0xFF00) << 8) | ((data[3] & 0xFF)),
            ((data[4] & 0xFF00) << 8) | ((data[5] & 0xFF)),
            ((data[6] & 0xFF00) << 8) | ((data[7] & 0xFF)),
            ((data[8] & 0xFF00) << 8) | ((data[9] & 0xFF)));

    System.out.println(String.format("Ant1WorkTime:%d Ant2WorkTime:%d Ant3WorkTime:%d Ant4WorkTime:%d WaitTime:%d",
            antWorkAndWaitTime.getAnt1WorkTime(),
            antWorkAndWaitTime.getAnt2WorkTime(),
            antWorkAndWaitTime.getAnt3WorkTime(),
            antWorkAndWaitTime.getAnt4WorkTime(),
            antWorkAndWaitTime.getWaitTime()));
  }
}