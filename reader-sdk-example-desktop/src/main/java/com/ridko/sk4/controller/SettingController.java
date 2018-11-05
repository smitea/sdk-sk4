package com.ridko.sk4.controller;

import com.ridko.sk4.AbstactMessageCallback;
import com.ridko.sk4.ICommand;
import com.ridko.sk4.entity.*;
import com.ridko.sk4.promise.Callback;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 设置对话框控制器
 *
 * @author smitea
 * @since 2018-11-03
 */
public class SettingController extends AbstactMessageCallback {
  public CheckBox gpio_1_checkbox;
  public CheckBox gpio_2_checkbox;
  public CheckBox gpio_6_checkbox;
  public CheckBox gpio_4_checkbox;
  public CheckBox gpio_5_checkbox;
  public CheckBox gpio_3_checkbox;
  public CheckBox gpio_7_checkbox;
  public CheckBox gpio_8_checkbox;
  public Button gpio_input_get_button;
  public Button gpio_ouput_set_button;
  public Button gpio_output_button;

  public TextArea freq_data_text;
  public Button freq_set_button;
  public Button freq_get_button;

  public ComboBox<Gen2.Q> gen_q_combox;
  public TextField gen_startq_text;
  public TextField gen_minq_text;
  public TextField gen_maxq_text;
  public ComboBox<Gen2.Select> gen_select_combox;
  public ComboBox<Gen2.Session> gen_session_combox;
  public ComboBox<Gen2.Target> gen_target_combox;
  public Button gen_set_button;
  public Button gen_get_button;


  public CheckBox region_save_checkbox;
  public ComboBox<FrequencyRegion> region_combox;
  public Button region_set_button;
  public Button region_get_button;

  public Label info_temperature_label;
  public Label info_firmware_label;
  public Label info_hardware_label;
  public Button info_get_button;


  public TextField cyclic_param1_text;
  public TextField cyclic_param2_text;
  public Button cyclic_get_button;
  public Button cyclic_set_button;

  public TextField ant_work_ant1_text;
  public TextField ant_work_ant2_text;
  public TextField ant_work_ant3_text;
  public TextField ant_work_ant4_text;
  public TextField ant_wait_time_text;
  public Button ant_work_set_button;
  public Button ant_work_get_button;

  public CheckBox fast_param_checkbox;
  public Button fast_set_button;
  public Button fast_get_button;

  public ComboBox<BaudRate> baute_combox;
  public Button baute_set_button;

  public CheckBox auto_read_checkbox;
  public Button auto_read_set_button;

  public CheckBox tagfocus_checked;
  public Button fagfocus_set_button;
  public Button fagfocus_get_button;

  public CheckBox beep_enabled_checkbox;
  public TextField beep_time_text;
  public Button beep_set_button;

  public ComboBox<WorkMode> workd_mode_combox;
  public Button work_mode_set_button;
  public Button work_mode_get_button;

  public TextField eas_bit_text;
  public TextField eas_value_text;
  public Button eas_set_button;
  public Button eas_get_button;

  public TextField heartbeat_param;
  public Button heartbeat_set_button;
  public Button heartbeat_get_button;


  public Button reset_wifi_button;

  public TextField branch_work_i_param;
  public Button branch_work_i_set_button;

  public TextField relay_work_param_text;
  public Button relay_work_set_button;
  public Button relay_work_get_button;


  public TextField trigger_work_param;
  public Button trigger_work_set_button;

  public Button restart_button;

  public Label serial_num_label;
  public Button serial_num_get_button;

  public TitledPane titled_pane_2;
  public TitledPane titled_pane_1;

  private ICommand command;

  public void initialize() {
    initCombox();
    initPane();
    bindAction();
  }

  public void setCommand(ICommand command) {
    this.command = command;
  }

  private void initPane() {
    titled_pane_1.expandedProperty().setValue(true);
    titled_pane_2.expandedProperty().setValue(true);
  }

  private void initCombox() {
    gen_q_combox.getItems().addAll(Gen2.Q.values());
    gen_q_combox.getSelectionModel().selectFirst();
    gen_select_combox.getItems().addAll(Gen2.Select.values());
    gen_select_combox.getSelectionModel().selectFirst();
    gen_session_combox.getItems().addAll(Gen2.Session.values());
    gen_session_combox.getSelectionModel().selectFirst();
    gen_target_combox.getItems().addAll(Gen2.Target.values());
    gen_target_combox.getSelectionModel().selectFirst();

    region_combox.getItems().addAll(FrequencyRegion.values());
    region_combox.getSelectionModel().selectFirst();
    workd_mode_combox.getItems().addAll(WorkMode.values());
    workd_mode_combox.getSelectionModel().selectFirst();
    baute_combox.getItems().addAll(BaudRate.values());
    baute_combox.getSelectionModel().selectFirst();
  }

  private void bindAction() {
    CheckBox[] gpios = new CheckBox[]{gpio_1_checkbox, gpio_2_checkbox, gpio_3_checkbox, gpio_4_checkbox, gpio_5_checkbox, gpio_6_checkbox, gpio_7_checkbox, gpio_8_checkbox};

    // 获取 GPIO 输入
    gpio_input_get_button.setOnAction(event -> command.getInputGpio(1, 2, 3, 4, 5, 6, 7, 8).then(new Callback<Gpios>() {
      @Override
      public void onSuccess(Gpios value) {
        Platform.runLater(() -> {
          for (Gpios.Gpio gpio : value.gpios()) {
            if (gpio.getIndex() > 0 && gpio.getIndex() <= gpios.length) {
              gpios[gpio.getIndex() - 1].setSelected(gpio.isHight());
            }
            SettingController.this.notify("获取 GPIO 输入 成功", "green");
          }
        });
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("获取 GPIO 输入 失败", "red");
      }
    }));

    // 获取 GPIO 输出
    gpio_output_button.setOnAction(event -> command.getGpio().then(new Callback<Gpios>() {
      @Override
      public void onSuccess(Gpios value) {
        Platform.runLater(() -> {
          for (Gpios.Gpio gpio : value.gpios()) {
            if (gpio.getIndex() > 0 && gpio.getIndex() <= gpios.length) {
              gpios[gpio.getIndex() - 1].setSelected(gpio.isHight());
            }
            SettingController.this.notify("获取 GPIO 输出 成功", "green");
          }
        });
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("获取 GPIO 输出 失败", "red");
      }
    }));
    // 设置 GPIO 输出
    gpio_ouput_set_button.setOnAction(event -> {
      Gpios _gpios = new Gpios();
      for (int index = 0; index < gpios.length; index++) {
        _gpios.add(index + 1, gpios[index].isSelected());
      }

      command.setGpio(_gpios).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("设置 GPIO 输出 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("设置 GPIO 输出 失败", "red");
        }
      });
    });

    // 获取读写器跳频频点
    freq_get_button.setOnAction(event -> command.getOutputFrequency().then(new Callback<List<Integer>>() {
      @Override
      public void onSuccess(List<Integer> value) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Integer frequency : value) {
          stringBuffer.append(frequency).append("\n");
        }
        Platform.runLater(() -> freq_data_text.setText(stringBuffer.toString()));
        SettingController.this.notify("获取 射频频率状态 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("获取 射频频率状态 失败", "red");
      }
    }));
    // 设置射频跳频频段
    freq_set_button.setOnAction(event -> {
      String data = freq_data_text.getText();
      String[] datas = data.split("\n");
      int[] _datas = new int[datas.length];
      for (int index = 0; index < datas.length; index++) {
        _datas[index] = NumberUtils.createInteger(datas[index]);
      }
      command.setOutputFrequency(_datas).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("设置 射频频率状态 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("设置 射频频率状态 失败", "red");
        }
      });
    });

    // Q算法 获取
    gen_get_button.setOnAction(event -> command.getGen2().then(new Callback<Gen2>() {
      @Override
      public void onSuccess(Gen2 value) {
        Platform.runLater(() -> {
          gen_startq_text.setText(String.format("%d", value.getStartQ()));
          gen_minq_text.setText(String.format("%d", value.getMinQ()));
          gen_maxq_text.setText(String.format("%d", value.getMaxQ()));

          gen_q_combox.getSelectionModel().select(value.getqValue());
          gen_select_combox.getSelectionModel().select(value.getSelect());
          gen_session_combox.getSelectionModel().select(value.getSession());
          gen_target_combox.getSelectionModel().select(value.getTarget());
        });
        SettingController.this.notify("Q算法 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("Q算法 获取 失败", "red");
      }
    }));
    // Q算法 设置
    gen_set_button.setOnAction(event -> {
      Gen2 gen2 = new Gen2();
      gen2.setStartQ(NumberUtils.createInteger(gen_startq_text.getText()));
      gen2.setMinQ(NumberUtils.createInteger(gen_minq_text.getText()));
      gen2.setMaxQ(NumberUtils.createInteger(gen_maxq_text.getText()));

      gen2.setqValue(gen_q_combox.getValue());
      gen2.setSelect(gen_select_combox.getValue());
      gen2.setSession(gen_session_combox.getValue());
      gen2.setTarget(gen_target_combox.getValue());
      command.setGen2(gen2).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("Q算法 设置 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("Q算法 设置 失败", "red");
        }
      });
    });

    // 设备温度/版本信息
    info_get_button.setOnAction(event -> {
      command.getTemperature().then(new Callback<Integer>() {
        @Override
        public void onSuccess(Integer value) {
          Platform.runLater(() -> info_temperature_label.setText(value.toString()));
          SettingController.this.notify("设备温度 获取 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("设备温度 获取 失败", "red");
        }
      });

      command.getFirmwareVersion().then(new Callback<String>() {
        @Override
        public void onSuccess(String value) {
          Platform.runLater(() -> info_firmware_label.setText(value));
          SettingController.this.notify("设备Firmware版本 获取 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("设备Firmware版本 获取 失败", "red");
        }
      });

      command.getHardwareVersion().then(new Callback<String>() {
        @Override
        public void onSuccess(String value) {
          Platform.runLater(() -> info_hardware_label.setText(value));
          SettingController.this.notify("设备Hardware版本 获取 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("设备Hardware版本 获取 失败", "red");
        }
      });
    });

    // 天线循环工作时间和间隔时间 设置
    ant_work_set_button.setOnAction(event -> command.setAntWorkAndWaitTime(
            NumberUtils.createInteger(ant_work_ant1_text.getText()),
            NumberUtils.createInteger(ant_work_ant2_text.getText()),
            NumberUtils.createInteger(ant_work_ant3_text.getText()),
            NumberUtils.createInteger(ant_work_ant4_text.getText()),
            NumberUtils.createInteger(ant_wait_time_text.getText())
    ).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("天线循环工作时间和间隔时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("天线循环工作时间和间隔时间 设置 失败", "red");
      }
    }));
    // 天线循环工作时间和间隔时间 获取
    ant_work_get_button.setOnAction(event -> command.getAntWorkAndWaitTime().then(new Callback<AntWorkAndWaitTime>() {
      @Override
      public void onSuccess(AntWorkAndWaitTime value) {
        Platform.runLater(() -> {
          ant_work_ant1_text.setText(String.format("%d", value.getAnt1WorkTime()));
          ant_work_ant2_text.setText(String.format("%d", value.getAnt2WorkTime()));
          ant_work_ant3_text.setText(String.format("%d", value.getAnt3WorkTime()));
          ant_work_ant4_text.setText(String.format("%d", value.getAnt4WorkTime()));
          ant_wait_time_text.setText(String.format("%d", value.getWaitTime()));
        });
        SettingController.this.notify("天线循环工作时间和间隔时间 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("天线循环工作时间和间隔时间 获取 失败", "red");
      }
    }));

    // FastID功能 设置
    fast_set_button.setOnAction(event -> {
      command.setFastID(fast_param_checkbox.isSelected()).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("FastID功能 设置 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("FastID功能 设置 失败", "red");
        }
      });
    });
    // FastID功能 获取
    fast_get_button.setOnAction(event -> {
      command.getFastID().then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          Platform.runLater(() -> fast_param_checkbox.setSelected(value));
          SettingController.this.notify("FastID功能 获取 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("FastID功能 获取 失败", "red");
        }
      });
    });

    // 循环查询标签工作时间及间断时间 设置
    cyclic_set_button.setOnAction(event -> command.setCyclicQueryWorkAndResponseTime(
            NumberUtils.createInteger(cyclic_param2_text.getText()),
            NumberUtils.createInteger(cyclic_param1_text.getText())
    ).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("循环查询标签工作时间及间断时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("循环查询标签工作时间及间断时间 设置 失败", "red");
      }
    }));
    // 循环查询标签工作时间及间断时间 获取
    cyclic_get_button.setOnAction(event -> command.getCyclicQueryWorkAndResponseTime().then(new Callback<CyclicQueryWorkAndResponseTime>() {
      @Override
      public void onSuccess(CyclicQueryWorkAndResponseTime value) {
        Platform.runLater(() -> {
          cyclic_param2_text.setText(String.format("%d", value.getWorkTime()));
          cyclic_param1_text.setText(String.format("%d", value.getInterruptedTime()));
        });
        SettingController.this.notify("循环查询标签工作时间及间断时间 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("循环查询标签工作时间及间断时间 获取 失败", "red");
      }
    }));

    // 模块通讯波特率 设置
    baute_set_button.setOnAction(event -> {
      command.setBaudRate(baute_combox.getValue()).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("模块通讯波特率 设置 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("模块通讯波特率 设置 失败", "red");
        }
      });
    });
    // 开机自动读取标志 设置
    auto_read_set_button.setOnAction(event -> command.setAutoReadWhenPowerOff(auto_read_checkbox.isSelected()).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("开机自动读取标志 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("开机自动读取标志 设置 失败", "red");
      }
    }));

    // TAGFOCUS 参数 设置
    fagfocus_set_button.setOnAction(event -> {
      command.setTagFocus(tagfocus_checked.isSelected()).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("TAGFOCUS 参数 设置 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("TAGFOCUS 参数 设置 失败", "red");
        }
      });
    });
    // TAGFOCUS 参数 获取
    fagfocus_get_button.setOnAction(event -> command.getTagFocus().then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        Platform.runLater(() -> tagfocus_checked.setSelected(value));
        SettingController.this.notify("TAGFOCUS 参数 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("TAGFOCUS 参数 获取 失败", "red");
      }
    }));

    // 报警间隔时间 设置
    beep_set_button.setOnAction(event -> command.setReaderAlarmIntervalTime(NumberUtils.createInteger(beep_time_text.getText())).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("报警间隔时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("报警间隔时间 设置 失败", "red");
      }
    }));

    // 工作模式 设置
    work_mode_set_button.setOnAction(event -> command.setWorkMode(workd_mode_combox.getValue()).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("工作模式 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("工作模式 设置 成功", "red");
      }
    }));
    // 工作模式 获取
    work_mode_get_button.setOnAction(event -> command.getWorkMode().then(new Callback<WorkMode>() {
      @Override
      public void onSuccess(WorkMode value) {
        Platform.runLater(() -> workd_mode_combox.getSelectionModel().select(value));
        SettingController.this.notify("工作模式 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("工作模式 获取 成功", "red");
      }
    }));

    // EAS 参数 设置
    eas_set_button.setOnAction(event -> command.setEASParam(
            NumberUtils.createInteger(eas_bit_text.getText()),
            NumberUtils.createInteger(eas_value_text.getText())).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("EAS 参数 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("EAS 参数 设置 失败", "red");
      }
    }));
    // EAS 参数 获取
    eas_get_button.setOnAction(event -> command.getEASParam().then(new Callback<EAS>() {
      @Override
      public void onSuccess(EAS value) {
        Platform.runLater(() -> {
          eas_bit_text.setText(String.format("%d", value.getBit()));
          eas_value_text.setText(String.format("%d", value.getValue()));
        });
        SettingController.this.notify("EAS 参数 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("EAS 参数 获取 失败", "red");
      }
    }));

    // 重置读写器 WIFI 模块
    reset_wifi_button.setOnAction(event -> command.restWifi().then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("重置读写器 WIFI 模块 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("重置读写器 WIFI 模块 失败", "red");
      }
    }));

    // 分支器间隔时间 设置
    branch_work_i_set_button.setOnAction(event -> command.setBranchWorkIntervalTime(NumberUtils.createInteger(branch_work_i_param.getText())).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("分支器间隔时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("分支器间隔时间 设置 失败", "red");
      }
    }));

    // 通道模式继电器工作时间 设置
    relay_work_set_button.setOnAction(event -> command.setRelayWorkTimeNew(NumberUtils.createInteger(relay_work_param_text.getText())).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("通道模式继电器工作时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("通道模式继电器工作时间 设置 失败", "red");
      }
    }));
    // 通道模式继电器工作时间 获取
    relay_work_get_button.setOnAction(event -> command.getRelayWorkTimeNew().then(new Callback<Integer>() {
      @Override
      public void onSuccess(Integer value) {
        Platform.runLater(() -> relay_work_param_text.setText(String.format("%s", value)));
        SettingController.this.notify("通道模式继电器工作时间 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("通道模式继电器工作时间 获取 失败", "red");
      }
    }));

    // 读写器触发工作时间 设置
    trigger_work_set_button.setOnAction(event -> command.setReaderTriggerWorkTime(NumberUtils.createInteger(trigger_work_param.getText())).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("读写器触发工作时间 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("读写器触发工作时间 设置 失败", "red");
      }
    }));

    // 模块重新上电
    restart_button.setOnAction(event -> command.restart().then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("模块重新上电 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("模块重新上电 失败", "red");
      }
    }));

    // 设备序列号
    serial_num_get_button.setOnAction(event -> command.getSerialNum().then(new Callback<String>() {
      @Override
      public void onSuccess(String value) {
        Platform.runLater(() -> serial_num_label.setText(value));
        SettingController.this.notify("获取 设备序列号 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("获取 设备序列号 失败", "red");
      }
    }));

    // 频率区域 获取
    region_get_button.setOnAction(event -> command.getFrequencyRegion().then(new Callback<FrequencyRegion>() {
      @Override
      public void onSuccess(FrequencyRegion value) {
        Platform.runLater(() -> region_combox.getSelectionModel().select(value));
        SettingController.this.notify("频率区域 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("频率区域 获取 失败", "red");
      }
    }));
    // 频率区域 设置
    region_set_button.setOnAction(event -> command.setFrequencyRegion(region_save_checkbox.isSelected(), region_combox.getValue()).then(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean value) {
        SettingController.this.notify("频率区域 设置 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("频率区域 设置 失败", "red");
      }
    }));

    // 心跳包参数 设置
    heartbeat_set_button.setOnAction(event -> {
      command.setHeartbeatParam(NumberUtils.createInteger(heartbeat_param.getText())).then(new Callback<Boolean>() {
        @Override
        public void onSuccess(Boolean value) {
          SettingController.this.notify("心跳包参数 设置 成功", "green");
        }

        @Override
        public void onFailure(Throwable value) {
          SettingController.this.notify("心跳包参数 设置 失败", "red");
        }
      });
    });
    // 心跳包参数 获取
    heartbeat_get_button.setOnAction(event -> command.getHeartbeatParam().then(new Callback<Integer>() {
      @Override
      public void onSuccess(Integer value) {
        Platform.runLater(() -> heartbeat_param.setText(String.format("%d", value)));
        SettingController.this.notify("心跳包参数 获取 成功", "green");
      }

      @Override
      public void onFailure(Throwable value) {
        SettingController.this.notify("心跳包参数 获取 成功", "red");
      }
    }));
  }
}


