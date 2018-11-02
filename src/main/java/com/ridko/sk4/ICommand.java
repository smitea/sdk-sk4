package com.ridko.sk4;

import com.ridko.sk4.entity.*;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.promise.Promise;

import java.util.List;

/**
 * 操作指令层
 *
 * @author smitea
 * @since 2018-10-30
 */
public interface ICommand {
  /**
   * 设置功率
   * <p style="color:red">读功率/写功率取值范围 5-30, 单位 dBm</p>
   *
   * @param readPower  读功率
   * @param writePower 写功率
   * @param isLoop     是否开环
   */
  public Promise<Boolean> setTxPower(int readPower, int writePower, boolean isLoop);

  /**
   * 获取功率
   *
   * @return 功率参数
   */
  public Promise<TxPower> getTxPower();

  /**
   * 设置GPIO状态
   *
   * @param gpios GPIO列表
   */
  public Promise<Boolean> setGpio(Gpios gpios);

  /**
   * 获取GPIO状态
   *
   * @return GPIO列表
   */
  public Promise<Gpios> getGpio();

  /**
   * 获取指定索引的GPIO状态
   *
   * @return GPIO值
   */
  public Promise<Boolean> getGpio(int index);

  /**
   * 查询输入 GPIO 状态
   *
   * @param index 需要查询得GPIO索引列表
   * @return 返回GPIO状态列表
   */
  public Promise<Gpios> getInputGpio(int... index);

  /**
   * 设置射频输出频率
   *
   * @param freqs 跳频频点列表
   */
  public Promise<Boolean> setOutputFrequency(int... freqs);

  /**
   * 查询射频频率状态
   *
   * @return 跳频频点列表
   */
  public Promise<List<Integer>> getOutputFrequency();

  /**
   * 设置 Q算法 参数设置
   *
   * @param gen2 gen2 参数
   */
  public Promise<Boolean> setGen2(Gen2 gen2);

  /**
   * 查询 Q算法 参数
   *
   * @return gen2 参数
   */
  public Promise<Gen2> getGen2();

  /**
   * 设置工作天线
   *
   * @param ants 天线参数列表
   */
  public Promise<Boolean> setAnts(Ants ants);

  /**
   * 获取工作天线设置
   *
   * @return 工作天线设置
   */
  public Promise<Ants> getAnts();

  /**
   * 设置读写器频率区域
   *
   * @param isSave 是否保存设置
   * @param region 频率区域
   */
  public Promise<Boolean> setFrequencyRegion(boolean isSave, FrequencyRegion region);

  /**
   * 查询频率区域设置
   *
   * @return 频率区域设置
   */
  public Promise<FrequencyRegion> getFrequencyRegion();

  /**
   * 查询读写器当前温度
   *
   * @return 读写器当前温度
   */
  public Promise<Integer> getTemperature();

  /**
   * 获取Hardware 版本号
   *
   * @return Hardware 版本号
   */
  public Promise<String> getHardwareVersion();

  /**
   * 查询Firmware 版本号
   *
   * @return Firmware 版本号
   */
  public Promise<String> getFirmwareVersion();

  /**
   * 单次查询标签ECP
   *
   * @return 标签数据
   */
  public Promise<Tag> singleRead();

  /**
   * 查询标签数据
   *
   * @param password 标签的访问密码
   * @param fmb      过滤数据类型
   * @param md       过滤数据
   * @param mb       需查询的数据的bank号
   * @param dl       需查询的数据长度
   * @param sa       需查询的数据的起始地址
   * @return 标签数据
   */
  public Promise<TagData> readTagData(String password, FMB fmb, byte[] md, BankNo mb, int sa, int dl);

  /**
   * 写入标签数据
   *
   * @param password 标签的访问密码
   * @param fmb      过滤数据类型
   * @param md       过滤数据
   * @param mb       需要写入的数据的 bank号
   * @param sa       需写入的数据的起始地址
   * @param dl       需写入的数据长度
   * @param data     写入的数据
   * @return 标签数据
   */
  public Promise<TagData> writeTagData(String password, FMB fmb, byte[] md, BankNo mb, int sa, int dl, byte[] data);

  /**
   * 锁定标签
   *
   * @param password 标签的访问密码
   * @param fmb      过滤数据类型
   * @param md       过滤数据
   * @param param    锁定参数
   * @return 标签数据
   */
  public Promise<TagData> lockTag(String password, FMB fmb, byte[] md, LockParam param);

  /**
   * 杀死标签
   *
   * @param password 杀死密码
   * @param fmb      过滤数据类型
   * @param md       过滤数据
   * @return 标签数据
   */
  public Promise<TagData> killTag(String password, FMB fmb, byte[] md);

  /**
   * 设置 QT 参数
   * <p style="color:red">该命令用于配置标签的工作参数。仅对 Impinj 的 Monza 4QT 标签有效。</p>
   *
   * @param password                 标签的访问密码
   * @param fmb                      过滤数据类型
   * @param md                       过滤数据
   * @param isCloseControl           是否启用近距离控制
   * @param isEnabledPublicMemoryMap 是否使用 Public Memory Map
   */
  public Promise<Boolean> setQtParam(String password, FMB fmb, byte[] md, boolean isCloseControl, boolean isEnabledPublicMemoryMap);

  /**
   * 获取 QT 参数
   *
   * @param password 标签的访问密码
   * @param fmb      过滤数据类型
   * @param md       过滤数据
   * @return Qt参数
   */
  public Promise<QtParam> getQtParam(String password, FMB fmb, byte[] md);

  /**
   * 设置 QT 操作(设置之后执行读取操作)
   * <p style="color:red">该命令用于配置标签的工作参数。仅对 Impinj 的 Monza 4QT 标签有效。</p>
   * 其中
   * <code>bankNo</code>
   * <code>sa</code>
   * <code>dl</code>
   * <code>data</code>
   * 参数值只需要在写操作时使用,在读操作时使用这些参数值是无效。
   *
   * @param password                 标签的访问密码
   * @param fmb                      过滤数据类型
   * @param md                       过滤数据
   * @param operation                执行Qt操作类型
   * @param isCloseControl           是否启用近距离控制
   * @param isEnabledPublicMemoryMap 是否使用 Public Memory Map
   * @param isSave                   QT 操作是否掉电保存
   * @param bankNo                   需要查询的数据的 bank 号
   * @param sa                       需查询的数据的起始地址
   * @param dl                       需写入的数据长度
   * @param data                     写入的数据
   * @return 执行Qt操作成功后需要执行的操作类型
   */
  public Promise<QtOperation> setQtOperation(String password, FMB fmb, byte[] md, QtOperation operation, boolean isCloseControl, boolean isEnabledPublicMemoryMap, boolean isSave, BankNo bankNo, int sa, int dl, byte[] data);

  /**
   * 设置循环查询标签工作时间及间断时间
   * <p style="color:red">该命令仅限于 M1 模块使用,如 M4 模块使用，会发生异常</p>
   *
   * @param workTime        循环查询标签工作时间
   * @param interruptedTime 间断时间
   */
  public Promise<Boolean> setCyclicQueryWorkAndResponseTime(int workTime, int interruptedTime);

  /**
   * 查询循环查询标签工作时间及间断时间
   * <p style="color:red">该命令仅限于 M1 模块使用,如 M4 模块使用，会发生异常</p>
   *
   * @return 循环查询标签工作时间及间断时间设置
   */
  public Promise<CyclicQueryWorkAndResponseTime> getCyclicQueryWorkAndResponseTime();

  /**
   * 设置天线循环工作时间和间隔时间
   * <p style="color:red">设置天线工作时间只适用 M4 设备。一旦设置后，模块断电后，会默认将四个天线的工作时间，都统一设置为 ANT1 的工作时间。</p>
   *
   * @param ant1WorkTime 天线 1 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant2WorkTime 天线 2 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant3WorkTime 天线 3 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant4WorkTime 天线 4 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param waitTime     等待时间（单位ms，范围 0ms—60000ms）
   */
  public Promise<Boolean> setAntWorkAndWaitTime(int ant1WorkTime, int ant2WorkTime, int ant3WorkTime, int ant4WorkTime, int waitTime);

  /**
   * 设置天线工作时间及等待时间
   * <p style="color:red">设置天线工作时间只适用 M4 设备。一旦设置后，模块断电后，会默认将四个天线的工作时间，都统一设置为 ANT1 的工作时间。</p>
   *
   * @return 天线工作时间及等待时间设置
   */
  public Promise<AntWorkAndWaitTime> getAntWorkAndWaitTime();

  /**
   * 设置 FastID
   * <p style="color:red">FastID 只对特定品种的标签有效。</p>
   *
   * @param isOn 是否开启 FastID
   */
  public Promise<Boolean> setFastID(boolean isOn);

  /**
   * 获取FastID 设置
   * <p style="color:red">FastID 只对特定品种的标签有效。</p>
   *
   * @return 返回FastID设置状态
   */
  public Promise<Boolean> getFastID();

  /**
   * 设置模块通讯波特率
   * <p style="color:red">只适用于单独模块，不适用设备。</p>
   *
   * @param baudRate 模块通讯波特率
   */
  public Promise<Boolean> setBaudRate(BaudRate baudRate);

  /**
   * 设置开机自动读取标志
   *
   * @param isAuto 开机自动读取标志
   *               <ul>
   *               <li>true 自动读取</li>
   *               <li>false 手动</li>
   *               </ul>
   */
  public Promise<Boolean> setAutoReadWhenPowerOff(boolean isAuto);

  /**
   * 设置 TAGFOCUS
   * <p style="color:red">TAGFOCUS 只对特定品种的标签有效。</p>
   *
   * @param isEnabled 是否开启
   */
  public Promise<Boolean> setTagFocus(boolean isEnabled);

  /**
   * 获取 TAGFOCUS 设置
   * <p style="color:red">TAGFOCUS 只对特定品种的标签有效。</p>
   *
   * @return 返回当前TAGFOCUS状态
   */
  public Promise<Boolean> getTagFocus();

  /**
   * 设置蜂鸣器开关
   *
   * @param isEnabled 是否启动蜂鸣器
   */
  public Promise<Boolean> setBeep(boolean isEnabled);

  /**
   * 工作模式设置
   *
   * @param workMode 工作模式
   */
  public Promise<Boolean> setWorkMode(WorkMode workMode);

  /**
   * 获取工作模式设置
   *
   * @return 工作模式设置
   */
  public Promise<WorkMode> getWorkMode();

  /**
   * 设置 EAS 参数
   *
   * @param bits  EAS 区选择的字节，范围在（0-12）之间
   * @param value EAS 的值，范围在（0x00~0xFF）之间
   */
  public Promise<Boolean> setEASParam(int bits, int value);

  /**
   * 获取 EAS 参数
   *
   * @return EAS参数
   */
  public Promise<EAS> getEASParam();

  /**
   * 设置心跳包参数
   * <p>读写器只有在一段连续的时间内（超过心跳包时间间隔）没有读到任何标签数据，才会上传心跳包。请监听 {@link AbstractConnection#setConnectEventIListenter(IListenter)} 中的 <code>HEART_BEAT</code> 心跳包事件来检测心跳包</p>
   *
   * @param heartbeat 读写器上传心跳包的时间间隔。以 30 秒为单位，参数范围 0 秒～255 ×30 秒。（默认值 HeartBeatTime 为 10）
   */
  public Promise<Boolean> setHeartbeatParam(int heartbeat);

  /**
   * 获取心跳包参数
   * <p>心跳包时间间隔参数，读写器上传心跳包的时间间隔。以30 秒为单位，参数范围 0 秒～255×10 秒。（默认值 HeartBeatTime 为 10）</p>
   *
   * @return 心跳包参数
   */
  public Promise<Integer> getHeartbeatParam();

  /**
   * 重置无限模块
   */
  public Promise<Boolean> restWifi();

  /**
   * 天线分支器工作间隔时间设置
   * <p>天线分支器工作间隔时间参数; 以 100ms 为单位，参数范围 0秒～100×100ms。（默认值 TIME 为 1）</p>
   *
   * @param intervalTime 间隔时间
   */
  public Promise<Boolean> setBranchWorkIntervalTime(int intervalTime);

  /**
   * 设置当前分支器循环工作功率的大小
   * <p style="color:red">设置天线功率只适用设备连接分支器</p>
   *
   * @param params 分支器循环工作功率设置列表
   */
  public Promise<Boolean> setBranchWorkPowers(BranchAntPowerParam... params);

  /**
   * 获取分支器循环工作功率
   * <p style="color:red">设置天线功率只适用设备连接分支器</p>
   * @return 分支器循环工作功率列表
   */
  public Promise<List<BranchAntPowerParam>> getBranchWorkPowers();

  /**
   * 设置通道模式继电器工作时间(V2.0版本)
   * <p>继电器工作时间参数; 以100ms为单位，参数范围100ms～255*100ms。（默认值TIME为 10）</p>
   *
   * @param relay 继电器工作时间
   */
  public Promise<Boolean> setRelayWorkTimeNew(int relay);

  /**
   * 获取通道模式继电器工作时间(V2.0版本)
   * <p>继电器工作时间参数; 以100ms为单位，参数范围100ms～255*100ms。（默认值TIME为 10）</p>
   *
   * @return 继电器工作时间参数
   */
  public Promise<Integer> getRelayWorkTimeNew();

  /**
   * 设置读写器触发工作时间
   * <p>触发工作时间参数; 以 100ms 为单位，参数范围 100ms～256*100s。（默认值 TIME 为 2）</p>
   *
   * @param time 触发读写器工作时间
   */
  public Promise<Boolean> setReaderTriggerWorkTime(int time);

  /**
   * 设置读写器报警间隔时间
   *
   * @param intervalTime 设置报警间隔时间参数; 以 1s 为单位，参数范围 3s～120s。（默认值 TIME为 5）
   */
  public Promise<Boolean> setReaderAlarmIntervalTime(int intervalTime);

  /**
   * 模块重新上电
   */
  public Promise<Boolean> restart();

  /**
   * 获取设备序列号
   *
   * @return 返回设备序列号
   */
  public Promise<String> getSerialNum();

  /**
   * 获取通道门进出人数统计
   * @return 通道门进出人数记录统计
   */
  public Promise<ChannelDoorCountNUm> getChannelDoorCountNUm();

  /**
   * 设置通道门进出人数统计
   * @return 通道门进出人数记录统计
   */
  public Promise<Boolean> setChannelDoorCountNUm(int inNum,int outNum);

  /**
   * 设置通道门延迟工作时间
   * @param delayWorkTime 通道门延迟工作时间
   */
  public Promise<Boolean> setChannelDoorDelayWorkTime(int delayWorkTime);

  /**
   * 设置16通道读写器天线工作时间
   * @param time 天线工作时间
   */
  public Promise<Boolean> setAntWorkTimeFor16Channel(int time);

  /**
   * 获取16通道读写器天线工作时间
   * @return  天线工作时间
   */
  public Promise<Integer> getAntWorkTimeFor16Channel();

  /**
   * 获取16通道读写器工作天线
   * @return 工作天线列表
   */
  public Promise<Ants> getAntsFor16Channel();

  /**
   * 设置16通道读写器工作天线
   * @param ants 工作天线列表
   */
  public Promise<Boolean> setAntsFor16Channel(Ants ants);

  /**
   * 设置天线功率
   * @param power 天线功率
   */
  public Promise<Boolean> setAntPower(int power);

  /**
   * 设置屏显示数据
   * @param cNum 串口号
   * @param pNum 屏号
   * @param data 显示屏信息
   */
  @Deprecated
  public Promise<Boolean> setDisplayData(int cNum,int pNum,byte[]... data);

  /**
   * 设置屏待机
   * @param cNum 串口号
   * @param pNum 屏号
   */
  public Promise<Boolean> setSleep(int cNum,int pNum);
}
