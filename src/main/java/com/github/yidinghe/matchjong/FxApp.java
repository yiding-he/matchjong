package com.github.yidinghe.matchjong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class FxApp extends Application {

  public static Window primaryWindow;

  @Override
  public void start(Stage stage) throws IOException {
    primaryWindow = stage;

    FXMLLoader fxmlLoader = new FXMLLoader(FxApp.class.getResource("main.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("三消麻将关卡编辑器");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
