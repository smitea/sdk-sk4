package com.ridko.sk4.controller;

import com.ridko.sk4.AbstactMessageCallback;
import com.ridko.sk4.ICommand;
import com.ridko.sk4.common.HexTools;
import com.ridko.sk4.entity.*;
import com.ridko.sk4.promise.Callback;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.apache.commons.lang3.math.NumberUtils;

import static com.ridko.sk4.entity.QtOperation.READ;
import static com.ridko.sk4.entity.QtOperation.WRITE;

/**
 * 标签操作
 * @author smitea
 * @since 2018-11-03
 */
public class OptionController extends AbstactMessageCallback {
    public TextField filter_data_text;
    public TextField fliter_tag_password_text;

    public ComboBox<BankNo> tag_bank_no_combox;
    public TextField tag_start_adr_text;
    public TextField tag_data_length_text;
    public ComboBox<FMB> tag_filter_type_combox;
    public Button tag_read_button;
    public Button tag_write_button;
    public TextArea tag_write_data_text;

    public CheckBox qt_control_enabled_checkbox;
    public CheckBox qt_public_memory_map_checkbox;
    public Button qt_setting_button;
    public Button qt_get_button;
    public Button qt_op_read_button;
    public Button qt_op_write_button;
    public CheckBox lock_killl_pwd_checkbox;
    public CheckBox lock_access_pwd_checkbox;
    public CheckBox lock_epc_checkbox;
    public CheckBox lock_tid_checkbox;
    public CheckBox lock_user_checkbox;
    public Button lock_button;
    public ComboBox<LockType> lock_type_combox;

    public TextField kill_text;
    public Button kill_button;

    private ICommand command;

    public void initialize() {
        initCombox();
        initAction();
    }

    public void setCommand(ICommand command) {
        this.command = command;
    }

    public void setFilterData(String epc){
        this.filter_data_text.setText(epc);
        this.tag_filter_type_combox.getSelectionModel().selectFirst();
    }

    private void initCombox() {
        tag_bank_no_combox.getItems().addAll(BankNo.values());
        tag_bank_no_combox.getSelectionModel().selectFirst();
        tag_filter_type_combox.getItems().addAll(FMB.values());
        tag_filter_type_combox.getSelectionModel().selectFirst();
        lock_type_combox.getItems().addAll(LockType.values());
        lock_type_combox.getSelectionModel().selectFirst();
    }

    private void initAction() {
        // 读取标签数据
        tag_read_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();
            BankNo bankNo = tag_bank_no_combox.getValue();
            String startAdr = tag_start_adr_text.getText();
            String dataLen = tag_data_length_text.getText();

            command.readTagData(
                    password,
                    fmb,
                    HexTools.hexStr2Byte(filterData),
                    bankNo,
                    NumberUtils.createInteger(startAdr),
                    NumberUtils.createInteger(dataLen))
                    .then(new Callback<TagData>() {
                        @Override
                        public void onSuccess(TagData value) {
                            Platform.runLater(() -> {
                                tag_write_data_text.setText(HexTools.byteArrayToHexString(value.getData()));
                                OptionController.this.notify("读取标签数据成功", "green");
                            });
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            OptionController.this.notify("读取标签数据失败", "red");
                        }
                    });
        });
        // 写入标签数据
        tag_write_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();
            BankNo bankNo = tag_bank_no_combox.getValue();
            String startAdr = tag_start_adr_text.getText();
            String dataLen = tag_data_length_text.getText();
            String data = tag_write_data_text.getText();

            command.writeTagData(
                    password,
                    fmb,
                    HexTools.hexStr2Byte(filterData),
                    bankNo,
                    NumberUtils.createInteger(startAdr),
                    NumberUtils.createInteger(dataLen),
                    HexTools.hexStr2Byte(data))
                    .then(new Callback<TagData>() {
                        @Override
                        public void onSuccess(TagData value) {
                            Platform.runLater(() -> {
                                tag_write_data_text.setText(HexTools.byteArrayToHexString(value.getData()));
                                OptionController.this.notify("写入标签数据成功", "green");
                            });
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            OptionController.this.notify("写入标签数据失败", "red");
                        }
                    });
        });

        // 获取Qt参数
        qt_get_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();
            command.getQtParam(password, fmb, HexTools.hexStr2Byte(filterData))
                    .then(new Callback<QtParam>() {
                        @Override
                        public void onSuccess(QtParam value) {
                            Platform.runLater(() -> {
                                qt_control_enabled_checkbox.setSelected(value.isCloseControl());
                                qt_public_memory_map_checkbox.setSelected(value.isEnabledPublicMemoryMap());
                            });
                            OptionController.this.notify("获取Qt参数成功", "green");
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            OptionController.this.notify("获取Qt参数失败", "red");
                        }
                    });
        });
        // 设置Qt参数
        qt_setting_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();

            boolean isPMM = qt_control_enabled_checkbox.isSelected();
            boolean isControl = qt_public_memory_map_checkbox.isSelected();
            command.setQtParam(
                    password,
                    fmb,
                    HexTools.hexStr2Byte(filterData),
                    isControl,
                    isPMM)
                    .then(new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean value) {
                            OptionController.this.notify("设置Qt参数成功", "green");
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            OptionController.this.notify("设置Qt参数失败", "red");
                        }
                    });
        });

        // Qt读操作
        qt_op_read_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();

            BankNo bankNo = tag_bank_no_combox.getValue();

            boolean isPMM = qt_control_enabled_checkbox.isSelected();
            boolean isControl = qt_public_memory_map_checkbox.isSelected();
            command.setQtOperation(
                    password,
                    fmb,
                    HexTools.hexStr2Byte(filterData),
                    READ,
                    isControl,
                    isPMM,
                    false,
                    bankNo,
                    0x00,
                    0x00,
                    null).then(new Callback<QtOperation>() {
                @Override
                public void onSuccess(QtOperation value) {
                    switch (value) {
                        case READ:
                            Platform.runLater(() -> {
                                byte[] data = value.getData();
                                tag_write_data_text.setText(HexTools.byteArrayToHexString(data));
                            });
                            break;
                    }
                    OptionController.this.notify("Qt读操作成功", "green");
                }

                @Override
                public void onFailure(Throwable value) {
                    OptionController.this.notify("Qt读操作失败", "red");
                }
            });
        });
        // Qt写操作
        qt_op_write_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();

            BankNo bankNo = tag_bank_no_combox.getValue();
            String startAdr = tag_start_adr_text.getText();
            String dataLen = tag_data_length_text.getText();
            String data = tag_write_data_text.getText();

            boolean isPMM = qt_control_enabled_checkbox.isSelected();
            boolean isControl = qt_public_memory_map_checkbox.isSelected();
            command.setQtOperation(
                    password,
                    fmb,
                    HexTools.hexStr2Byte(filterData),
                    WRITE,
                    isControl,
                    isPMM,
                    false,
                    bankNo,
                    NumberUtils.createInteger(startAdr),
                    NumberUtils.createInteger(dataLen),
                    HexTools.hexStr2Byte(data)).then(new Callback<QtOperation>() {
                @Override
                public void onSuccess(QtOperation value) {
                    OptionController.this.notify("Qt写操作成功", "green");
                }

                @Override
                public void onFailure(Throwable value) {
                    OptionController.this.notify("Qt写操作失败", "red");
                }
            });
        });

        // 标签锁定
        lock_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();
            LockType value = lock_type_combox.getValue();

            LockParam lockParam = new LockParam();
            lockParam.setEPC(lock_epc_checkbox.isSelected());
            lockParam.setUser(lock_user_checkbox.isSelected());
            lockParam.setAccessPwd(lock_access_pwd_checkbox.isSelected());
            lockParam.setKillPwd(lock_killl_pwd_checkbox.isSelected());
            lockParam.setTID(lock_tid_checkbox.isSelected());

            lockParam.setLockType(value);

            command.lockTag(password, fmb, HexTools.hexStr2Byte(filterData), lockParam).then(new Callback<TagData>() {
                @Override
                public void onSuccess(TagData value) {
                    Platform.runLater(() -> {
                        byte[] data = value.getData();
                        tag_write_data_text.setText(HexTools.byteArrayToHexString(data));
                    });
                    OptionController.this.notify("锁定标签成功","green");
                }

                @Override
                public void onFailure(Throwable value) {
                    OptionController.this.notify("锁定标签失败","red");
                }
            });
        });

        // 杀死标签
        kill_button.setOnAction(event -> {
            String password = fliter_tag_password_text.getText();
            String filterData = filter_data_text.getText();
            FMB fmb = tag_filter_type_combox.getValue();

            command.killTag(password,fmb,HexTools.hexStr2Byte(filterData)).then(new Callback<TagData>() {
                @Override
                public void onSuccess(TagData value) {
                    Platform.runLater(() -> {
                        byte[] data = value.getData();
                        tag_write_data_text.setText(HexTools.byteArrayToHexString(data));
                    });
                    OptionController.this.notify("杀死标签成功","green");
                }

                @Override
                public void onFailure(Throwable value) {
                    OptionController.this.notify("杀死标签失败","red");
                }
            });
        });

    }
}
