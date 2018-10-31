package com.ridko.sk4;

import com.ridko.sk4.entity.*;
import com.ridko.sk4.promise.Promise;

import java.util.List;

/**
 * 操作指令层
 *
 * @author smitea
 * @since 2018-10-30
 */
interface ICommand {
  /**
   * 设置功率
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
   * 设置 gen2 参数
   *
   * @param gen2 gen2 参数
   */
  public Promise<Boolean> setGen2(Gen2 gen2);

  /**
   * 查询 gen2 参数设置
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
   * @param password 标签的访问密码
   * @param fmb 过滤数据类型
   * @param md 过滤数据
   * @param param 锁定参数
   * @return 标签数据
   */
  public Promise<TagData> lockTag(String password, FMB fmb, byte[] md, LockParam param);

  /**
   * 杀死标签
   * @param password 杀死密码
   * @param fmb 过滤数据类型
   * @param md 过滤数据
   * @return 标签数据
   */
  public Promise<TagData> killTag(String password, FMB fmb, byte[] md);

  /**
   * 设置循环查询标签工作时间及间断时间
   * @param workTime 循环查询标签工作时间
   * @param interruptedTime 间断时间
   */
  public Promise<Boolean> setCyclicQueryWorkAndResponseTime(int workTime,int interruptedTime);

  /**
   * 查询循环查询标签工作时间及间断时间
   * <p style="color:red">该命令仅限于 M1 模块使用,如 M4 模块使用，会发生异常</p>
   * @return 循环查询标签工作时间及间断时间设置
   */
  public Promise<CyclicQueryWorkAndResponseTime> getCyclicQueryWorkAndResponseTime();

  /**
   * 设置天线工作时间及等待时间
   * <p style="color:red">设置天线工作时间只适用 M4 设备。一旦设置后，模块断电后，会默认将四个天线的工作时间，都统一设置为 ANT1 的工作时间。</p>
   * @param ant1WorkTime 天线 1 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant2WorkTime 天线 2 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant3WorkTime 天线 3 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param ant4WorkTime 天线 4 的工作时间 （单位 ms，范围 30ms—60000ms）
   * @param waitTime 等待时间（单位ms，范围 0ms—60000ms）
   */
  public Promise<Boolean> setAntWorkAndWaitTime(int ant1WorkTime,int ant2WorkTime,int ant3WorkTime,int ant4WorkTime,int waitTime);

  /**
   * 设置天线工作时间及等待时间
   * <p style="color:red">设置天线工作时间只适用 M4 设备。一旦设置后，模块断电后，会默认将四个天线的工作时间，都统一设置为 ANT1 的工作时间。</p>
   * @return 天线工作时间及等待时间设置
   */
  public Promise<AntWorkAndWaitTime> getAntWorkAndWaitTime();
}
