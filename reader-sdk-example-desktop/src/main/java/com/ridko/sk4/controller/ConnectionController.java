package com.ridko.sk4.controller;

import com.ridko.sk4.*;
import com.ridko.sk4.common.Dialogs;
import com.ridko.sk4.common.ThreadExcutorUntils;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 连接控制器
 *
 * @author smitea
 * @since 2018-11-03
 */
public class ConnectionController extends AbstactMessageCallback {

  /** TCP HOST输入框 */
  public TextField connection_tcp_host;
  /** TCP 端口输入框 */
  public TextField connection_tcp_port;

  /** 校验码选项框 */
  public ComboBox<String> connection_com_parity;
  /** 停止位选项框 */
  public ComboBox<String> connection_com_stop;
  /** 数据位选项框 */
  public ComboBox<String> connection_com_data;
  /** 波特率选项框 */
  public ComboBox<Integer> connection_com_baudrate;
  /** 串口选项框 */
  public ComboBox<String> connection_com_name;

  /** 串口链接按钮 */
  public Button connection_com_button;
  /** TCP链接按钮 */
  public Button connection_tcp_button;

  /** 串口参数 */
  private SerialParam serialParam = new SerialParam("");

  /** 成功连接设备后回调此接口 */
  private IConnectionCallback connectionCallback;
  /** 创建连接对象后回调此接口 */
  private Callback<IReaderConnection,Void> addListenterCallback;

  /** 是否未连接 */
  private Property<Boolean> isDisConnection = new SimpleBooleanProperty(false);

  /** 存放设备操作接口对象 */
  public AtomicReference<ICommand> commandAtomicReference = new AtomicReference<>();
  /** 存放设备连接器对象 */
  public AtomicReference<IReaderConnection> connectionAtomicReference = new AtomicReference<>();

  public void setConnectionCallback(IConnectionCallback connectionCallback) {
    this.connectionCallback = connectionCallback;
  }

  public void setAddListenterCallback(Callback<IReaderConnection, Void> addListenterCallback) {
    this.addListenterCallback = addListenterCallback;
  }

  public void initialize() {
    // 查找系统串口路径
    List<String> ports = SerialParam.findPort();
    // 绑定串口名称
    connection_com_name.getItems().addAll(ports);
    // 默认选中第一个
    connection_com_name.getSelectionModel().selectFirst();

    // 添加波特率
    connection_com_baudrate.getItems().add(9600);
    connection_com_baudrate.getItems().add(19200);
    connection_com_baudrate.getItems().add(38400);
    connection_com_baudrate.getItems().add(57600);
    connection_com_baudrate.getItems().add(115200);
    // 默认为115200
    connection_com_baudrate.getSelectionModel().select(4);

    // 添加奇偶校验位
    connection_com_parity.getItems().add("NONE");
    connection_com_parity.getItems().add("ODD");
    connection_com_parity.getItems().add("EVEN");
    connection_com_parity.getItems().add("MARK");
    connection_com_parity.getItems().add("SPACE");
    // 默认无奇偶校验位
    connection_com_parity.getSelectionModel().select(0);

    // 添加停止位
    connection_com_stop.getItems().add("1");
    connection_com_stop.getItems().add("2");
    connection_com_stop.getItems().add("1.5");
    // 默认为1位停止位
    connection_com_stop.getSelectionModel().select(0);

    // 添加数据位
    connection_com_data.getItems().add("5");
    connection_com_data.getItems().add("6");
    connection_com_data.getItems().add("7");
    connection_com_data.getItems().add("8");
    // 默认为8位数据位
    connection_com_data.getSelectionModel().select(3);

    // 绑定按钮启用状态
    connection_tcp_button.disableProperty().bind(isDisConnection);
    connection_com_button.disableProperty().bind(isDisConnection);

    // TCP 按钮事件处理
    connection_tcp_button.setOnAction(event -> {
      // 获取连接参数
      String tcpHostText = connection_tcp_host.getText();
      String tcpPortText = connection_tcp_port.getText();
      // 校验参数
      if (StringUtils.isNotEmpty(tcpHostText) && StringUtils.isNotEmpty(tcpPortText) && StringUtils.isNumeric(tcpPortText)) {
        if (connectionCallback != null) {
          // 创建TCP连接对象
          IReaderConnection<SocketAddress> tcpConnection = ReaderConnectionBuild.createTcpConnection();
          if(addListenterCallback!=null){
            // 触发回调，并在回调中处理连接设备前的操作
            addListenterCallback.call(tcpConnection);
          }
          // 创建TCP连接参数
          InetSocketAddress inetSocketAddress = new InetSocketAddress(tcpHostText, Integer.parseInt(tcpPortText));

          ThreadExcutorUntils.submit(() -> {
            try {
              // 禁用连接按钮
              this.isDisConnection.setValue(true);
              // 获取设备操作接口
              ICommand command = tcpConnection.connect(inetSocketAddress).await(10, TimeUnit.SECONDS);

              // 保存设备操作接口对象
              commandAtomicReference.getAndSet(command);
              // 保存设备连接器
              connectionAtomicReference.getAndSet(tcpConnection);
              // 通知完成设备连接
              connectionCallback.callback();

              // 启用连接按钮
              this.isDisConnection.setValue(false);
            } catch (Exception e) {
              // 通知显示错误消息
              notify("TCP连接超时", "red");
              // 启用连接按钮
              this.isDisConnection.setValue(false);
            }
          });
        }
      } else {
        notify("TCP连接参数错误", "red");
      }
    });

    // 串口 按钮事件处理
    connection_com_button.setOnAction(event -> {
      // 设置串口参数
      serialParam.setPortName(connection_com_name.getValue());
      serialParam.setBaudrate(connection_com_baudrate.getValue());
      serialParam.setDatabits(5+connection_com_data.getSelectionModel().getSelectedIndex());
      serialParam.setParity(connection_com_parity.getSelectionModel().getSelectedIndex());
      serialParam.setStopbits(1+connection_com_stop.getSelectionModel().getSelectedIndex());

      if (connectionCallback != null) {
        // 创建串口连接对象
        IReaderConnection<SerialParam> serialConnection = ReaderConnectionBuild.createSerialConnection();
        if(addListenterCallback!=null){
          addListenterCallback.call(serialConnection);
        }

        ThreadExcutorUntils.submit(() -> {
          try {
            this.isDisConnection.setValue(true);
            ICommand command = serialConnection.connect(serialParam).await(10, TimeUnit.SECONDS);

            commandAtomicReference.getAndSet(command);
            connectionAtomicReference.getAndSet(serialConnection);
            this.isDisConnection.setValue(false);
            connectionCallback.callback();
          } catch (Exception e) {
            notify("串口连接超时", "red");
            this.isDisConnection.setValue(false);
          }
        });
      }
    });
  }
}
