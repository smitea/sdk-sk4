package com.ridko.sk4.controller;

import com.ridko.sk4.AbstactMessageCallback;
import com.ridko.sk4.ICommand;
import com.ridko.sk4.entity.BaudRate;
import com.ridko.sk4.entity.FrequencyRegion;
import com.ridko.sk4.entity.Gen2;
import com.ridko.sk4.entity.WorkMode;
import javafx.scene.control.*;

/**
 * 设置对话框控制器
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
    }

    public void setCommand(ICommand command) {
        this.command = command;
    }

    private void initPane(){
        titled_pane_1.setExpanded(true);
        titled_pane_2.setExpanded(true);
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
}


