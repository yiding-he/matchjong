package com.github.yidinghe.matchjong.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yidinghe.matchjong.FxApp;
import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;
import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.play.PlayWindow;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.nio.file.Files;

public class MainController {

  public static final String TILES_COUNT_PREFIX = "块数：";

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public HBox root;

  private GameStage gameStage;

  private GameEditorBoard editorBoard;

  private Spinner<Integer> spMatchCount;

  private Spinner<Integer> spBufferSize;

  public void initialize() {
    var cols = 40;
    var rows = 30;

    this.gameStage = new GameStage(cols, rows);
    this.gameStage.setMatchCount(4);
    this.gameStage.setBufferSize(13);

    this.editorBoard = new GameEditorBoard(cols, rows);
    this.editorBoard.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), null, null)));

    this.root.setBackground(new Background(new BackgroundFill(Color.web("#EEEEEE"), null, null)));
    this.root.getChildren().addAll(
      createLayerList(),
      editorBoard
    );
  }

  private VBox createLayerList() {
    var lvLayers = new ListView<EditorBoardLayer>();
    var btnAddLayer = new Button("添加Layer");
    var btnDeleteLayer = new Button("删除Layer");
    var lblTilesCount = new Label(TILES_COUNT_PREFIX + "0");
    var hbButtons = new HBox(10, btnAddLayer, btnDeleteLayer, lblTilesCount);
    var btnPlay = new Button("开始玩");
    spMatchCount = new Spinner<>(2, 10, 3);
    spBufferSize = new Spinner<>(2, 100, 9);
    var hbMatchCount = new HBox(5, new Label("消除个数："), spMatchCount);
    var hbBufferSize = new HBox(5, new Label("缓冲区域大小："), spBufferSize);
    var btnSave = new Button("保存关卡");
    var btnLoad = new Button("加载关卡");
    var hbSaveLoad = new HBox(5, btnSave, btnLoad);

    var vBox = new VBox(10, hbButtons, lvLayers, hbMatchCount, hbBufferSize, btnPlay, hbSaveLoad);

    spMatchCount.setPrefWidth(70);
    spBufferSize.setPrefWidth(70);
    hbMatchCount.setAlignment(Pos.BASELINE_LEFT);
    hbBufferSize.setAlignment(Pos.BASELINE_LEFT);

    hbButtons.setAlignment(Pos.BASELINE_LEFT);
    btnAddLayer.setOnAction(e -> doAddLayer(lvLayers, lblTilesCount));
    btnDeleteLayer.setOnAction(e -> doDeleteLayer(lvLayers, lblTilesCount));

    hbSaveLoad.setAlignment(Pos.BASELINE_LEFT);
    btnSave.setOnAction(e -> {
      prepareGameStage();
      if (!validateGameStage()) {
        return;
      }
      var fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
      var file = fileChooser.showSaveDialog(FxApp.primaryWindow);
      if (file != null) {
        try {
          Files.writeString(file.toPath(), OBJECT_MAPPER.writeValueAsString(gameStage));
          new Alert(Alert.AlertType.INFORMATION, "保存完毕", ButtonType.OK).showAndWait();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    });

    initLvLayers(lvLayers);

    btnPlay.setOnAction(e -> play());

    lvLayers.setPrefHeight(120);

    return vBox;
  }

  private static void initLvLayers(ListView<EditorBoardLayer> lvLayers) {
    lvLayers.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(EditorBoardLayer item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
        } else {
          setText("Layer " + item.getLayer());
        }
      }
    });

    lvLayers.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) -> lvLayers.getItems().forEach(boardLayer -> boardLayer.setActive(boardLayer == newValue))
    );
  }

  private void doDeleteLayer(ListView<EditorBoardLayer> lvLayers, Label lblTilesCount) {
    var boardLayer = lvLayers.getSelectionModel().getSelectedItem();
    if (boardLayer == null) {
      return;
    }

    var layerNum = boardLayer.getLayer();
    this.gameStage.removeLayer(layerNum);
    this.editorBoard.removeLayer(layerNum);
    lvLayers.getItems().remove(boardLayer);
    lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
  }

  private void doAddLayer(ListView<EditorBoardLayer> lvLayers, Label lblTilesCount) {
    if (this.editorBoard.getBoardLayers().size() >= 5) {
      return;
    }
    var boardLayer = this.editorBoard.addLayer(this.gameStage.getStageLayers().size());
    boardLayer.setOnAddTile(event -> {
      gameStage.getLayer(boardLayer.getLayer()).addTile(event.colIndex(), event.rowIndex());
      lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
    });
    boardLayer.setOnDeleteTile(event -> {
      gameStage.getLayer(boardLayer.getLayer()).deleteTile(event.colIndex(), event.rowIndex());
      lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
    });
    this.gameStage.addLayer(boardLayer.getLayer());
    lvLayers.getItems().add(0, boardLayer);
    if (lvLayers.getItems().size() == 1) {
      lvLayers.getSelectionModel().select(0);
    }
  }

  private void play() {
    prepareGameStage();

    if (!validateGameStage()) {
      return;
    }

    PlayWindow playWindow = new PlayWindow();
    playWindow.start(this.gameStage);
  }

  private boolean validateGameStage() {
    var valid = true;
    var validateResult = gameStage.validate();
    if (validateResult != null) {
      new Alert(Alert.AlertType.ERROR, validateResult, ButtonType.OK).showAndWait();
      valid = false;
    }
    return valid;
  }

  private void prepareGameStage() {
    gameStage.setMatchCount(spMatchCount.getValue());
    gameStage.setBufferSize(spBufferSize.getValue());
  }
}
