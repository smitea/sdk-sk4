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

  public TextField connection_tcp_host;
  public TextField connection_tcp_port;

  public ComboBox<String> connection_com_parity;
  public ComboBox<String> connection_com_stop;
  public ComboBox<String> connection_com_data;
  public ComboBox<Integer> connection_com_baudrate;
  public ComboBox<String> connection_com_name;

  public Button connection_com_button;
  public Button connection_tcp_button;

  private SerialParam serialParam = new SerialParam("");

  private IConnectionCallback connectionCallback;
  private Callback<IReaderConnection,Void> addListenterCallback;

  private Property<Boolean> isConnection = new SimpleBooleanProperty(false);

  public AtomicReference<ICommand> commandAtomicReference = new AtomicReference<>();
  public AtomicReference<IReaderConnection> connectionAtomicReference = new AtomicReference<>();

  public void setConnectionCallback(IConnectionCallback connectionCallback) {
    this.connectionCallback = connectionCallback;
  }

  public void setAddListenterCallback(Callback<IReaderConnection, Void> addListenterCallback) {
    this.addListenterCallback = addListenterCallback;
  }

  public void initialize() {
    List<String> ports = SerialParam.findPort();
    connection_com_name.getItems().addAll(ports);
    connection_com_name.getSelectionModel().selectFirst();

    connection_com_baudrate.getItems().add(9600);
    connection_com_baudrate.getItems().add(19200);
    connection_com_baudrate.getItems().add(38400);
    connection_com_baudrate.getItems().add(57600);
    connection_com_baudrate.getItems().add(115200);
    connection_com_baudrate.getSelectionModel().select(4);

    connection_com_parity.getItems().add("NONE");
    connection_com_parity.getItems().add("ODD");
    connection_com_parity.getItems().add("EVEN");
    connection_com_parity.getItems().add("MARK");
    connection_com_parity.getItems().add("SPACE");
    connection_com_parity.getSelectionModel().select(0);

    connection_com_stop.getItems().add("1");
    connection_com_stop.getItems().add("2");
    connection_com_stop.getItems().add("1.5");
    connection_com_stop.getSelectionModel().select(0);

    connection_com_data.getItems().add("5");
    connection_com_data.getItems().add("6");
    connection_com_data.getItems().add("7");
    connection_com_data.getItems().add("8");
    connection_com_data.getSelectionModel().select(3);

    connection_tcp_button.disableProperty().bind(isConnection);
    connection_com_button.disableProperty().bind(isConnection);

    connection_tcp_button.setOnAction(event -> {
      String tcpHostText = connection_tcp_host.getText();
      String tcpPortText = connection_tcp_port.getText();
      if (StringUtils.isNotEmpty(tcpHostText) && StringUtils.isNotEmpty(tcpPortText) && StringUtils.isNumeric(tcpPortText)) {
        if (connectionCallback != null) {
          IReaderConnection<SocketAddress> tcpConnection = ReaderConnectionBuild.createTcpConnection();
          if(addListenterCallback!=null){
            addListenterCallback.call(tcpConnection);
          }
          InetSocketAddress inetSocketAddress = new InetSocketAddress(tcpHostText, Integer.parseInt(tcpPortText));

          ThreadExcutorUntils.submit(() -> {
            try {
              this.isConnection.setValue(true);
              ICommand command = tcpConnection.connect(inetSocketAddress).await(10, TimeUnit.SECONDS);

              commandAtomicReference.getAndSet(command);
              connectionAtomicReference.getAndSet(tcpConnection);
              connectionCallback.callback();

              this.isConnection.setValue(false);
            } catch (Exception e) {
              e.printStackTrace();
              notify("TCP连接超时", "red");
              this.isConnection.setValue(false);
            }
          });
        }
      } else {
        notify("TCP连接参数错误", "red");
      }
    });

    connection_com_button.setOnAction(event -> {
      serialParam.setPortName(connection_com_name.getValue());
      serialParam.setBaudrate(connection_com_baudrate.getValue());
      serialParam.setDatabits(connection_com_data.getSelectionModel().getSelectedIndex());
      serialParam.setParity(connection_com_data.getSelectionModel().getSelectedIndex());
      serialParam.setStopbits(connection_com_data.getSelectionModel().getSelectedIndex());

      if (connectionCallback != null) {
        IReaderConnection<SocketAddress> serialConnection = ReaderConnectionBuild.createTcpConnection();
        if(addListenterCallback!=null){
          addListenterCallback.call(serialConnection);
        }

        ThreadExcutorUntils.submit(() -> {
          try {
            this.isConnection.setValue(true);
            ICommand command = serialConnection.connect(serialParam).await(10, TimeUnit.SECONDS);

            commandAtomicReference.getAndSet(command);
            connectionAtomicReference.getAndSet(serialConnection);
            this.isConnection.setValue(false);
            connectionCallback.callback();
          } catch (Exception e) {
            notify("串口连接超时", "red");
            this.isConnection.setValue(false);
          }
        });
      }
    });
  }

  public void disconnect() {
    if (connectionAtomicReference.get() != null) {
      connectionAtomicReference.get().disconnect();
    }
  }
}
