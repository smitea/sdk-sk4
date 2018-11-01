# 深科RFID读写器SDK
---
深科RFID读写器SDK适用于所有深科物联科技有限公司所生产的读写器,有关产品信息请访问[深科物联-RFID产品中心](http://www.soonke.com/welcome)

## 快速开始

### 使用前说明
项目使用Maven作为依赖管理工具，在开发之前请到[maven](http://maven.apache.org/)下载并安装maven管理器。

### 安装Java串口驱动
如果不使用串口连接设备，请跳过此步骤。

驱动已存在项目文件中，在 [rescources](src/main/resources/) 目录下，可根据自己的系统版本选取相应的动态库文件。
或者使用 ``LibTools`` 工具类的 ``load`` 方法将驱动加载到JVM中。

### 连接RFID读写器设备
  * 使用串口连接：（更多串口参数请参考SerialParam类的说明）
  ```java
 // 指定串口地址
 SerialParam serialParam = new SerialParam("COM5");
 // 指定波特率
 serialParam.setBaudrate(115200);
 // 创建串口连接器
 IReaderConnection<SerialParam> futureConnection = ReaderConnectionBuild.createSerialConnection();
 // 连接设备并获取读写器操作接口
 ICommand readerClient = futureConnection.connect(serialParam).await();
  ```
  * 使用TCP连接：
  ```java
  // 创建TCP连接器
  IReaderConnection<SocketAddress> futureConnection = ReaderConnectionBuild.createTcpConnection();
  // 连接设备并获取读写器操作接口
  ICommand readerClient =  futureConnection.connect(new InetSocketAddress("192.168.1.10",8001)).await();
  ```
> 在方法调用中,如果方法返回类型为```Promise<T>`` 类型的接口 ，可以使用同步的方式，也可以使用异步的方式调用。请参考[博客](https://smitea.github.io)的 《Promise实现》
### 设置事件监听器
  ```java
    // 设置连接事件监听(当所有连接事件，包括连接状态、心跳包都通过该方法回调通知)
    futureConnection.setConnectEventIListenter(new IListenter<ConnectEvent>() {
      public void notify(ConnectEvent event) {
        System.out.println(event.getMsg());
      }
    });
    // 设置设备异常监听
    futureConnection.setErrorEventIListenter(new IListenter<ErrorEvent>() {
      public void notify(ErrorEvent errorEvent) {
        System.out.println(errorEvent.getMsg());
      }
    });
    
    // 设置通道门进出返回值响应监听
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
  ```
### 关闭设备连接
```java
   // 关闭设备连接之前，会先调用停止循环查询标签数据的方法，所以在准备断开连接之前，无须停止循环查询标签数据
   futureConnection.disconnect().await();
```
### 开始循环查询标签数据
```java
    // 在查询之前请先监听标签数据的回调
    futureConnection.setTagListenter(new ITagListenter() {
      public void notify(Tag tag) {
        System.out.println(String.format("ANT:%d EPC:%s PC:%d RSSI:%f", tag.getAnt(), tag.getEpc(), tag.getPc(), tag.getRssi()));
      }
    });
    // 开始循环查询标签数据
    futureConnection.start();
```
### 停止循环查询标签数据
```java
    futureConnection.stop().await(2, TimeUnit.SECONDS);
```
### 设置命令

#### 参数设置

 1. 功率设置/读取
```java
    // 设置读功率为30,写功率为28
    readerClient.setTxPower(30, 28, true).then(new Callback<Boolean>() {
      public void onSuccess(Boolean value) {
        System.out.println("功率设置成功");
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
        System.out.println("功率设置失败");
      }
    });

    // 获取功率
    readerClient.getTxPower().then(new Callback<TxPower>() {
      public void onSuccess(TxPower value) {
        System.out.println(String.format("功率获取成功:读功率-%d 写功率-%d ", value.getReadPower(), value.getWritePower(), value.isLoop() ? "开环" : "闭环"));
      }

      public void onFailure(Throwable value) {
        value.fillInStackTrace();
        System.out.println("功率获取失败");
      }
    });
```
  GPIO状态设置/读取
