package com.ridko.sk4.controller;

import com.ridko.sk4.ICommand;
import com.ridko.sk4.IFutureConnection;
import com.ridko.sk4.IMessageCallback;
import com.ridko.sk4.IReaderConnection;
import com.ridko.sk4.common.ThreadExcutorUntils;
import com.ridko.sk4.common.ViewLoads;
import com.ridko.sk4.domain.TagMapper;
import com.ridko.sk4.entity.Tag;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ITagListenter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author smitea
 * @since 2018-11-03
 */
public class MainController implements IMessageCallback {
  public BorderPane root_panel;

  public Label connection_state_label;
  public Label setting_message_label;
  public Label time_label;
  public Label version_label;

  public MenuItem connect_menu;
  public MenuItem setting_btn;

  public Button start_button;
  public Tooltip start_tooltip;
  public Button single_button;
  public Button tagdata_button;
  public Button break_button;
  public Button clear_button;

  public TextField epc_edit;
  public Button sreach_button;

  public TableView<TagMapper> tag_table;
  public TableColumn<TagMapper,Integer> num_column;
  public TableColumn<TagMapper,Integer> ant_column;
  public TableColumn<TagMapper,String> epc_column;
  public TableColumn<TagMapper,Integer> pc_column;
  public TableColumn<TagMapper,Integer> count_column;
  public TableColumn<TagMapper,String> time_column;

  public Label tag_time_label;
  public Label tag_count_label;
  public Label tag_rate_label;

  public CheckBox ant1_checkbox;
  public CheckBox ant4_checkbox;
  public CheckBox ant3_checkbox;
  public CheckBox ant2_checkbox;
  public Button ant_setting_button;
  public Button ant_getting_button;


  public ComboBox<Integer> read_power_combox;
  public ComboBox<Integer> write_power_combox;
  public Button power_get_button;
  public Button power_setting_button;

  public RadioButton beep_radio_enabled;
  public RadioButton beep_radio_disabled;
  public Button beep_setting_button;

  private Timeline connectionTimeline = new Timeline();

  private ViewLoads.ViewPane<ConnectionController> connectionUI = null;
  private ViewLoads.ViewPane<OptionController> optionUI = null;
  private ViewLoads.ViewPane<SettingController> settingUI = null;

  private Stage connectionStage = null;
  private Stage optionStage = null;
  private Stage settingStage = null;

  public ScheduledExecutorService scheduledExecutorService;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public void setConnectionUI(ViewLoads.ViewPane<ConnectionController> connectionUI) {
    this.connectionUI = connectionUI;
    this.connectionUI.getController().setMessageCallback(this::notify);
    connectionStage = new Stage(StageStyle.DECORATED);
    connectionStage.setScene(new Scene(connectionUI.getParent()));
    connectionStage.setTitle("连接设备");
  }

  public void setOptionUI(ViewLoads.ViewPane<OptionController> optionUI) {
    this.optionUI = optionUI;
    optionStage = new Stage(StageStyle.DECORATED);
    optionStage.setScene(new Scene(optionUI.getParent()));
    optionStage.setTitle("标签操作");
  }

  public void setSettingUI(ViewLoads.ViewPane<SettingController> settingUI) {
    this.settingUI = settingUI;
    settingStage = new Stage(StageStyle.DECORATED);
    settingStage.setScene(new Scene(settingUI.getParent()));
    settingStage.setTitle("设备配置");
  }


  private ICommand readerClient;
  private IReaderConnection futureConnection;

  private BooleanProperty isDisConnection = new SimpleBooleanProperty(true);

  private AtomicInteger tagNum = new AtomicInteger(1);
  private AtomicInteger tagCount = new AtomicInteger(0);
  private ObservableList<TagMapper> tagMappers =  FXCollections.observableArrayList();

  public void initialize() {
    scheduledExecutorService = Executors.newScheduledThreadPool(2);

    // 添加功率参数
    for (int index = 5; index < 31; index++) {
      read_power_combox.getItems().add(index);
      write_power_combox.getItems().add(index);
    }

    // 绑定网络状态
    bindEnabled();
    bindAction();
    bindTable();
  }

  /** 设置监听 */
  public void addListenter(IReaderConnection connection) {
    tag_table.setItems(tagMappers);

    connection.setConnectEventIListenter(this::notifyConnectionState);
    connection.setErrorEventIListenter(errorEvent -> this.notify(errorEvent.getMsg(), "red"));
    // 添加标签数据
    connection.setTagListenter(tag -> ThreadExcutorUntils.submit(() -> {
      // 累计标签数量
      tagCount.addAndGet(1);

      // 获取当前时间
      String format = dateFormat.format(new Date());
      // 添加标签信息
      Optional<TagMapper> first = tagMappers.stream().filter(_tag -> StringUtils.equals(_tag.getEpc(), tag.getEpc())).findFirst();
      if (first.isPresent()) {
        TagMapper value = first.get();
        value.setAnt(tag.getAnt());
        value.setPc(tag.getPc());
        value.setRssi(tag.getRssi());

        value.setTime(format);
        value.setCount(value.getCount() + 1);
      } else {
        // 计算序号
        tagNum.addAndGet(1);
        TagMapper mapper = new TagMapper();
        mapper.setRssi(tag.getRssi());
        mapper.setAnt(tag.getAnt());
        mapper.setPc(tag.getPc());
        mapper.setEpc(tag.getEpc());

        mapper.setCount(1);
        mapper.setNum(tagNum.get());
        mapper.setTime(format);

        synchronized (tagMappers) {
          tagMappers.add(mapper);
        }
      }
    }));
  }

  private void bindTable() {
    num_column.setCellValueFactory(new PropertyValueFactory<>("num"));
    ant_column.setCellValueFactory(new PropertyValueFactory<>("ant"));
    epc_column.setCellValueFactory(new PropertyValueFactory<>("epc"));
    pc_column.setCellValueFactory(new PropertyValueFactory<>("pc"));
    count_column.setCellValueFactory(new PropertyValueFactory<>("count"));
    time_column.setCellValueFactory(new PropertyValueFactory<>("time"));
  }

  private void bindAction() {
    /** 开始询读 */
    start_button.setOnAction(event -> {
      if (futureConnection == null) {
        return;
      }
      if (StringUtils.equals("启动循环读取标签", start_tooltip.getText())) {
        futureConnection.start();
        // 每秒计算一次速度
        scheduledExecutorService.scheduleAtFixedRate(() -> tagCount.getAndSet(0), 1, 1, TimeUnit.SECONDS);
        start_tooltip.setText("停止循环读取标签");
      } else {
        futureConnection.stop();
        start_tooltip.setText("启动循环读取标签");
      }
    });
    // 断开连接
    break_button.setOnAction(event -> {
      if (futureConnection != null) {
        futureConnection.disconnect();
      }
    });
    // 连接设备
    connect_menu.setOnAction(event -> {
      connectionUI.getController().setAddListenterCallback(readconnection -> {
        this.futureConnection = readconnection;
        addListenter(readconnection);
        return null;
      });
      connectionUI.getController().setConnectionCallback(() -> {
        this.futureConnection = connectionUI.getController().connectionAtomicReference.get();
        this.readerClient = connectionUI.getController().commandAtomicReference.get();
        isDisConnection.setValue(false);

        Platform.runLater(() -> connectionStage.hide());
      });
      connectionStage.showAndWait();
    });
  }

  /** 按钮启用属性绑定 */
  private void bindEnabled() {
    isDisConnection.addListener((observable, oldValue, newValue) -> connect_menu.disableProperty().setValue(!newValue));
    ant_setting_button.disableProperty().bind(isDisConnection);
    ant_getting_button.disableProperty().bind(isDisConnection);
    beep_setting_button.disableProperty().bind(isDisConnection);
    break_button.disableProperty().bind(isDisConnection);
    clear_button.disableProperty().bind(isDisConnection);
    power_get_button.disableProperty().bind(isDisConnection);
    power_setting_button.disableProperty().bind(isDisConnection);
    single_button.disableProperty().bind(isDisConnection);
    sreach_button.disableProperty().bind(isDisConnection);
    tagdata_button.disableProperty().bind(isDisConnection);
    start_button.disableProperty().bind(isDisConnection);
    tagdata_button.disableProperty().bind(isDisConnection);
  }

  /** 通知消息处理 */
  @Override
  public void notify(String message, String color) {
    Platform.runLater(() -> {
      setting_message_label.setText(message);
      setting_message_label.setTextFill(Color.web(color));
    });
  }

  /** 网络状态处理 */
  public void notifyConnectionState(ConnectEvent connectEvent) {
    Platform.runLater(() -> {
      switch (connectEvent) {
        case CONNECTION:
          isDisConnection.setValue(true);

          connectionTimeline.setAutoReverse(true);
          connectionTimeline.setCycleCount(-1);
          connection_state_label.setText("正在连接中...");
          connection_state_label.setTextFill(Color.web("green"));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(connection_state_label.opacityProperty(), 0.1F)));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(new Duration(500), new KeyValue(connection_state_label.opacityProperty(), 1.0F)));
          break;
        case CONNECTED:
          isDisConnection.setValue(false);

          connectionTimeline.setAutoReverse(true);
          connectionTimeline.setCycleCount(-1);
          connection_state_label.setText("设备已就绪");
          connection_state_label.setTextFill(Color.web("green"));
          connectionTimeline.stop();
          break;
        case DISCONNECTION:
          isDisConnection.setValue(true);

          connectionTimeline.setAutoReverse(true);
          connectionTimeline.setCycleCount(-1);
          connection_state_label.setText("正在断开连接...");
          connection_state_label.setTextFill(Color.web("red"));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(connection_state_label.opacityProperty(), 0.1F)));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(new Duration(500), new KeyValue(connection_state_label.opacityProperty(), 1.0F)));
          break;
        case DISCONNECTED:
          isDisConnection.setValue(true);

          connectionTimeline.setAutoReverse(true);
          connectionTimeline.setCycleCount(-1);
          connection_state_label.setText("设备未就绪");
          connection_state_label.setTextFill(Color.web("red"));
          connectionTimeline.stop();

          if (futureConnection != null) {
            futureConnection.disconnect();
          }
          if (connectionUI != null) {
            scheduledExecutorService.shutdown();
          }
          break;
        case HEART_BEAT:
          String oldValue = connection_state_label.getText();
          Paint oldTextPaint = connection_state_label.getTextFill();
          connectionTimeline.setAutoReverse(true);
          connectionTimeline.setCycleCount(4);
          connection_state_label.setText("设备正在运行中...");
          connection_state_label.setTextFill(Color.web("green"));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(connection_state_label.opacityProperty(), 0.1F)));
          connectionTimeline.getKeyFrames().addAll(new KeyFrame(new Duration(2000), new KeyValue(connection_state_label.opacityProperty(), 1.0F)));
          connectionTimeline.setOnFinished(event -> {
            connection_state_label.setText(oldValue);
            connection_state_label.setTextFill(oldTextPaint);
          });
          break;
      }
    });
  }
}
