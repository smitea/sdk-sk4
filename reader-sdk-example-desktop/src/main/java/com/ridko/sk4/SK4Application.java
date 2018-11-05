package com.ridko.sk4;

import com.ridko.sk4.common.Dialogs;
import com.ridko.sk4.common.ViewLoads;
import com.ridko.sk4.controller.ConnectionController;
import com.ridko.sk4.controller.MainController;
import com.ridko.sk4.controller.OptionController;
import com.ridko.sk4.controller.SettingController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * RFID 读写器 SDK 接口 测试应用
 *
 * @author smitea
 * @since 2018-11-02
 */
public class SK4Application extends Application {

  private final URL MAIN_PATH = this.getClass().getResource("/fxml/main.fxml");
  private final URL CONNECTION_PATH = this.getClass().getResource("/fxml/connection.fxml");
  private final URL OPTION_PATH = this.getClass().getResource("/fxml/option.fxml");
  private final URL SETTING_PATH = this.getClass().getResource("/fxml/setting.fxml");

  private final static double INIT_WIDTH = 1000;
  private final static double INIT_HEIGHT = 600;

  private ViewLoads.ViewPane<MainController> mainUI = null;
  private ViewLoads.ViewPane<ConnectionController> connectionUI = null;
  private ViewLoads.ViewPane<OptionController> optionUI = null;
  private ViewLoads.ViewPane<SettingController> settingUI = null;

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      // 设置 指令 调试打印输出
      System.setProperty("sk4.debug", "true");

      // 加载驱动(动态库文件在调试时放置在项目根目录，打包发布后动态库文件放置在程序根目录)
      System.loadLibrary("rxtxParallel");
      System.loadLibrary("rxtxSerial");

      // 加载资源文件
      mainUI = ViewLoads.load(MAIN_PATH);
      connectionUI = ViewLoads.load(CONNECTION_PATH);
      optionUI = ViewLoads.load(OPTION_PATH);
      settingUI = ViewLoads.load(SETTING_PATH);

      // 创建主页面
      createMain(primaryStage);
    } catch (Exception e) {
      e.printStackTrace();
      Dialogs.alertError("系统错误", "系统文件缺失");
    }
  }

  private void createMain(Stage primaryStage) {
    // 创建主窗口
    primaryStage.setScene(new Scene(mainUI.getParent(), INIT_WIDTH, INIT_HEIGHT));
    primaryStage.setMinHeight(600);
    primaryStage.setMinWidth(1000);
    primaryStage.show();

    // 获取控制器
    MainController controller = mainUI.getController();
    controller.setMainStage(primaryStage);
    // 设置资源对象
    controller.setConnectionUI(connectionUI);
    controller.setOptionUI(optionUI);
    controller.setSettingUI(settingUI);

    // 程序关闭后，释放资源
    primaryStage.setOnCloseRequest(event -> {
      controller.release();
      System.exit(0);
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
