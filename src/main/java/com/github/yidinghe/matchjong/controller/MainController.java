package com.github.yidinghe.matchjong.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yidinghe.matchjong.FxApp;
import com.github.yidinghe.matchjong.TileImages;
import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;
import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.events.AddTileEvent;
import com.github.yidinghe.matchjong.editor.events.DeleteTileEvent;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameTileImage;
import com.github.yidinghe.matchjong.play.PlayWindow;
import com.github.yidinghe.matchjong.util.EventBus;
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
import java.util.Collections;
import java.util.Objects;

public class MainController {

  public static final String TILES_COUNT_PREFIX = "块数：";

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public HBox root;

  private GameStage gameStage;

  private GameEditorBoard gameEditorBoard;

  private Spinner<Integer> spMatchCount;

  private Spinner<Integer> spBufferSize;

  private ListView<EditorBoardLayer> lvLayers;

  private Label lblTilesCount;

  public void initialize() {
    var cols = 40;
    var rows = 30;

    initGameStage(cols, rows);
    initEditorBoard(cols, rows);

    this.root.setBackground(new Background(new BackgroundFill(Color.web("#EEEEEE"), null, null)));
    this.root.getChildren().addAll(createControlPane(), gameEditorBoard);
  }

  private void initGameStage(int cols, int rows) {
    this.gameStage = new GameStage(cols, rows);
    this.gameStage.setMatchCount(4);
    this.gameStage.setBufferSize(13);

    EventBus.on(AddTileEvent.class, e -> {
      var tile = e.tile();
      if (!this.gameStage.isReadOnly()) {
        this.gameStage.getLayer(tile.getLayer()).addTile(tile.getColIndex(), tile.getRowIndex());
      }
      this.lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
    });

    EventBus.on(DeleteTileEvent.class, e -> {
      var tile = e.tile();
      if (!this.gameStage.isReadOnly()) {
        this.gameStage.getLayer(tile.getLayer()).deleteTile(tile.getColIndex(), tile.getRowIndex());
      }
      this.lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
    });
  }

  private void initEditorBoard(int cols, int rows) {
    this.gameEditorBoard = new GameEditorBoard(cols, rows);
    this.gameEditorBoard.setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"), null, null)));
  }

  private VBox createControlPane() {
    lvLayers = new ListView<>();
    var btnAddLayer = new Button("添加Layer");
    var btnDeleteLayer = new Button("删除Layer");
    lblTilesCount = new Label(TILES_COUNT_PREFIX + "0");
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
    btnAddLayer.setOnAction(e -> doAddLayer(lvLayers));
    btnDeleteLayer.setOnAction(e -> doDeleteLayer(lvLayers));

    hbSaveLoad.setAlignment(Pos.BASELINE_LEFT);
    btnSave.setOnAction(e -> saveGameStage());
    btnLoad.setOnAction(e -> loadGameStage());

    initLvLayers(lvLayers);

    btnPlay.setOnAction(e -> play());

    lvLayers.setPrefHeight(120);

    return vBox;
  }

  private void loadGameStage() {
    var fileChooser = initFileChooser();
    var file = fileChooser.showOpenDialog(FxApp.primaryWindow);
    if (file == null) {
      return;
    }
    try {
      gameStage = OBJECT_MAPPER.reader()
        .readValue(Files.readString(file.toPath()), GameStage.class);

      gameStage.setReadOnly(true);
      gameEditorBoard.loadGameStage(gameStage);
      gameStage.setReadOnly(false);

      lvLayers.getItems().setAll(gameEditorBoard.getBoardLayers());
      Collections.reverse(lvLayers.getItems());
      lvLayers.getSelectionModel().select(0);

      spMatchCount.getValueFactory().setValue(gameStage.getMatchCount());
      spBufferSize.getValueFactory().setValue(gameStage.getBufferSize());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void saveGameStage() {
    prepareGameStage();
    if (!validateGameStage()) {
      return;
    }
    var fileChooser = initFileChooser();
    var file = fileChooser.showSaveDialog(FxApp.primaryWindow);
    if (file != null) {
      try {
        Files.writeString(file.toPath(), OBJECT_MAPPER.writeValueAsString(gameStage));
        new Alert(Alert.AlertType.INFORMATION, "保存完毕", ButtonType.OK).showAndWait();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  private static FileChooser initFileChooser() {
    var fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file", "*.json"));
    return fileChooser;
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

  private void doDeleteLayer(ListView<EditorBoardLayer> lvLayers) {
    var boardLayer = lvLayers.getSelectionModel().getSelectedItem();
    if (boardLayer == null) {
      return;
    }

    var layerNum = boardLayer.getLayer();
    this.gameStage.removeLayer(layerNum);
    this.gameEditorBoard.removeLayer(layerNum);
    lvLayers.getItems().remove(boardLayer);
    lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
  }

  private void doAddLayer(ListView<EditorBoardLayer> lvLayers) {
    if (this.gameEditorBoard.getBoardLayers().size() >= 5) {
      return;
    }
    var boardLayer = this.gameEditorBoard.addLayer(this.gameStage.getStageLayers().size());
    this.gameStage.addLayer(boardLayer.getLayer());

    var prevSelected = lvLayers.getSelectionModel().getSelectedItem();
    lvLayers.getItems().add(0, boardLayer);
    lvLayers.getSelectionModel().select(Objects.requireNonNullElse(prevSelected, boardLayer));
    lvLayers.getSelectionModel().getSelectedItem().setActive(true);
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

    gameStage.getTileImages().clear();
    gameStage.getTileImages().addAll(TileImages.getTileImages().stream().map(GameTileImage::rawDataBase64).toList());
  }
}
