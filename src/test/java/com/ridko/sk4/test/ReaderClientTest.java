package com.ridko.sk4.test;

import com.ridko.sk4.*;
import com.ridko.sk4.common.HexTools;
import com.ridko.sk4.entity.*;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ErrorEvent;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.listenter.ITagListenter;
import com.ridko.sk4.promise.Callback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReaderClientTest {
  private ICommand readerClient;
  private IReaderConnection<SerialParam> futureConnection;
  private IReaderConnection<SocketAddress> tcpConnection;

  @Before
  public void connected() throws Exception {
    SerialParam serialParam = new SerialParam("COM5");
    serialParam.setBaudrate(115200);

    futureConnection = ReaderConnectionBuild.createSerialConnection();

    tcpConnection = ReaderConnectionBuild.createTcpConnection();

    futureConnection.setConnectEventIListenter(new IListenter<ConnectEvent>() {
      public void notify(ConnectEvent event) {
        System.out.println(event.getMsg());
      }
    });
    futureConnection.setErrorEventIListenter(new IListenter<ErrorEvent>() {
      public void notify(ErrorEvent errorEvent) {
        System.out.println(errorEvent.getMsg());
      }
    });
    futureConnection.setChannelValueIListenter(new IListenter<ChannelValue>() {
      public void notify(ChannelValue event) {
        switch (event){
          case IN:
            System.out.println("检测到通道门的操作是进");
            break;
          case OUT:
            System.out.println("检测到通道门的操作是出");
            break;
          case ERROR:
            System.out.println("示通道门进出的状态有误");
            break;
        }
      }
    });
    readerClient = futureConnection.connect(serialParam).await();
  }

  @After
  public void disconnected() throws Exception {
    futureConnection.disconnect().await();
  }

  @Test
  public void start() {
    futureConnection.setTagListenter(new ITagListenter() {
      public void notify(Tag tag) {
        assert tag != null;
        System.out.println(String.format("ANT:%d EPC:%s PC:%d RSSI:%f", tag.getAnt(), tag.getEpc(), tag.getPc(), tag.getRssi()));
      }
    });
    futureConnection.start();
  }

  @Test
  public void stop() throws Exception {
    futureConnection.start();
    futureConnection.stop().await(2, TimeUnit.SECONDS);
    System.out.println("stop...");
  }

  @Test
  public void setTxPower() {
    readerClient.setTxPower(30, 28, true).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println("功率设置成功");
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
        System.out.println("功率设置失败");
      }
    });

    readerClient.getTxPower().then(new Callback<TxPower>() {
      public void onSuccess(TxPower value) {
        System.out.println(String.format("功率获取成功:读功率-%d 写功率-%d ", value.getReadPower(), value.getWritePower(), value.isLoop() ? "开环" : "闭环"));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
        System.out.println("功率获取失败");
      }
    });
  }

  @Test
  public void setGpio() {
    Gpios gpios = new Gpios().add(1, true).add(3, false);
    readerClient.setGpio(gpios).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println("GPIO设置成功");
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });

    // BB1301FF130D0A
    // BB1301FF130D0A
    readerClient.getGpio().then(new Callback<Gpios>() {
      public void onSuccess(Gpios value) {
        System.out.println();
        for (Gpios.Gpio gpio : value.gpios()) {
          System.out.print(String.format("%d : %s |\t", gpio.getIndex(), gpio.isHight()));
        }
        System.out.println();
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });

    // BB130104180D0A
    // BB130104180D0A
    readerClient.getGpio(0x03).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.print(String.format("%d : %s |\t", 0x03, value));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void getInputGpio() throws Exception {
    Gpios result = readerClient.getInputGpio(3).await(2, TimeUnit.SECONDS);
    System.out.println("GPIOs:");
    for (Gpios.Gpio gpio : result.gpios()) {
      System.out.print(String.format("%d : %s |\t", gpio.getIndex(), gpio.isHight()));
    }
    System.out.println();
  }

  @Test
  public void setFrquency() {
    readerClient.setOutputFrequency(921250, 924375).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        if (value) {
          System.out.println("设置射频输出频率成功");
        } else {
          System.out.println("设置射频输出频率失败");
        }
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
    readerClient.getOutputFrequency().then(new Callback<List<Integer>>() {
      public void onSuccess(List<Integer> value) {
        System.out.print("射频频点列表:");
        for (Integer frequency : value) {
          System.out.print(String.format(" %d | ", frequency));
        }
        System.out.println();
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void setGen2() {
    final Gen2 gen2 = new Gen2();
    gen2.setqValue(Gen2.Q.DYNAMIC);
    gen2.setStartQ(4);
    gen2.setMinQ(0);
    gen2.setMaxQ(15);
    readerClient.setGen2(gen2).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println(String.format("设置Gen2%s", value ? "成功" : "失败"));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });

    readerClient.getGen2().then(new Callback<Gen2>() {
      public void onSuccess(Gen2 value) {
        System.out.println(String.format("Q 设置:%s \t startQ 设置:%d \t MinQ 设置:%d \t MaxQ 设置:%d \t select:%d \t session:%d \t tagret:%d\t",
                gen2.getqValue() == Gen2.Q.DYNAMIC ? "动态Q算法" : "固定Q算法",
                gen2.getStartQ(),
                gen2.getMinQ(),
                gen2.getMaxQ(),
                gen2.getSelect().getValue(),
                gen2.getSession().getValue(),
                gen2.getTarget().getValue()));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void setAnt() {
    readerClient.setAnts(new Ants().add(2, true).add(4, true)).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println(String.format("设置Ant%s", value ? "成功" : "失败"));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
    readerClient.getAnts().then(new Callback<Ants>() {
      public void onSuccess(Ants value) {
        List<Ants.Ant> _ants = value.getAnts();
        for (Ants.Ant ant : _ants) {
          System.out.print(String.format("天线号:%d 状态:%s", ant.getIndex(), ant.isOn() ? "开启" : "关闭"));
        }
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void setFrequencyRegion() {
    readerClient.setFrequencyRegion(true, FrequencyRegion.CHINA_2).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println(String.format("设置读写器频率区域%s", value ? "成功" : "失败"));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });

    readerClient.getFrequencyRegion().then(new Callback<FrequencyRegion>() {
      public void onSuccess(FrequencyRegion value) {
        System.out.println(value);
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void getTemperature() {
    readerClient.getTemperature().then(new Callback<Integer>() {
      public void onSuccess(Integer value) {
        System.out.println("当前温度为:" + value);
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
      }
    });
  }

  @Test
  public void getVersion() throws Exception {
    String firmwareVersion = readerClient.getFirmwareVersion().await(3, TimeUnit.SECONDS);
    System.out.println(firmwareVersion);

    String hardwareVersion = readerClient.getHardwareVersion().await(3, TimeUnit.SECONDS);
    System.out.println(hardwareVersion);
  }

  @Test
  public void singleRead() throws Exception {
    Tag tag = readerClient.singleRead().await(3, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d RSSI:%f PC:%d ANT:%d", tag.getAnt(), tag.getRssi(), tag.getPc(), tag.getAnt()));
  }

  @Test
  public void readTagData() throws Exception {
    byte[] md = HexTools.hexStr2Byte("11223344");
    TagData tagData = readerClient.readTagData("11223344", FMB.TID, md, BankNo.EPC, 0x02, 0x03).await(3, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  @Test
  public void writeTagData() throws Exception {
    byte[] data = HexTools.hexStr2Byte("0011223344556677");
    TagData tagData = readerClient.writeTagData("11223344", FMB.EPC, null, BankNo.EPC, 0x02, 0x04, data).await(3, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  @Test
  public void lockTag() throws Exception {
    LockParam param = new LockParam();
    param.setEPC(true);
    param.setUser(true);

    param.setLockType(LockType.LOCK);
    TagData tagData = readerClient.lockTag("11223344", FMB.EPC, HexTools.hexStr2Byte("112233445566"), param).await();
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  @Test
  public void killTag() throws Exception {
    TagData tagData = readerClient.killTag("44332211", FMB.EPC, HexTools.hexStr2Byte("112233445566778899001122")).await();
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  @Test
  public void setCyclicQueryWorkAndResponseTime() throws Exception {
    Boolean result = readerClient.setCyclicQueryWorkAndResponseTime(100, 100).await();
    System.out.println(String.format("设置%s", result ? "成功" : "失败"));
    CyclicQueryWorkAndResponseTime cyclicQueryWorkAndResponseTime = readerClient.getCyclicQueryWorkAndResponseTime().await();
    System.out.println(String.format("循环查询标签工作时间%d | 间断时间设置响应%d",
            cyclicQueryWorkAndResponseTime.getWorkTime(),
            cyclicQueryWorkAndResponseTime.getInterruptedTime()));
  }

  @Test
  public void setAntWorkAndWaitTime() throws Exception {
    Boolean result = readerClient.setAntWorkAndWaitTime(100, 150, 314, 30, 10000).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("设置%s", result ? "成功" : "失败"));
    AntWorkAndWaitTime antWorkAndWaitTime = readerClient.getAntWorkAndWaitTime().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("Ant1WorkTime:%d Ant2WorkTime:%d Ant3WorkTime:%d Ant4WorkTime:%d WaitTime:%d",
            antWorkAndWaitTime.getAnt1WorkTime(),
            antWorkAndWaitTime.getAnt2WorkTime(),
            antWorkAndWaitTime.getAnt3WorkTime(),
            antWorkAndWaitTime.getAnt4WorkTime(),
            antWorkAndWaitTime.getWaitTime()));
  }

  @Test
  public void setFastID() throws Exception {
    Boolean result = readerClient.setFastID(true).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("设置%s", result ? "成功" : "失败"));
    Boolean value = readerClient.getFastID().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("当前 FastID 功能为%s状态", value ? "开启" : "关闭"));
  }

  @Test
  public void setBaudRate() throws Exception {
    Boolean result = readerClient.setBaudRate(BaudRate.B_115200).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("设置%s", result ? "成功" : "失败"));
  }

  @Test
  public void setAutoReadWhenPowerOff() throws Exception {
    Boolean await = readerClient.setAutoReadWhenPowerOff(false).await();
    System.out.println(String.format("开机自动读取标志为: %s", await ? "auto" : "non"));
  }

  @Test
  public void setQtParam() throws Exception {
    Boolean result = readerClient.setQtParam("55555555", FMB.TID, HexTools.hexStr2Byte("112233445566"), true, false).await(2, TimeUnit.SECONDS);
    System.out.println(String.format("设置%s", result ? "成功" : "失败"));
    QtParam qtParam = readerClient.getQtParam("55555555", FMB.TID, HexTools.hexStr2Byte("112233445566")).await();
    System.out.println(String.format("获取的Qt参数为 \t%s |\t %s", qtParam.isCloseControl() ? "启用近距离控制" : "无近距离控制", qtParam.isEnabledPublicMemoryMap() ? "使用 Public Memory Map" : "启用 Private Memory Map"));
  }

  @Test
  public void setQtOperation() throws Exception {
    // 设置 QT 参数，启用近距离控制，标签使用 Private Memory Map。标签TID=0x112233445566778899001122，通过 TID 前 6 个字节过滤，AP=0x55555555
    readerClient.setQtOperation(
            "55555555",
            FMB.TID,
            HexTools.hexStr2Byte("112233445566"),
            QtOperation.NONE,
            true,
            false,
            false,
            // 以下参数值只需要在写操作时添加,在读操作时以下参数值无效
            BankNo.EPC,
            0x00,
            0x00,
            null).await(2, TimeUnit.SECONDS);

    // QT读操作
    QtOperation operation = readerClient.setQtOperation(
            "55555555",
            FMB.TID,
            HexTools.hexStr2Byte("112233445566"),
            QtOperation.NONE,
            true,
            false,
            false,
            // 以下参数值只需要在写操作时添加,在读操作时以下参数值无效
            BankNo.EPC,
            0x00,
            0x00,
            null).await(2, TimeUnit.SECONDS);
    // 获取读操作时返回的数据内容
    switch (operation) {
      case READ:
        byte[] data = operation.getData();
        System.out.println(String.format("设置Qt操作成功后,读取到的数据内容为:%s", HexTools.byteArrayToHexString(data)));
        break;
    }

    // QT写操作
    byte[] data = HexTools.hexStr2Byte("0011223344556677");
    readerClient.setQtOperation("55555555",
            FMB.TID,
            HexTools.hexStr2Byte("112233445566"),
            QtOperation.WRITE,
            true,
            false,
            false,
            BankNo.EPC,
            0x02,
            0x04,
            data).await(2, TimeUnit.SECONDS);
  }

  @Test
  public void setTagFocus() throws Exception {
    Boolean result = readerClient.setTagFocus(true).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println(String.format("设置成功!"));

    Boolean value = readerClient.getTagFocus().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("当前 TAGFOCUS 功能为 %s状态", value ? "开启" : "关闭"));
  }

  @Test
  public void setBeep() throws Exception {
    Boolean result = readerClient.setBeep(true).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void setWorkMode() throws Exception {
    Boolean result = readerClient.setWorkMode(WorkMode.EAS_MODE).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");

    WorkMode value = readerClient.getWorkMode().await(1, TimeUnit.SECONDS);
    System.out.println("获取工作模式成功,工作模式为" + value);
  }

  @Test
  public void setEASParam() throws Exception {
    Boolean result = readerClient.setEASParam(1, 0x02).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
    EAS value = readerClient.getEASParam().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("EAS 区选择的字节为:%d,EAS的值为:%d", value.getBit(), value.getValue()));
  }

  @Test
  public void setHeartbeatParam() throws Exception {
    Boolean result = readerClient.setHeartbeatParam(6).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
    Integer value = readerClient.getHeartbeatParam().await(2, TimeUnit.SECONDS);
    System.out.println(String.format("心跳包参数为:%d * 30s", value));
  }

  @Test
  public void restWifi() throws Exception {
    Boolean result = readerClient.restWifi().await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("重置成功!");
  }

  @Test
  public void setBranchWorkIntervalTime() throws Exception {
    Boolean result = readerClient.setBranchWorkIntervalTime(1).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void setBranchAnts() throws Exception {
    Boolean result = readerClient.setBranchAnts(1, 2).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void setRelayWorkTime() throws Exception {
    Boolean result = readerClient.setRelayWorkTime(1, 2).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void setReaderTriggerWorkTime() throws Exception {
    Boolean result = readerClient.setReaderTriggerWorkTime(2).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void setReaderAlarmIntervalTime() throws Exception {
    Boolean result = readerClient.setReaderAlarmIntervalTime(3).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  @Test
  public void restart() throws Exception {
    Boolean result = readerClient.restart().await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设备已重启!");
  }

  @Test
  public void setBranchWorkPowers() throws Exception {
    Boolean result = readerClient.setBranchWorkPowers(new BranchAntPowerParam(1, 0x10), new BranchAntPowerParam(2, 0x11)).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");

    List<BranchAntPowerParam> params = readerClient.getBranchWorkPowers().await(2, TimeUnit.SECONDS);
    System.out.println();
    for (BranchAntPowerParam powerParam : params) {
      System.out.println(String.format("Ant:%d Power:%d",powerParam.getIndex(),powerParam.getPower()));
    }
    System.out.println();
  }
}