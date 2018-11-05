package com.ridko.test;

import com.ridko.sk4.ICommand;
import com.ridko.sk4.IReaderConnection;
import com.ridko.sk4.ReaderConnectionBuild;
import com.ridko.sk4.SerialParam;
import com.ridko.sk4.common.HexTools;
import com.ridko.sk4.entity.*;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ErrorEvent;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.listenter.ITagListenter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderClientTest {
  private ICommand readerClient;
  private IReaderConnection futureConnection;

  private static ScheduledExecutorService scheduledExecutorService;

  /** 测试初始化操作 */
  @Before
  public void created() throws Exception {
    // 设置 指令 调试打印输出
    System.setProperty("sk4.debug", "true");

    // 创建一个单线程的线程池用于计算读写器循环查询标签速率
    scheduledExecutorService = Executors.newScheduledThreadPool(1);

    // 设置TCP连接
    connectTcp();

    // 设置串口连接
//    connectSerial();
  }

  /** 连接串口(连接串口之前需要配置 Rxtx 驱动环境) */
  private void connectSerial() throws Exception {
    // 设置串口参数
    SerialParam serialParam = new SerialParam("COM5");
    // 设置波特率
    serialParam.setBaudrate(115200);
    // 创建串口连接对象
    IReaderConnection<SerialParam> serialConnection = ReaderConnectionBuild.createSerialConnection();
    // 设置事件监听器(事件监听器请在连接前设置，否则某些事件会监听不到)
    addListeners(serialConnection);
    // 保存连接对象
    futureConnection = serialConnection;

    // 建立串口通信连接(连接超时3秒后抛出异常),并返回读写器操作接口
    readerClient = serialConnection.connect(serialParam).await(3, TimeUnit.SECONDS);
  }

  /** 连接TCP */
  private void connectTcp() throws Exception {
    // 创建TCP连接对象
    IReaderConnection<SocketAddress> tcpConnection = ReaderConnectionBuild.createTcpConnection();
    // 设置事件监听器(事件监听器请在连接前设置，否则某些事件会监听不到)
    addListeners(tcpConnection);
    // 建立TCP通信连接(连接超时3秒后抛出异常),并返回读写器操作接口
    readerClient = tcpConnection.connect(new InetSocketAddress("192.168.1.252", 8000)).await(3, TimeUnit.SECONDS);
    // 保存连接对象
    futureConnection = tcpConnection;
  }

  /** 设置事件监听器 */
  private void addListeners(IReaderConnection futureConnection) {
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
  }

  /** 测试结束时操作 */
  @After
  public void disconnected() throws Exception {
    // 关闭连接对象
    futureConnection.disconnect();
    // 关闭线程池
    scheduledExecutorService.shutdown();
  }

  /** 开始循环查询标签 */
  @Test
  public void start() throws InterruptedException {
    // 设置循环查询标签回调
    futureConnection.setTagListenter(new ITagListenter() {
      public void notify(Tag tag) {
        assert tag != null;
        // 打印标签信息
        System.out.println(String.format("ANT:%d EPC:%s PC:%d RSSI:%f", tag.getAnt(), tag.getEpc(), tag.getPc(), tag.getRssi()));
      }
    });
    // 开始循环查询标签
    futureConnection.start();

    // 测试10秒周期
    Thread.sleep(10000);
  }

  /** 测试循环查询标签速率 */
  @Test
  public void readTagRateTest() throws InterruptedException {
    // 标签数量计数器
    final AtomicInteger atomicInteger = new AtomicInteger(0);

    // 每秒计算一次速度
    scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        System.out.println(String.format("rate: %d /s ", atomicInteger.get()));
        // 重置
        atomicInteger.getAndSet(0);
      }
    }, 1, 1, TimeUnit.SECONDS);

    // 设置循环查询标签回调
    futureConnection.setTagListenter(new ITagListenter() {
      public void notify(Tag tag) {
        assert tag != null;
        // 计数器+1
        atomicInteger.addAndGet(1);
      }
    });
    // 开始循环查询标签
    futureConnection.start();

    // 测试10秒周期
    Thread.sleep(10000);
  }

  /** 停止循环查询标签 */
  @Test
  public void stop() throws Exception {
    // 开始循环查询标签
    futureConnection.start();
    // 等待两秒停止循环查询标签
    futureConnection.stop().await(2, TimeUnit.SECONDS);
    System.out.println("stop...");
  }

  /** 读写器功率设置/获取 */
  @Test
  public void setTxPower() throws Exception {
    // 设置 读功率30 / 写功率28 / 开环状态
    readerClient.setTxPower(30, 30, true).await(1, TimeUnit.SECONDS);
    System.out.println("功率设置成功");

    // 获取读写器功率
    TxPower value = readerClient.getTxPower().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("功率获取成功:读功率-%d 写功率-%d ", value.getReadPower(), value.getWritePower(), value.isLoop() ? "开环" : "闭环"));
  }

  /** 读写器 GPIO 输出设置/获取 */
  @Test
  public void setGpio() throws Exception {
    // 设置 GPIO1 | 打开 ,GPIO3 | 关闭
    Gpios gpios = new Gpios().add(1, true).add(3, false);
    readerClient.setGpio(gpios).await(1, TimeUnit.SECONDS);
    System.out.println("GPIO设置成功");

    // 获取 GPIO状态
    Gpios _gpios = readerClient.getGpio().await(1, TimeUnit.SECONDS);
    for (Gpios.Gpio gpio : _gpios.gpios()) {
      System.out.println(String.format("GPIO%d : %s |\t", gpio.getIndex(), gpio.isHight() ? "高电平" : "低电平"));
    }

    // 获取指定 GPIO 状态
    Boolean value = readerClient.getGpio(3).await(1, TimeUnit.SECONDS);
    System.out.print(String.format("GPIO%d : %s |\t", 0x03, value ? "高电平" : "低电平"));
  }

  /** 读写器 GPIO 输入设置/获取 */
  @Test
  public void getInputGpio() throws Exception {
    // 获取 GPIO3 的输入状态(等待1s)
    Gpios result = readerClient.getInputGpio(1, 2, 3, 4, 5, 6, 7, 8).await(1, TimeUnit.SECONDS);
    System.out.println("GPIOs:");
    for (Gpios.Gpio gpio : result.gpios()) {
      System.out.println(String.format("GPIO%d : %s |\t", gpio.getIndex(), gpio.isHight() ? "高电平" : "低电平"));
    }
    System.out.println();
  }

  /** 读写器 射频频率状态 设置/获取 */
  @Test
  public void setFrquency() throws Exception {
    // 设置读写器2个跳频频点， 921250(0E0EA2)\924375(0E1AD7)
    readerClient.setOutputFrequency(921250, 924375).await(1, TimeUnit.SECONDS);
    System.out.println("设置射频输出频率成功");
    // 查询射频跳频频段
    List<Integer> value = readerClient.getOutputFrequency().await(1, TimeUnit.SECONDS);
    System.out.println("射频频点列表:");
    for (Integer frequency : value) {
      System.out.println(String.format(" %d | ", frequency));
    }
    System.out.println();
  }

  /** 读写器 Q算法 设置/获取 */
  @Test
  public void setGen2() throws Exception {
    // 设置 gen2 参数
    final Gen2 gen2 = new Gen2();
    // 动态Q算法
    gen2.setqValue(Gen2.Q.DYNAMIC);
    // StartQ设置
    gen2.setStartQ(4);
    // MinQ设置
    gen2.setMinQ(0);
    // MaxQ设置
    gen2.setMaxQ(15);
    readerClient.setGen2(gen2).await(1, TimeUnit.SECONDS);
    System.out.println("设置 Gen2 成功");

    // 获取 Q算法 参数
    Gen2 value = readerClient.getGen2().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("Q 设置:%s \t startQ 设置:%d \t MinQ 设置:%d \t MaxQ 设置:%d \t select:%d \t session:%d \t tagret:%d\t",
            value.getqValue() == Gen2.Q.DYNAMIC ? "动态Q算法" : "固定Q算法",
            value.getStartQ(),
            value.getMinQ(),
            value.getMaxQ(),
            value.getSelect().getValue(),
            value.getSession().getValue(),
            value.getTarget().getValue()));
  }

  /** 读写器 工作天线 设置/获取 */
  @Test
  public void setAnt() throws Exception {
    // 设置 工作天线1 开启，
    readerClient.setAnts(new Ants().add(1, true)).await(1, TimeUnit.SECONDS);
    System.out.println("设置 Ant 成功");
    // 获取 工作天线 设置
    Ants value = readerClient.getAnts().await(1, TimeUnit.SECONDS);
    List<Ants.Ant> _ants = value.getAnts();
    for (Ants.Ant ant : _ants) {
      System.out.println(String.format("天线号: %d  状态: %s", ant.getIndex(), ant.isOn() ? "开启" : "关闭"));
    }
  }

  /** 读写器 频率区域 设置/获取 */
  @Test
  public void setFrequencyRegion() throws Exception {
    // 保存设置，设置区域为China2
    readerClient.setFrequencyRegion(true, FrequencyRegion.CHINA_2).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取频率区域
    FrequencyRegion value = readerClient.getFrequencyRegion().await(1, TimeUnit.SECONDS);
    System.out.println("当前频率区域为:" + value);
  }

  /** 查询读写器温度 */
  @Test
  public void getTemperature() throws Exception {
    Integer value = readerClient.getTemperature().await(1, TimeUnit.SECONDS);
    System.out.println("当前温度为:" + value);
  }

  /** 查询读写器版本信息 */
  @Test
  public void getVersion() throws Exception {
    // 查询Firmware 版本号
    String firmwareVersion = readerClient.getFirmwareVersion().await(1, TimeUnit.SECONDS);
    System.out.println("Firmware 版本号:" + firmwareVersion);

    // 获取Hardware 版本号
    String hardwareVersion = readerClient.getHardwareVersion().await(1, TimeUnit.SECONDS);
    System.out.println("Hardware 版本号:" + hardwareVersion);
  }

  /** 单次查询标签 */
  @Test
  public void singleRead() throws Exception {
    // 单次查询标签
    Tag tag = readerClient.singleRead().await(4, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d RSSI:%f PC:%d EPC:%s", tag.getAnt(), tag.getRssi(), tag.getPc(), tag.getEpc()));
  }

  /** 查询标签数据 */
  @Test
  public void readTagData() throws Exception {
    // 过滤数据
    byte[] md = HexTools.hexStr2Byte("300833B2DDD9014000000000");
    // 查询标签数据
    TagData tagData = readerClient.readTagData(
            // 访问密码
            "00000000",
            // 过滤参数
            FMB.EPC,
            // 过滤数据
            md,
            // 查询存储区
            BankNo.TID,
            // 查询起始地址
            0x02,
            // 查询数据长度
            0x03).await(4, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  /** 写入标签数据 */
  @Test
  public void writeTagData() throws Exception {
    // 写入数据
    byte[] data = HexTools.hexStr2Byte("11223344");
    // 写入标签数据
    TagData tagData = readerClient.writeTagData(
            // 标签的访问密码
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000000"),
            // 用户需要写入的数据的bank号
            BankNo.USER,
            // 写入的数据的起始地址
            0x02,
            // 需写入的数据长度
            0x04,
            data).await(4, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  /** 锁定标签 */
  @Test
  public void lockTag() throws Exception {
    // 设置锁定参数
    LockParam param = new LockParam();
    // 设置锁定存储区
    param.setEPC(true);
    param.setUser(true);
    // 设置锁定类型
    param.setLockType(LockType.LOCK_FOREVER);
    // 锁定标签
    TagData tagData = readerClient.lockTag(
            // 标签的访问密码
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000001"),
            // 锁定参数
            param).await(3, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  /** 灭活Kill标签 */
  @Test
  public void killTag() throws Exception {
    // 灭活Kill标签
    TagData tagData = readerClient.killTag(
            // 标签的杀死密码(当标签的KillPwd区的值为0x00000000时，标签会忽略kill命令，kill命令不会成功)
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000001")).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("ANT:%d DATA:%s", tagData.getAnt(), HexTools.byteArrayToHexString(tagData.getData())));
  }

  /** 设置循环查询标签工作时间及间断时间 (只适用 M1 设备) */
  @Test
  public void setCyclicQueryWorkAndResponseTime() throws Exception {
    // 设置循环查询标签工作时间及间断时间
    readerClient.setCyclicQueryWorkAndResponseTime(
            // 循环查询标签周期工作时间
            100,
            // 间断时间
            100).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取天线循环工作时间和间隔时间
    CyclicQueryWorkAndResponseTime cyclicQueryWorkAndResponseTime = readerClient.getCyclicQueryWorkAndResponseTime().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("循环查询标签工作时间%d | 间断时间设置响应%d",
            cyclicQueryWorkAndResponseTime.getWorkTime(),
            cyclicQueryWorkAndResponseTime.getInterruptedTime()));
  }


  /** 天线循环工作时间和间隔时间 设置/获取 (只适用 M4 设备) */
  @Test
  public void setAntWorkAndWaitTime() throws Exception {
    // 设置天线循环工作时间和间隔时间
    readerClient.setAntWorkAndWaitTime(
            // 天线 1 的工作时间 （单位 ms，范围 30ms—60000ms）
            100,
            // 天线 2 的工作时间 （单位 ms，范围 30ms—60000ms）
            150,
            // 天线 3 的工作时间 （单位 ms，范围 30ms—60000ms）
            314,
            // 天线 4 的工作时间 （单位 ms，范围 30ms—60000ms）
            30,
            //  等待时间（单位ms，范围 0ms—60000ms）
            10000).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取天线循环工作时间和间隔时间
    AntWorkAndWaitTime antWorkAndWaitTime = readerClient.getAntWorkAndWaitTime().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("Ant1WorkTime:%d Ant2WorkTime:%d Ant3WorkTime:%d Ant4WorkTime:%d WaitTime:%d",
            antWorkAndWaitTime.getAnt1WorkTime(),
            antWorkAndWaitTime.getAnt2WorkTime(),
            antWorkAndWaitTime.getAnt3WorkTime(),
            antWorkAndWaitTime.getAnt4WorkTime(),
            antWorkAndWaitTime.getWaitTime()));
  }

  /** 读写器 FastID功能 设置/获取 */
  @Test
  public void setFastID() throws Exception {
    // 设置读写器FastID功能
    readerClient.setFastID(true).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取读写器FastID状态
    Boolean value = readerClient.getFastID().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("当前 FastID 功能为%s状态", value ? "开启" : "关闭"));
  }

  /** 读写器 模块通讯波特率 设置 */
  @Test
  public void setBaudRate() throws Exception {
    // 设置模块通讯波特率
    readerClient.setBaudRate(BaudRate.B_115200).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");
  }

  /** 读写器 设置开机自动读取标志 设置 */
  @Test
  public void setAutoReadWhenPowerOff() throws Exception {
    // 设置开机自动读取
    Boolean await = readerClient.setAutoReadWhenPowerOff(true).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("开机自动读取标志为: %s", await ? "auto" : "non"));
  }

  /** QT参数 设置/获取 */
  @Test
  public void setQtParam() throws Exception {
    // 设置QT参数
    readerClient.setQtParam(
            // 标签的访问密码
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000001"),
            // 是否启用近距离控制标志
            true,
            // 是否启用Private Memory Map
            false).await(2, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取QT参数
    QtParam qtParam = readerClient.getQtParam(
            // 标签的访问密码
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000001")).await(1, TimeUnit.SECONDS);
    System.out.println(String.format("获取的Qt参数为 \t%s |\t %s", qtParam.isCloseControl() ? "启用近距离控制" : "无近距离控制", qtParam.isEnabledPublicMemoryMap() ? "使用 Public Memory Map" : "启用 Private Memory Map"));
  }

  /** 设置QT读写操作 */
  @Test
  public void setQtOperation() throws Exception {
    // 设置 QT 参数，启用近距离控制，标签使用 Private Memory Map。标签TID=0x112233445566778899001122，通过 TID 前 6 个字节过滤，AP=0x55555555
    readerClient.setQtOperation(
            // 标签的访问密码
            "00000000",
            // 过滤数据类型
            FMB.EPC,
            // 过滤数据
            HexTools.hexStr2Byte("300833B2DDD9014000000001"),
            // QT操作
            QtOperation.NONE,
            // 是否启用近距离控制标志
            true,
            // 是否启用Private Memory Map
            false,
            // QT操作是否掉电保存
            false,

            // 以下参数值只需要在写操作时添加,在读操作时以下参数值无效
            // 用户需要查询的数据的bank号
            BankNo.EPC,
            // 查询的数据的起始地址
            0x00,
            // 写入的数据长度
            0x00,
            // 写入的数据
            null).await(2, TimeUnit.SECONDS);

    // QT读操作
    QtOperation operation = readerClient.setQtOperation(
            "00000000",
            FMB.EPC,
            HexTools.hexStr2Byte("300833B2DDD9014000000001"),
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
    byte[] data = HexTools.hexStr2Byte("300833B2DDD9014000000000");
    readerClient.setQtOperation(
            "00000000",
            FMB.TID,
            HexTools.hexStr2Byte("300833B2DDD9014000000001"),
            QtOperation.WRITE,
            true,
            false,
            false,
            BankNo.EPC,
            0x02,
            0x04,
            data).await(2, TimeUnit.SECONDS);
  }

  /** TAGFOCUS 参数 设置/获取 */
  @Test
  public void setTagFocus() throws Exception {
    // 开启 TAGFOCUS
    Boolean result = readerClient.setTagFocus(true).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功");

    // 获取 TAGFOCUS 参数
    Boolean value = readerClient.getTagFocus().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("当前 TAGFOCUS 功能为 %s状态", value ? "开启" : "关闭"));
  }

  /** 设置蜂鸣器状态 */
  @Test
  public void setBeep() throws Exception {
    // 开启蜂鸣器
    Boolean result = readerClient.setBeep(false).await(1, TimeUnit.SECONDS);
    // TODO 设置成功但 返回的值为 BB 2B 01 01 2D 0D 0A
    assert result;
    System.out.println("设置成功!");
  }

  /** 读写器 工作模式 设置/获取 */
  @Test
  public void setWorkMode() throws Exception {
    // 设置 读写器 工作模式
    Boolean result = readerClient.setWorkMode(WorkMode.EAS_MODE).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");

    // TODO 文档CRC校验位有错误
    // 获取 读写器 工作模式
    WorkMode value = readerClient.getWorkMode().await(1, TimeUnit.SECONDS);
    System.out.println("获取工作模式成功,工作模式为" + value);
  }

  /** 读写器 EAS 参数 设置/获取 */
  @Test
  public void setEASParam() throws Exception {
    // 设置读写器 EAS 参数
    Boolean result = readerClient.setEASParam(1, 0x02).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
    // 获取读写器 EAS 参数
    EAS value = readerClient.getEASParam().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("EAS 区选择的字节为:%d,EAS的值为:%d", value.getBit(), value.getValue()));
  }

  /** 读写器 HeartBeat 参数 设置/获取 */
  @Test
  public void setHeartbeatParam() throws Exception {
    // 设置 HeartBeatTime 为6
    Boolean result = readerClient.setHeartbeatParam(6).await(1, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
    // 获取 HeartBeat 参数
    Integer value = readerClient.getHeartbeatParam().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("心跳包参数为:%d * 30s", value));
  }

  /** 重置读写器 WIFI 模块 */
  @Test
  public void restWifi() throws Exception {
    // 重置读写器 WIFI 模块
    Boolean result = readerClient.restWifi().await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("重置成功!");
  }

  /** 设置读写器分支器间隔时间 */
  @Test
  public void setBranchWorkIntervalTime() throws Exception {
    // 设置分支器工作间隔时间为（1*100）ms
    Boolean result = readerClient.setBranchWorkIntervalTime(1).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  /** 设置通道模式继电器工作时间 */
  @Test
  public void setRelayWorkTime() throws Exception {
    // 设置通道模式继电器工作时间100ms
    Boolean result = readerClient.setRelayWorkTimeNew(1).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
    // 获取通道模式继电器工作时间
    Integer value = readerClient.getRelayWorkTimeNew().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("通道模式继电器工作时间为:%dms", value * 100));
  }

  /** 设置读写器触发工作时间 */
  @Test
  public void setReaderTriggerWorkTime() throws Exception {
    // 设置触发工作时间200ms
    Boolean result = readerClient.setReaderTriggerWorkTime(2).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  /** 设置读写器报警间隔时间 */
  @Test
  public void setReaderAlarmIntervalTime() throws Exception {
    // 设置报警间隔时间3s;
    // TODO 文档CRC校验位有错误
    Boolean result = readerClient.setReaderAlarmIntervalTime(3).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");
  }

  /** 模块重新上电 */
  @Test
  public void restart() throws Exception {
    // 设置当前读写模块重新上电
    Boolean result = readerClient.restart().await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设备已重启!");
  }

  /** 分支器循环工作功率 设置/获取 */
  @Test
  public void setBranchWorkPowers() throws Exception {
    // 设置分支器循环工作功率 TODO 文档发送指令的长度有错误
    Boolean result = readerClient.setBranchWorkPowers(
            // Ant1 功率 0x10
            new BranchAntPowerParam(1, 0x10),
            // Ant2 功率 0x10
            new BranchAntPowerParam(2, 0x11)).await(2, TimeUnit.SECONDS);
    assert result;
    System.out.println("设置成功!");

    // 获取分支器循环工作功率
    List<BranchAntPowerParam> params = readerClient.getBranchWorkPowers().await(2, TimeUnit.SECONDS);
    System.out.println();
    for (BranchAntPowerParam powerParam : params) {
      System.out.println(String.format("Ant:%d Power:%d", powerParam.getIndex(), powerParam.getPower()));
    }
    System.out.println();
  }

  /** 获取设备序列号 */
  @Test
  public void getSerialNum() throws Exception {
    String result = readerClient.getSerialNum().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("序列号为:%s", result));
  }

  /** 通道门进出人数统计 设置/获取 */
  @Test
  public void setChannelDoorCountNUm() throws Exception {
    // 设置通道门进出人数统计
    readerClient.setChannelDoorCountNUm(
            // 设置入馆人数
            1,
            // 设置出馆人数
            1).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取通道门进出人数统计
    ChannelDoorCountNUm value = readerClient.getChannelDoorCountNUm().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("入馆人数为 %d \t 出馆人数为 %d", value.getInCount(), value.getOutCount()));
  }

  /** 设置通道门延迟工作时间 */
  @Test
  public void setChannelDoorDelayWorkTime() throws Exception {
    // 设置通道门延迟工作时间
    readerClient.setChannelDoorDelayWorkTime(10).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");
  }

  /** 16通道读写器天线工作时间 设置/获取 */
  @Test
  public void setAntWorkTimeFor16Channel() throws Exception {
    // 设置通道门延迟工作时间
    readerClient.setAntWorkTimeFor16Channel(1).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取通道门延迟工作时间
    Integer value = readerClient.getAntWorkTimeFor16Channel().await(1, TimeUnit.SECONDS);
    System.out.println(String.format("通道门延迟工作时间为:%d", value));
  }

  /** 16通道读写器工作天线 设置/获取 */
  @Test
  public void getAntsFor16Channel() throws Exception {
    // 设置16通道读写器工作天线
    readerClient.setAntsFor16Channel(
            new Ants()
                    .add(1, true)
    ).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");

    // 获取16通道读写器工作天线
    Ants value = readerClient.getAntsFor16Channel().await(1, TimeUnit.SECONDS);
    List<Ants.Ant> _ants = value.getAnts();
    for (Ants.Ant ant : _ants) {
      System.out.print(String.format("天线号:%d 状态:%s", ant.getIndex(), ant.isOn() ? "开启" : "关闭"));
    }
  }

  /** 设置天线功率 */
  @Test
  public void setAntPower() throws Exception {
    // 设置天线功率
    readerClient.setAntPower(30).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");
  }

  /** 设置屏待机 */
  @Test
  public void setSleep() throws Exception {
    // 设置屏待机
    readerClient.setSleep(2, 1).await(1, TimeUnit.SECONDS);
    System.out.println("设置成功");
  }
}