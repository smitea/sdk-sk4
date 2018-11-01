# 深科RFID读写器SDK
---
深科RFID读写器SDK适用于所有深科物联科技有限公司所生产的读写器,有关产品信息请访问 [深科物联-RFID产品中心](http://www.soonke.com/welcome)
> 此文档只涉及深科RFID读写器SDK的接口调用规范以及接口说明，如需了解更多内容，请自行联系厂商。

## 快速开始

### 使用前说明
项目使用Maven作为依赖管理工具，在开发之前请到 [maven](http://maven.apache.org/) 下载并安装maven管理器。

### 安装Java串口驱动
如果不使用串口连接设备，请跳过此步骤。

驱动已存放在项目文件中，在 [rescources](src/main/resources/) 目录下，可根据自己的系统版本选取相应的动态库文件。
或者使用 ``LibTools`` 工具类的 ``load()`` 方法将驱动加载到JVM中。

### 接口调用说明
在方法调用中,如果方法返回类型为 ``Future <T>`` 类型的接口，可以使用同步的方式，也可以使用异步的方式调用。

* 异步方式:

```java
// 例如:
readerClient.getTxPower().then(new Callback<TxPower>() {
   public void onSuccess(TxPower value) {
     // TODO 执行成功后会回调该接口,value为调用返回后的值
   }

   public void onFailure(Throwable value) {
     // TODO 执行失败后会调用该接口,value为抛出的异常信息
   }
});
```

* 同步方式:
  - 同步等待:同步等待，直到调用成功或者抛出异常信息后才会执行下一行代码。
  例如:``Gpios result = readerClient.getInputGpio(3).await();``
  
  - 指定同步等待时间:同步等待指定时间间隔，直到调用成功或者抛出异常信息后才会执行下一行代码。
  例如:``Gpios result = readerClient.getInputGpio(3).await(2, TimeUnit.SECONDS);``

> 有关于 ``Future<T>`` 具体可参考博客[《Promise实现》](https://smitea.github.io)

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

### 设置事件监听器
当前事件监听器有:

  * 设备连接监听器:设备连接监听器主要用于监听设备连接事件,设备连接事件有:
    1. ``CONNECTION``: 标识设备正在连接中，当调用``connect``方法之前会触发该事件；
    2. ``CONNECTED``: 标识设备已连接，当调用``connect``方法之后并且连接成功会触发该事件；
    3. ``HEART_BEAT``: 标识心跳包消息，当读写器开启循环查询标签时，没有查询到标签后，会定期触发该事件。具体的间隔时间请参考 [设置心跳包参数]()；
    4. ``RETRY_CONNECTION``: 标识重试连接，但检测到读写器已断开时，会重试连接3次，每一次都会触发该事件；
    5. ``DISCONNECTION``: 标识正在断开连接，当调用``disconnect``之前或者当检测到读写器已断开时并且重新尝试连接时，会触发该事件；
    6. ``DISCONNECTED``: 标识连接已断开，当调用``disconnect``之后或者当检测到读写器已断开时并且重新3次失败后，会触发该事件；
  * 设备异常监听器: 设备内置错误指令回调监听；
  * 通道门进出返回值响应监听器: 该监听器只适用于特殊设备场景中,普通RFID读写器可以不设置，在此不作以说明；

设置事件监听器实例:

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
 任何设置的接口在设置失败时都会由 ``Promise`` 抛出 ``GetException`` 异常信息，当然获取的接口在获取失败时也会由 ``Promise`` 抛出 ``SetException`` 异常信息。

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
