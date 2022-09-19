package com.github.yidinghe.matchjong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FxApp extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(FxApp.class.getResource("main.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Matchjong Stage Editor");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    TileImages.load("tiles");
    launch();
  }
}
