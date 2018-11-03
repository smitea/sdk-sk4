package com.ridko.sk4.common;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Dialogs {
    /**
     * 弹出错误对话框
     *
     * @param title   标题
     * @param message 错误信息
     */
    public static void alertError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }
}
