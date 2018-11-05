package com.ridko.sk4.controller;

import com.ridko.sk4.ICommand;
import com.ridko.sk4.IFutureConnection;
import com.ridko.sk4.IMessageCallback;
import com.ridko.sk4.IReaderConnection;
import com.ridko.sk4.common.ThreadExcutorUntils;
import com.ridko.sk4.common.ViewLoads;
import com.ridko.sk4.domain.TagMapper;
import com.ridko.sk4.entity.Ants;
import com.ridko.sk4.entity.Tag;
import com.ridko.sk4.entity.TxPower;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ITagListenter;
import com.ridko.sk4.promise.Callback;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 主窗口控制器
 *
 * @author smitea
 * @since 2018-11-03
 */
public class MainController implements IMessageCallback {
    /**
     * 根目录
     */
    public BorderPane root_panel;

    /**
     * 连接状态 Label
     */
    public Label connection_state_label;
    /**
     * 操作状态 Label
     */
    public Label setting_message_label;
    /**
     * 当前时间 Label
     */
    public Label time_label;
    /**
     * 版本信息 Label
     */
    public Label version_label;

    /**
     * 连接按钮
     */
    public Button connect_menu;
    /**
     * 配置按钮
     */
    public Button setting_btn;

    /**
     * 循环查询标签按钮
     */
    public Button start_button;
    /**
     * 单次查询按钮
     */
    public Button single_button;
    /**
     * 查询标签数据按钮
     */
    public Button tagdata_button;
    /**
     * 清楚列表按钮
     */
    public Button clear_button;

    /**
     * EPC 筛选输入框
     */
    public TextField epc_edit;
    /**
     * 筛选按钮
     */
    public Button sreach_button;

    /**
     * 标签列表
     */
    public TableView<TagMapper> tag_table;
    /**
     * 序号列
     */
    public TableColumn<TagMapper, Integer> num_column;
    /**
     * 天线列
     */
    public TableColumn<TagMapper, Integer> ant_column;
    /**
     * EPC列
     */
    public TableColumn<TagMapper, String> epc_column;
    /**
     * PC 列
     */
    public TableColumn<TagMapper, Integer> pc_column;
    /**
     * 计数列
     */
    public TableColumn<TagMapper, Integer> count_column;
    /**
     * 时间列
     */
    public TableColumn<TagMapper, String> time_column;
    public TableColumn<TagMapper, Double> rssi_column;

    /**
     * 循环查询标签时间
     */
    public Label tag_time_label;
    /**
     * 循环查询标签总计
     */
    public Label tag_count_label;
    /**
     * 循环查询标签速率
     */
    public Label tag_rate_label;

    /**
     * 天线1选择框
     */
    public CheckBox ant1_checkbox;
    /**
     * 天线2选择框
     */
    public CheckBox ant4_checkbox;
    /**
     * 天线3选择框
     */
    public CheckBox ant3_checkbox;
    /**
     * 天线4选择框
     */
    public CheckBox ant2_checkbox;
    /**
     * 设置天线按钮
     */
    public Button ant_setting_button;
    /**
     * 获取天线按钮
     */
    public Button ant_getting_button;

    /**
     * 读功率选择框
     */
    public TextField read_power_text;
    /**
     * 写功率选择框
     */
    public TextField write_power_text;
    /**
     * 获取读功率按钮
     */
    public Button power_get_button;
    /**
     * 设置写功率按钮
     */
    public Button power_setting_button;

    /**
     * 设置蜂鸣器按钮
     */
    public Button beep_setting_button;
    public CheckBox beep_checkbox;

    /**
     * 连接动画时间对象
     */
    private Timeline connectionTimeline = new Timeline();

    /**
     * 连接对话框资源
     */
    private ViewLoads.ViewPane<ConnectionController> connectionUI = null;
    /**
     * 选项对话框资源
     */
    private ViewLoads.ViewPane<OptionController> optionUI = null;
    /**
     * 设置对话框资源
     */
    private ViewLoads.ViewPane<SettingController> settingUI = null;

    /**
     * 连接对话框
     */
    private Stage connectionStage = null;
    /**
     * 选项对话框
     */
    private Stage optionStage = null;
    /**
     * 设置对话框
     */
    private Stage settingStage = null;

    /**
     * 主对话框
     */
    private Stage mainStage = null;

    /**
     * 定时任务线程池
     */
    private Timer timer;

    /**
     * 时间刷新定时器
     */
    private Timer dateUpdateTimer;

    /**
     * 时间转换器
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setConnectionUI(ViewLoads.ViewPane<ConnectionController> connectionUI) {
        // 创建连接对话框
        this.connectionUI = connectionUI;
        this.connectionUI.getController().setMessageCallback(this);
        connectionStage = new Stage(StageStyle.DECORATED);
        connectionStage.initOwner(mainStage);
        connectionStage.setScene(new Scene(connectionUI.getParent()));
        connectionStage.setTitle("连接设备");
    }

    public void setOptionUI(ViewLoads.ViewPane<OptionController> optionUI) {
        // 创建选项对话框
        this.optionUI = optionUI;
        this.optionUI.getController().setMessageCallback(this);
        optionStage = new Stage(StageStyle.DECORATED);
        optionStage.setScene(new Scene(optionUI.getParent()));
        connectionStage.initOwner(mainStage);
        optionStage.setTitle("标签操作");
    }

    public void setSettingUI(ViewLoads.ViewPane<SettingController> settingUI) {
        // 创建设置对话框
        this.settingUI = settingUI;
        this.settingUI.getController().setMessageCallback(this);
        connectionStage.initOwner(mainStage);
        settingStage = new Stage(StageStyle.DECORATED);
        settingStage.setScene(new Scene(settingUI.getParent()));
        settingStage.setTitle("设备配置");
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /**
     * 设备操作接口
     */
    private ICommand readerClient;
    /**
     * 设备连接对象
     */
    private IReaderConnection futureConnection;

    /**
     * 控制按钮 启用/禁用
     */
    private BooleanProperty isDisConnection = new SimpleBooleanProperty(true);

    /**
     * 标签序累加器
     */
    private IntegerProperty tagNum = new SimpleIntegerProperty(0);
    /**
     * 标签计数累加器
     */
    private IntegerProperty tagCount = new SimpleIntegerProperty(0);
    /**
     * 循环查询标签时间
     */
    private IntegerProperty tagTime = new SimpleIntegerProperty(0);

    /**
     * 标签信息列表
     */
    private final ObservableList<TagMapper> tagMappers = FXCollections.observableArrayList();

    /**
     * 标签信息回调监听器
     */
    private final ITagListenter tagListenter = tag -> ThreadExcutorUntils.submit(() -> Platform.runLater(() -> {
        // 累计标签数量
        tagCount.setValue(tagCount.getValue()+1);

        // 获取当前时间
        String format = dateFormat.format(new Date());

        // 判断EPC是否存在
        Optional<TagMapper> first = tagMappers
                .stream()
                .filter(_tag -> StringUtils.equals(_tag.getEpc(), tag.getEpc()))
                .findFirst();

        if (first.isPresent()) {
            // 修改EPC信息
            TagMapper value = first.get();
            value.setAnt(tag.getAnt());
            value.setPc(tag.getPc());
            value.setRssi(tag.getRssi());

            value.setTime(format);
            value.setCount(value.getCount() + 1);
        } else {
            // 计算序号
            tagNum.setValue(tagNum.get() + 1);

            // 添加EPC信息
            TagMapper mapper = new TagMapper();
            mapper.setRssi(tag.getRssi());
            mapper.setAnt(tag.getAnt());
            mapper.setPc(tag.getPc());
            mapper.setEpc(tag.getEpc());

            mapper.setCount(1);
            mapper.setNum(tagNum.get());
            mapper.setTime(format);

            tagMappers.add(mapper);
        }
    }));

    public void initialize() {

        // 创建定时任务线程池
        timer = new Timer(1000, (actionEvent) -> Platform.runLater(() -> {
            tag_rate_label.setText(String.format("%d",tagCount.getValue()));

            // 标签总数置零
            tagCount.setValue(0);
            // 时间累加
            tagTime.setValue(tagTime.get() + 1);
        }));

        // 时间更新定时器
        dateUpdateTimer = new Timer(1000, event -> {
            Platform.runLater(() -> time_label.textProperty().setValue(dateFormat.format(new Date())));
        });
        dateUpdateTimer.start();


        // 绑定按钮 启用/禁用 状态
        bindEnabled();
        // 绑定按钮事件
        bindAction();
        // 绑定列表数据
        bindTable();
        // 绑定标签统计信息
        bindTagCount();
    }

    /**
     * 设置监听
     */
    private void addListenter(IReaderConnection connection) {
        //设置连接状态监听器
        connection.setConnectEventIListenter(this::notifyConnectionState);
        // 设置设备错误监听器
        connection.setErrorEventIListenter(errorEvent -> this.notify(errorEvent.getMsg(), "red"));

        // 添加标签数据
        connection.setTagListenter(tagListenter);
    }

    private void bindTable() {
        num_column.setCellValueFactory(new PropertyValueFactory<>("num"));
        ant_column.setCellValueFactory(new PropertyValueFactory<>("ant"));
        epc_column.setCellValueFactory(new PropertyValueFactory<>("epc"));
        rssi_column.setCellValueFactory(new PropertyValueFactory<>("rssi"));
        pc_column.setCellValueFactory(new PropertyValueFactory<>("pc"));
        count_column.setCellValueFactory(new PropertyValueFactory<>("count"));
        time_column.setCellValueFactory(new PropertyValueFactory<>("time"));
        tag_table.setItems(tagMappers);

        // 表格选中事件监听
        tag_table.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                Platform.runLater(()->{
                    TagMapper selectedItem = tag_table.getSelectionModel().getSelectedItem();
                    if(selectedItem!=null) {
                        // 弹出标签信息框
                        futureConnection.stop();
                        timer.stop();

                        optionUI.getController().setFilterData(selectedItem.getEpc());
                        optionStage.setOnCloseRequest(event1 -> resumeScan());
                        optionStage.showAndWait();
                    }
                });
            }
        });
    }

    private void bindAction() {
        /** 开始扫描 */
        start_button.setOnAction(event -> {
            if (futureConnection == null) {
                return;
            }
            if (StringUtils.equals("开始扫描", start_button.getText())) {
                clearData();
                futureConnection.start();
                timer.start();
                futureConnection.setTagListenter(tagListenter);
                start_button.setText("停止扫描");
            } else {
                futureConnection.stop();
                timer.stop();
                start_button.setText("开始扫描");
            }
        });
        // 连接设备
        connect_menu.setOnAction(event -> {
            if (StringUtils.equals("断开设备", connect_menu.getText())) {
                if (futureConnection != null) {
                    futureConnection.disconnect();
                }
            } else {
                connectionUI.getController().setAddListenterCallback(readConnection -> {
                    this.futureConnection = readConnection;
                    // 添加监听器
                    addListenter(readConnection);
                    return null;
                });
                connectionUI.getController().setConnectionCallback(() -> {
                    // 保存对象
                    this.futureConnection = connectionUI.getController().connectionAtomicReference.get();
                    this.readerClient = connectionUI.getController().commandAtomicReference.get();
                    // 启用按钮
                    isDisConnection.setValue(false);

                    // 关闭连接窗口
                    Platform.runLater(() -> connectionStage.hide());
                });
                connectionStage.showAndWait();
            }
        });
        // 设备配置
        setting_btn.setOnAction(event -> {
            futureConnection.stop();
            timer.stop();
            settingStage.setOnCloseRequest(event1 -> {
                resumeScan();
            });
            settingStage.showAndWait();
        });
        // 标签设置
        tagdata_button.setOnAction(event -> {
            futureConnection.stop();
            timer.stop();
            optionStage.setOnCloseRequest(event1 -> resumeScan());
            optionStage.showAndWait();
        });

        // 单次扫描
        single_button.setOnAction(event -> {
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");
            clearData();
            if (readerClient != null) {
                readerClient.singleRead().then(new Callback<Tag>() {
                    @Override
                    public void onSuccess(Tag tag) {
                        TagMapper mapper = new TagMapper();
                        mapper.setRssi(tag.getRssi());
                        mapper.setAnt(tag.getAnt());
                        mapper.setPc(tag.getPc());
                        mapper.setEpc(tag.getEpc());

                        mapper.setCount(1);
                        mapper.setNum(tagNum.get());
                        String format = dateFormat.format(new Date());
                        mapper.setTime(format);
                        synchronized (tagMappers) {
                            tagMappers.clear();
                            tagMappers.add(mapper);
                        }
                        MainController.this.notify("单次扫描执行成功", "green");
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        MainController.this.notify("单次扫描执行失败", "red");
                    }
                });
            }
        });
        // 过滤EPC码
        sreach_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            List<TagMapper> mappers = tagMappers.stream()
                    .filter(tagMapper -> StringUtils.contains(tagMapper.getEpc(), epc_edit.getText()))
                    .collect(Collectors.toList());
            tagMappers.addAll(mappers);
            resumeScan();
        });
        // 清除数据
        clear_button.setOnAction(event -> {
            tagCount.setValue(0);
            tagTime.setValue(0);
            tagNum.setValue(0);

            synchronized (tagMappers) {
                tagMappers.clear();
            }
        });

        // 获取天线
        ant_getting_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            readerClient.getAnts().then(new Callback<Ants>() {
                @Override
                public void onSuccess(Ants value) {
                    Platform.runLater(() -> {
                        for (Ants.Ant ant : value.getAnts()) {
                            if (ant.isOn() && ant.getIndex() == 1) {
                                ant1_checkbox.setSelected(true);
                            } else if (ant.isOn() && ant.getIndex() == 2) {
                                ant2_checkbox.setSelected(true);
                            } else if (ant.isOn() && ant.getIndex() == 3) {
                                ant3_checkbox.setSelected(true);
                            } else if (ant.isOn() && ant.getIndex() == 4) {
                                ant4_checkbox.setSelected(true);
                            }
                        }
                        MainController.this.notify("获取天线成功", "green");
                    });
                }

                @Override
                public void onFailure(Throwable value) {
                    MainController.this.notify("获取天线失败", "red");
                }
            });
        });
        // 设置天线
        ant_setting_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            Ants ants = new Ants();
            ants.add(1, ant1_checkbox.isSelected())
                    .add(2, ant2_checkbox.isSelected())
                    .add(3, ant3_checkbox.isSelected())
                    .add(4, ant4_checkbox.isSelected());

            readerClient.setAnts(ants).then(new Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    MainController.this.notify("天线设置成功", "green");
                }

                @Override
                public void onFailure(Throwable value) {
                    MainController.this.notify("天线设置失败", "red");
                }
            });
        });

        // 获取功率
        power_get_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            readerClient.getTxPower().then(new Callback<TxPower>() {
                @Override
                public void onSuccess(TxPower value) {
                    Platform.runLater(() -> {
                        read_power_text.setText(String.format("%d", value.getReadPower()));
                        write_power_text.setText(String.format("%d", value.getWritePower()));
                        MainController.this.notify("获取功率成功", "green");
                    });
                }

                @Override
                public void onFailure(Throwable value) {
                    MainController.this.notify("获取功率失败", "red");
                }
            });
        });
        // 设置功率
        power_setting_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            TxPower power = new TxPower();
            power.setReadPower(NumberUtils.createInteger(read_power_text.getText()));
            power.setWritePower(NumberUtils.createInteger(write_power_text.getText()));

            readerClient.setTxPower(power.getReadPower(), power.getWritePower(), power.isLoop()).then(new Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    MainController.this.notify("设置功率成功", "green");
                }

                @Override
                public void onFailure(Throwable value) {
                    value.fillInStackTrace();
                    MainController.this.notify("设置功率失败", "red");
                }
            });
        });

        // 设置蜂鸣器
        beep_setting_button.setOnAction(event -> {
            clearData();
            futureConnection.stop();
            timer.stop();
            start_button.setText("开始扫描");

            readerClient.setBeep(beep_checkbox.isSelected()).then(new Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    MainController.this.notify("设置蜂鸣器成功", "green");
                }

                @Override
                public void onFailure(Throwable value) {
                    value.fillInStackTrace();
                    MainController.this.notify("设置蜂鸣器失败", "red");
                }
            });
        });
    }

    /**
     * 按钮启用属性绑定
     */
    private void bindEnabled() {
        ant_setting_button.disableProperty().bind(isDisConnection);
        ant_getting_button.disableProperty().bind(isDisConnection);
        beep_setting_button.disableProperty().bind(isDisConnection);
        clear_button.disableProperty().bind(isDisConnection);
        power_get_button.disableProperty().bind(isDisConnection);
        power_setting_button.disableProperty().bind(isDisConnection);
        single_button.disableProperty().bind(isDisConnection);
        sreach_button.disableProperty().bind(isDisConnection);
        tagdata_button.disableProperty().bind(isDisConnection);
        start_button.disableProperty().bind(isDisConnection);
        tagdata_button.disableProperty().bind(isDisConnection);
        setting_btn.disableProperty().bind(isDisConnection);

        isDisConnection.addListener((observable, oldValue, newValue) -> {
            // 连接后，程序初始化处理
            Platform.runLater(() -> {
                if (!newValue && readerClient != null) {
                    optionUI.getController().setCommand(readerClient);
                    settingUI.getController().setCommand(readerClient);

                    power_get_button.fire();
                    ant_getting_button.fire();
                    readerClient.getFirmwareVersion().then(new Callback<String>() {
                        @Override
                        public void onSuccess(String value) {
                            Platform.runLater(()->version_label.setText(value));
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            MainController.this.notify("版本信息获取失败","red");
                        }
                    });
                }

                connect_menu.setText(newValue ? "连接设备" : "断开设备");
                start_button.setText(newValue ? "开始扫描" : start_button.getText());
                timer.stop();
            });
        });
    }

    /**
     * 绑定标签统计结果
     */
    private void bindTagCount() {
        StringConverter<Number> stringConverter = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return NumberUtils.createInteger(string);
            }
        };
        tag_count_label.textProperty().bindBidirectional(tagNum, stringConverter);
//        tag_rate_label.textProperty().bindBidirectional(tagCount, stringConverter);
        tag_time_label.textProperty().bindBidirectional(tagTime, stringConverter);
    }

    /**
     * 通知消息处理
     */
    @Override
    public void notify(String message, String color) {
        Platform.runLater(() -> {
            setting_message_label.setText(message);
            setting_message_label.setTextFill(Color.web(color));
        });
    }

    /**
     * 网络状态处理
     */
    private void notifyConnectionState(ConnectEvent connectEvent) {
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

    private void clearData() {
        tagCount.setValue(0);
        tagTime.setValue(0);
        tagNum.setValue(0);

        synchronized (tagMappers) {
            tagMappers.clear();
        }
        futureConnection.setTagListenter(null);
    }

    private void resumeScan() {
        if (StringUtils.equals("开始扫描", start_button.getText())) {
            futureConnection.stop();
            timer.stop();
            futureConnection.setTagListenter(null);
        } else {
            futureConnection.start();
            timer.start();
            futureConnection.setTagListenter(tagListenter);
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        timer.stop();
        dateUpdateTimer.stop();
        if (futureConnection != null) {
            futureConnection.disconnect();
        }
    }
}
