package com.ridko.sk4;

import com.ridko.sk4.common.Dialogs;
import com.ridko.sk4.common.ViewLoads;
import com.ridko.sk4.controller.ConnectionController;
import com.ridko.sk4.controller.MainController;
import com.ridko.sk4.controller.OptionController;
import com.ridko.sk4.controller.SettingController;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ErrorEvent;
import com.ridko.sk4.listenter.IListenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * SK4测试应用
 *
 * @author smitea
 * @since 2018-11-02
 */
public class SK4Application extends Application {

  private final URL MAIN_PATH = this.getClass().getResource("/fxml/main.fxml");
  private final URL CONNECTION_PATH = this.getClass().getResource("/fxml/connection.fxml");
  private final URL OPTION_PATH = this.getClass().getResource("/fxml/option.fxml");
  private final URL SETTIN_PATH = this.getClass().getResource("/fxml/setting.fxml");

  public static double INIT_WIDTH = 900;
  public static double INIT_HEIGHT = 600;

  private ICommand readerClient;
  private IReaderConnection futureConnection;

  private ViewLoads.ViewPane<MainController> mainUI = null;
  private ViewLoads.ViewPane<ConnectionController> connectionUI = null;
  private ViewLoads.ViewPane<OptionController> optionUI = null;
  private ViewLoads.ViewPane<SettingController> settingUI = null;

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      mainUI = ViewLoads.load(MAIN_PATH);
      connectionUI = ViewLoads.load(CONNECTION_PATH);
      optionUI = ViewLoads.load(OPTION_PATH);
      settingUI = ViewLoads.load(SETTIN_PATH);

      createMain(primaryStage);
    } catch (IOException e) {
      e.fillInStackTrace();
      Dialogs.alertError("系统错误", "系统文件缺失");
    }
  }

  private void createMain(Stage primaryStage) {
    primaryStage.setScene(new Scene(mainUI.getParent(), INIT_WIDTH, INIT_HEIGHT));
    primaryStage.setMinHeight(600);
    primaryStage.setMinWidth(800);
    primaryStage.show();

    MainController controller = mainUI.getController();
    controller.setConnectionUI(connectionUI);
    controller.setOptionUI(optionUI);
    controller.setSettingUI(settingUI);

    primaryStage.setOnCloseRequest(event -> {
      if (futureConnection != null) {
        futureConnection.disconnect();
      }
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
