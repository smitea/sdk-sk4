package com.ridko.sk4;

import com.ridko.sk4.common.HexTools;
import com.ridko.sk4.entity.*;
import com.ridko.sk4.error.GetException;
import com.ridko.sk4.error.SetException;
import com.ridko.sk4.promise.Promise;
import com.ridko.sk4.protocol.Protocol;
import com.ridko.sk4.protocol.SK4Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

/**
 * SK4客户端
 *
 * @author smitea
 * @since 2018-10-30
 */
public class SK4Client implements ICommand {
  private AbstractConnection abstractConnection = null;

  public SerialConnection createSerialConnection() {
    SerialConnection serialConnection = new SerialConnection();
    abstractConnection = serialConnection;
    return serialConnection;
  }

  public TcpConnection createTcpConnection() {
    TcpConnection serialConnection = new TcpConnection();
    abstractConnection = serialConnection;
    return serialConnection;
  }

  public Promise<Boolean> setTxPower(final int readPower, final int writePower, final boolean isLoop) {
    final Promise<Boolean> promise = new Promise<Boolean>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        boolean result = data[0] == 0x01;
        promise.onSuccess(result);
      }

      public SK4Protocol writeProtocol() {
        SK4Protocol sk4Protocol = new SK4Protocol();
        byte data0 = (byte) (isLoop ? 0 : 1);
        byte data1 = (byte) (readPower & 0xFF);
        byte data2 = (byte) (writePower & 0xFF);
        byte[] data = new byte[]{data0, data1, data2};
        sk4Protocol.setData(data);
        return sk4Protocol;
      }

      public int resultType() {
        return 0x80;
      }
    });

    return promise;
  }

  public Promise<TxPower> getTxPower() {
    final Promise<TxPower> txPowerPromise = new Promise<TxPower>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        TxPower power = new TxPower(data[0] == 0x00, data[1] & 0xFF, data[2] & 0xFF);
        txPowerPromise.onSuccess(power);
      }

      public SK4Protocol writeProtocol() {
        SK4Protocol sk4Protocol = new SK4Protocol();
        sk4Protocol.setType(0x0C);
        sk4Protocol.setLen(0x00);
        return sk4Protocol;
      }

      public int resultType() {
        return 0x8C;
      }
    });
    return txPowerPromise;
  }

  public Promise<Boolean> setGpio(final Gpios gpios) {
    final Promise<Boolean> promise = new Promise<Boolean>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        boolean result = data[0] == 0x01;
        promise.onSuccess(result);
      }

      public SK4Protocol writeProtocol() {
        byte data0 = 0;
        byte data1 = 0;
        List<Gpios.Gpio> _gpios = gpios.gpios();
        for (Gpios.Gpio gpio : _gpios) {
          int index = 1 << gpio.getIndex() - 1;
          data0 |= index;
          if (gpio.isHight()) {
            data1 |= index;
          } else {
            data1 = (byte) (data1 & ~index);
          }
        }
        return new SK4Protocol(0x01, 0x02, new byte[]{data0, data1});
      }

      public int resultType() {
        return 0x81;
      }
    });
    return promise;
  }

  public Promise<Gpios> getGpio() {
    final Promise<Gpios> gpiosPromise = new Promise<Gpios>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        Gpios gpios = new Gpios();

        byte[] data = protocol.getData();
        if (data[0] == 0x01) {
          byte _value = data[2];
          for (int _index = 0; _index < 8; _index++) {
            int _bit = 1 << _index;
            gpios.add(_index, ((_value & _bit) == _bit));
          }
        }
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x13, 0x01, new byte[]{(byte) 0xFF});
      }

      public int resultType() {
        return 0x93;
      }
    });
    return gpiosPromise;
  }

  public Promise<Boolean> getGpio(final int index) {
    final Promise<Boolean> gpioPromise = new Promise<Boolean>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        if (data[0] == 0x01) {
          byte _index = data[1];
          byte _value = data[2];

          byte _bit = (byte) (1 << index - 1);
          if ((_index & _bit) == _bit) {
            gpioPromise.onSuccess(_value == _bit);
          }
        } else {
          gpioPromise.onFailure(new RuntimeException(String.format("get %d gpio is failed", index)));
        }
      }

      public SK4Protocol writeProtocol() {
        byte _index = (byte) (1 << index - 1);
        return new SK4Protocol(0x13, 0x01, new byte[]{_index});
      }

      public int resultType() {
        return 0x93;
      }
    });
    return gpioPromise;
  }

  public Promise<Boolean> setOutputFrequency(final int... freqs) {
    final Promise<Boolean> promise = new Promise<Boolean>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        boolean result = data[0] == 0x01;
        promise.onSuccess(result);
      }

      public SK4Protocol writeProtocol() {
        int len = 1;
        byte[] datas = new byte[freqs.length * 3 + 1];
        datas[0] = (byte) freqs.length;
        for (int index = 0; index < freqs.length; index++) {
          Frequency frequency = new Frequency(freqs[index]);
          int msb = frequency.getMsb();
          int freq = frequency.getFreq();
          int lsb = frequency.getLsb();
          datas[len] = (byte) (msb & 0xFF);
          datas[len + 1] = (byte) (freq & 0xFF);
          datas[len + 2] = (byte) (lsb & 0xFF);
          len += 3;
        }
        return new SK4Protocol(0x02, (byte) (datas.length), datas);
      }

      public int resultType() {
        return 0x82;
      }
    });
    return promise;
  }

  public Promise<List<Integer>> getOutputFrequency() {
    final Promise<List<Integer>> promise = new Promise<List<Integer>>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] datas = protocol.getData();
        List<Integer> frequencies = new ArrayList<Integer>();
        int len = datas[0] & 0xFF;
        for (int index = 1; index < len + 1; index += 3) {
          if (index + 2 < datas.length) {
            int msb = datas[index] & 0xFF;
            int freq = datas[index + 1] & 0xFF;
            int lsb = datas[index + 2] & 0xFF;
            frequencies.add(new Frequency(msb, freq, lsb).getValue());
          }
        }
        promise.onSuccess(frequencies);
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x0D, 0x00, null);
      }

      public int resultType() {
        return 0x8D;
      }
    });
    return promise;
  }

  public Promise<Boolean> setGen2(final Gen2 gen2) {
    final Promise<Boolean> promise = new Promise<Boolean>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(data[0] == 0x01);
      }

      public SK4Protocol writeProtocol() {
        byte data3 = (byte) gen2.getqValue().getValue();
        byte data2 = (byte) (((gen2.getStartQ() << 4) & 0xF0) | (gen2.getMinQ() & 0x0F));
        byte data1 = (byte) ((gen2.getMaxQ() << 4) & 0xF0);
        byte data0 = (byte) ((gen2.getSelect().getValue() << 6) | (gen2.getSession().getValue() << 4) | (gen2.getTarget().getValue() << 3));
        return new SK4Protocol(0x07, 0x04, new byte[]{data3, data2, data1, data0});
      }

      public int resultType() {
        return 0x87;
      }
    });

    return promise;
  }

  public Promise<Gen2> getGen2() {
    final Promise<Gen2> promise = new Promise<Gen2>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        Gen2 gen2 = new Gen2();
        byte[] data = protocol.getData();

        gen2.setqValue(Gen2.Q.fromValue(data[0]));
        gen2.setStartQ(((data[1] >> 4) & 0x0F));
        gen2.setMinQ((data[1] & 0x0F));
        gen2.setMaxQ(((data[2] >> 4) & 0x0F));
        gen2.setSelect(Gen2.Select.fromValue(data[3] >> 6));
        gen2.setSession(Gen2.Session.fromValue(data[3] >> 4));
        gen2.setTarget(Gen2.Target.fromValue(data[3] >> 3));

        promise.onSuccess(gen2);
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x14, 0x00, null);
      }

      public int resultType() {
        return 0x94;
      }
    });
    return promise;
  }

  public Promise<Boolean> setAnts(final Ants ants) {
    final Promise<Boolean> promise = new Promise<Boolean>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(data[0] == 0x01);
      }

      public SK4Protocol writeProtocol() {
        List<Ants.Ant> _ants = ants.getAnts();
        byte data0 = 0;
        for (Ants.Ant ant : _ants) {
          if (ant.isOn()) {
            data0 |= (1 << ant.getIndex() - 1);
          }
        }
        return new SK4Protocol(0x08, 0x01, new byte[]{data0});
      }

      public int resultType() {
        return 0x88;
      }
    });

    return promise;
  }

  public Promise<Ants> getAnts() {
    final Promise<Ants> antsPromise = new Promise<Ants>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        Ants ants = new Ants();
        byte data0 = data[0];
        for (int bit = 0; bit < 8; bit++) {
          int _bit = (1 << bit);
          if ((data0 & _bit) == _bit) {
            ants.add(bit + 1, true);
          } else {
            ants.add(bit + 1, false);
          }
        }
        antsPromise.onSuccess(ants);
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x10, 0x00, null);
      }

      public int resultType() {
        return 0x90;
      }
    });

    return antsPromise;
  }

  public Promise<Boolean> setFrequencyRegion(final boolean isSave, final FrequencyRegion region) {
    final Promise<Boolean> promise = new Promise<Boolean>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(data[0] == 0x01);
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x09, 0x02, new byte[]{(byte) (isSave ? 1 : 0), (byte) region.getValue()});
      }

      public int resultType() {
        return 0x89;
      }
    });

    return promise;
  }

  public Promise<FrequencyRegion> getFrequencyRegion() {
    final Promise<FrequencyRegion> frequencyRegionPromise = new Promise<FrequencyRegion>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        byte data0 = data[0];
        byte data1 = data[1];

        if (data0 == 0x01) {
          frequencyRegionPromise.onSuccess(FrequencyRegion.fromValue(data1));
        } else {
          frequencyRegionPromise.onFailure(new GetException("the Frequency Region get failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x11, 0x00, null);
      }

      public int resultType() {
        return 0x91;
      }
    });

    return frequencyRegionPromise;
  }

  public Promise<Integer> getTemperature() {
    final Promise<Integer> promise = new Promise<Integer>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        byte data0 = data[0];
        if (data0 == 0x01) {
          byte msb = data[1];
          byte lsb = data[2];

          int _value = HexTools.convertByteToInt(new byte[]{msb, lsb});
          _value = _value / 100;
          if (_value < 0) {
            _value = ~_value + 1;
          }
          promise.onSuccess(_value);
        } else {
          promise.onFailure(new GetException("the Temperature get failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x12, 0x00, null);
      }

      public int resultType() {
        return 0x92;
      }
    });

    return promise;
  }

  public Promise<String> getHardwareVersion() {
    final Promise<String> promise = new Promise<String>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(String.format("V%d.%d.%d", data[0], data[1], data[2]));
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x0A, 0x00, null);
      }

      public int resultType() {
        return 0x8A;
      }
    });
    return promise;
  }

  public Promise<String> getFirmwareVersion() {
    final Promise<String> promise = new Promise<String>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(String.format("V%d.%d.%d", data[0], data[1], data[2]));
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x0B, 0x00, null);
      }

      public int resultType() {
        return 0x8B;
      }
    });
    return promise;
  }

  public Promise<Tag> singleRead() {
    final Promise<Tag> tagPromise = new Promise<Tag>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        // 天线号
        int ant = data[data.length - 1];
        if (ant > 8 || ant < 1) {
          return;
        }
        // PC
        int pc = HexTools.convertByteToInt(new byte[]{data[1], data[0]});
        int epcLen = data.length - 5;
        byte[] _epc = new byte[epcLen];
        System.arraycopy(data, 2, _epc, 0, epcLen);
        // EPC码
        String epc = HexTools.byteArrayToHexString(_epc);

        // 场强值
        double rssi = ((data[data.length - 3] << 8) + (int) data[data.length - 2]) / 10.00;

        Tag tag = new Tag(pc, ant, rssi, epc);
        tagPromise.onSuccess(tag);
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x16, 0x00, null);
      }

      public int resultType() {
        return 0x96;
      }
    });
    return tagPromise;
  }

  public Promise<TagData> readTagData(final String password, final FMB fmb, final byte[] md, final BankNo mb, final int sa, final int dl) {
    final Promise<TagData> tagDataPromise = new Promise<TagData>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        if (data[0] == 0x01) {
          int len = 0x08;
          int ant = data[len - 1];
          byte[] _data = new byte[len - 4];

          int bit = 0;
          for (int _bit = 3; _bit < len - 1; _bit++) {
            _data[bit] = data[_bit];
            bit++;
          }

          tagDataPromise.onSuccess(new TagData(_data, ant));
        } else {
          tagDataPromise.onFailure(new GetException("the read tag data is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        ByteBuf byteBuf = Unpooled.buffer();

        byte[] _password = new byte[]{0x00, 0x00, 0x00, 0x00};
        if (!"".equals(password)) {
          _password = HexTools.hexStr2Byte(password);
        }

        // 过滤数据类型
        int filterType = fmb.getValue();

        // 过滤数据
        int filterLength = 0;
        byte[] filterData = new byte[0];

        // 过滤数据
        if (md != null && md.length != 0) {
          filterData = md;
          filterLength = md.length;
        }

        // 写入标签密码
        byteBuf.writeBytes(_password);
        // 写入过滤数据类型
        byteBuf.writeByte(filterType);
        // 写入过滤数据长度
        byteBuf.writeByte(filterLength & 0xFF00);
        byteBuf.writeByte(filterLength & 0xFF);
        // 写入过滤数据
        byteBuf.writeBytes(filterData);
        // 写入数据的bank号
        byteBuf.writeByte(mb.getValue());
        // 写入数据的起始地址
        byteBuf.writeByte(sa & 0xFF00);
        byteBuf.writeByte(sa & 0xFF);
        // 写入需查询的数据长度
        byteBuf.writeByte(dl & 0xFF00);
        byteBuf.writeByte(dl & 0xFF);

        byte[] data = ByteBufUtil.getBytes(byteBuf);
        return new SK4Protocol(0x19, data.length, data);
      }

      public int resultType() {
        return 0x99;
      }
    });
    return tagDataPromise;
  }

  public Promise<TagData> writeTagData(final String password, final FMB fmb, final byte[] md, final BankNo mb, final int sa, final int dl, final byte[] data) {
    final Promise<TagData> promise = new Promise<TagData>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] _data = protocol.getData();
        if (_data[0] == 0x01) {
          promise.onSuccess(new TagData(data, _data[1]));
        } else {
          promise.onFailure(new SetException("the write tag data is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        ByteBuf byteBuf = Unpooled.buffer();

        byte[] _password = new byte[]{0x00, 0x00, 0x00, 0x00};
        if (!"".equals(password)) {
          _password = HexTools.hexStr2Byte(password);
        }

        // 过滤数据类型
        int filterType = fmb.getValue();

        // 过滤数据
        int filterLength = 0;
        byte[] filterData = new byte[0];

        // 过滤数据
        if (md != null && md.length != 0) {
          filterData = md;
          filterLength = md.length;
        }

        // 写入标签密码
        byteBuf.writeBytes(_password);
        // 写入过滤数据类型
        byteBuf.writeByte(filterType);
        // 写入过滤数据长度
        byteBuf.writeByte(filterLength & 0xFF00);
        byteBuf.writeByte(filterLength & 0xFF);
        // 写入过滤数据
        byteBuf.writeBytes(filterData);
        // 写入数据的bank号
        byteBuf.writeByte(mb.getValue());
        // 写入数据的起始地址
        byteBuf.writeByte(sa & 0xFF00);
        byteBuf.writeByte(sa & 0xFF);
        // 写入需查询的数据长度
        byteBuf.writeByte(dl & 0xFF00);
        byteBuf.writeByte(dl & 0xFF);

        // 写入的数据
        byteBuf.writeBytes(data);

        byte[] data = ByteBufUtil.getBytes(byteBuf);
        return new SK4Protocol(0x1A, data.length, data);
      }

      public int resultType() {
        return 0x9A;
      }
    });

    return promise;
  }

  public Promise<TagData> lockTag(final String password, final FMB fmb, final byte[] md, final LockParam param) {
    final Promise<TagData> promise = new Promise<TagData>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] _data = protocol.getData();
        if (_data[0] == 0x01) {
          promise.onSuccess(new TagData(md, _data[1]));
        } else {
          promise.onFailure(new SetException("the lock tag data is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        ByteBuf byteBuf = Unpooled.buffer();

        byte[] _password = new byte[]{0x00, 0x00, 0x00, 0x00};
        if (!"".equals(password)) {
          _password = HexTools.hexStr2Byte(password);
        }

        // 过滤数据类型
        int filterType = fmb.getValue();

        // 过滤数据
        int filterLength = 0;
        byte[] filterData = new byte[0];

        // 过滤数据
        if (md != null && md.length != 0) {
          filterData = md;
          filterLength = md.length;
        }

        // 写入标签密码
        byteBuf.writeBytes(_password);
        // 写入过滤数据类型
        byteBuf.writeByte(filterType);
        // 写入过滤数据长度
        byteBuf.writeByte(filterLength & 0xFF00);
        byteBuf.writeByte(filterLength & 0xFF);
        // 写入过滤数据
        byteBuf.writeBytes(filterData);
        // 共 3 个字节 24bit，其中，高4bit 无效，第 0~9bit(共 10bit)为 Action 位，第 10~19bit(共 10bit)为 mask 位
        byteBuf.writeBytes(param.getCommand());

        byte[] data = ByteBufUtil.getBytes(byteBuf);
        return new SK4Protocol(0x1B, data.length, data);
      }

      public int resultType() {
        return 0x9B;
      }
    });

    return promise;
  }

  public Promise<TagData> killTag(final String password, final FMB fmb, final byte[] md) {
    final Promise<TagData> tagDataPromise = new Promise<TagData>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] _data = protocol.getData();
        if (_data[0] == 0x01) {
          tagDataPromise.onSuccess(new TagData(md, _data[1]));
        } else {
          tagDataPromise.onFailure(new SetException("the kill tag data is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        ByteBuf byteBuf = Unpooled.buffer();
        byte[] _password = new byte[]{0x00, 0x00, 0x00, 0x00};
        if (!"".equals(password)) {
          _password = HexTools.hexStr2Byte(password);
        }

        // 过滤数据类型
        int filterType = fmb.getValue();

        // 过滤数据
        int filterLength = 0;
        byte[] filterData = new byte[0];

        // 过滤数据
        if (md != null && md.length != 0) {
          filterData = md;
          filterLength = md.length;
        }

        // 写入标签密码
        byteBuf.writeBytes(_password);
        // 写入过滤数据类型
        byteBuf.writeByte(filterType);
        // 写入过滤数据长度
        byteBuf.writeByte(filterLength & 0xFF00);
        byteBuf.writeByte(filterLength & 0xFF);
        // 写入过滤数据
        byteBuf.writeBytes(filterData);

        byte[] data = ByteBufUtil.getBytes(byteBuf);
        return new SK4Protocol(0x1C, data.length, data);
      }

      public int resultType() {
        return 0x9C;
      }
    });
    return tagDataPromise;
  }

  public Promise<Boolean> setCyclicQueryWorkAndResponseTime(final int workTime, final int interruptedTime) {
    final Promise<Boolean> promise = new Promise<Boolean>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(data[0] == 0x01);
      }

      public SK4Protocol writeProtocol() {
        byte data0 = (byte) (workTime & 0xFF00);
        byte data1 = (byte) (workTime & 0xFF);
        byte data2 = (byte) (interruptedTime & 0xFF00);
        byte data3 = (byte) (interruptedTime & 0xFF);
        return new SK4Protocol(0x1D, 0x04, new byte[]{data0, data1, data2, data3});
      }

      public int resultType() {
        return 0x9D;
      }
    });
    return promise;
  }

  public Promise<CyclicQueryWorkAndResponseTime> getCyclicQueryWorkAndResponseTime() {
    final Promise<CyclicQueryWorkAndResponseTime> promise = new Promise<CyclicQueryWorkAndResponseTime>();
    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        if (data[0] == 0x01) {
          byte data0 = data[1];
          byte data1 = data[2];
          byte data2 = data[3];
          byte data3 = data[4];

          int workTime = ((data0 & 0xFFFF) << 8) | (data1 & 0xFFFF);
          int interruptedTime = ((data2 & 0xFFFF) << 8) | (data3 & 0xFFFF);
          promise.onSuccess(new CyclicQueryWorkAndResponseTime(workTime, interruptedTime));
        } else {
          promise.onFailure(new GetException("the getCyclicQueryWorkAndResponseTime is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x1E, 0x00, null);
      }

      public int resultType() {
        return 0x9E;
      }
    });
    return promise;
  }

  public Promise<Boolean> setAntWorkAndWaitTime(final int ant1WorkTime, final int ant2WorkTime, final int ant3WorkTime, final int ant4WorkTime, final int waitTime) {
    final Promise<Boolean> promise = new Promise<Boolean>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        promise.onSuccess(data[0] == 0x01);
      }

      public SK4Protocol writeProtocol() {
        byte data0 = (byte) ((ant1WorkTime & 0xFF00) >> 8);
        byte data1 = (byte) (ant1WorkTime & 0x00FF);

        byte data2 = (byte) ((ant2WorkTime & 0xFF00) >> 8);
        byte data3 = (byte) (ant2WorkTime & 0x00FF);

        byte data4 = (byte) ((ant3WorkTime & 0xFF00) >> 8);
        byte data5 = (byte) (ant3WorkTime & 0x00FF);

        byte data6 = (byte) ((ant4WorkTime & 0xFF00) >> 8);
        byte data7 = (byte) (ant4WorkTime & 0x00FF);

        byte data8 = (byte) ((waitTime & 0xFF00) >> 8);
        byte data9 = (byte) (waitTime & 0x00FF);

        return new SK4Protocol(0x1F, 0x0A, new byte[]{data0, data1, data2, data3, data4, data5, data6, data7, data8, data9});
      }

      public int resultType() {
        return 0x9F;
      }
    });

    return promise;
  }

  public Promise<AntWorkAndWaitTime> getAntWorkAndWaitTime() {
    final Promise<AntWorkAndWaitTime> antWorkAndWaitTimePromise = new Promise<AntWorkAndWaitTime>();

    abstractConnection.send(new Protocol() {
      public void readProtocol(SK4Protocol protocol) {
        byte[] data = protocol.getData();
        if(data[0] == 0x01) {
          antWorkAndWaitTimePromise.onSuccess(new AntWorkAndWaitTime(
                  ((data[1] & 0xFF00) << 8) | ((data[2] & 0x00FF)),
                  ((data[3] & 0xFF00) << 8) | ((data[4] & 0x00FF)),
                  ((data[5] & 0xFF00) << 8) | ((data[6] & 0x00FF)),
                  ((data[7] & 0xFF00) << 8) | ((data[8] & 0x00FF)),
                  ((data[9] & 0xFF00) << 8) | ((data[10] & 0x00FF))
          ));
        }else{
          antWorkAndWaitTimePromise.onFailure(new GetException("the get AntWorkAndWaitTime is failed"));
        }
      }

      public SK4Protocol writeProtocol() {
        return new SK4Protocol(0x20,0x00,null);
      }

      public int resultType() {
        return 0xA0;
      }
    });

    return antWorkAndWaitTimePromise;
  }
}
