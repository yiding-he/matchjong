package com.github.yidinghe.matchjong.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yidinghe.matchjong.FxApp;
import com.github.yidinghe.matchjong.editor.EditEvent;
import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;
import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameTilePack;
import com.github.yidinghe.matchjong.play.PlayWindow;
import com.github.yidinghe.matchjong.util.EventBus;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainController {

  public static final String TILES_COUNT_PREFIX = "块数：";

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static final String INNER_PACK = "关卡自带";

  static {
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public HBox root;

  private GameStage gameStage;

  private GameEditorBoard gameEditorBoard;

  private Spinner<Integer> spMatchCount;

  private Spinner<Integer> spBufferSize;

  private ListView<EditorBoardLayer> lvLayers = new ListView<>();

  private Label lblTilesCount;

  private ListView<GameTilePack> lvTilePacks = new ListView<>();

  public void initialize() {
    var cols = 40;
    var rows = 25;

    initGameStage(cols, rows);
    initEditorBoard(cols, rows);
    initDefaultTilePack();

    var gameEditorGround = new ScrollPane(gameEditorBoard);
    HBox.setHgrow(gameEditorGround, Priority.ALWAYS);

    this.root.setBackground(new Background(new BackgroundFill(Color.web("#EEEEEE"), null, null)));
    this.root.getChildren().addAll(createControlPane(), gameEditorGround);

    try {
      loadGameStage(new String(MainController.class.getResourceAsStream("/sample.json").readAllBytes()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initDefaultTilePack() {
    try {
      var path = Path.of("tiles/default.zip");
      if (Files.exists(path)) {
        lvTilePacks.getItems().add(readTilePack(path));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private GameTilePack readTilePack(Path path) throws IOException {
    var p = new GameTilePack();
    p.setName(path.getFileName().toString());

    try (var z = new ZipFile(path.toFile())) {
      Enumeration<? extends ZipEntry> entries = z.entries();
      Predicate<String> isImage = f ->
        f.toLowerCase().endsWith(".png") || f.toLowerCase().endsWith(".jpg") || f.toLowerCase().endsWith(".gif");

      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        if (!zipEntry.isDirectory() && isImage.test(zipEntry.getName())) {
          p.getTileImages().add(Base64.getEncoder().encodeToString(
            z.getInputStream(zipEntry).readAllBytes()
          ));
        }
      }
    }

    return p;
  }

  private void initGameStage(int cols, int rows) {
    this.gameStage = new GameStage(cols, rows);
    this.gameStage.setMatchCount(4);
    this.gameStage.setBufferSize(13);

    EventBus.on(EditEvent.AddTileEvent.class, e -> {
      var tile = e.tile();
      if (!this.gameStage.isReadOnly()) {
        this.gameStage.getLayer(tile.getLayer()).addTile(tile.getColIndex(), tile.getRowIndex());
      }
      this.lblTilesCount.setText(TILES_COUNT_PREFIX + gameStage.tilesCount());
    });

    EventBus.on(EditEvent.DeleteTileEvent.class, e -> {
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
    var btnAddLayer = new Button("添加Layer");
    var btnDeleteLayer = new Button("删除Layer");
    lblTilesCount = new Label(TILES_COUNT_PREFIX + "0");
    var hbButtons = new HBox(10, btnAddLayer, btnDeleteLayer, lblTilesCount);
    var btnPlay = new Button("开始玩");
    spMatchCount = new Spinner<>(2, 10, 3);
    spBufferSize = new Spinner<>(2, 100, 9);
    var hbMatchCount = new HBox(5, new Label("消除个数："), spMatchCount);
    var hbBufferSize = new HBox(5, new Label("缓冲区域大小："), spBufferSize);
    var lblTilePacks = new Label("图标包：");
    setLvCellFactory(lvTilePacks, gameTilePack -> gameTilePack.getName() + "(" + gameTilePack.getTileImages().size() + " tiles)");
    var btnAddTilePack = new Button("+");
    var btnDelTilePack = new Button("-");
    var hbTilePackButtons = new HBox(5, btnAddTilePack, btnDelTilePack);
    var vbTilePacks = new VBox(3, lblTilePacks, lvTilePacks, hbTilePackButtons);
    var btnSave = new Button("保存关卡");
    var btnLoad = new Button("加载关卡");
    var hbSaveLoad = new HBox(5, btnSave, btnLoad, btnPlay);
    var lbTip = new Label("鼠标在棋盘中左键点击放置麻将块，右键点击删除");
    lbTip.setTextFill(Color.web("999999"));

    var vBox = new VBox(10, hbButtons, lbTip, lvLayers, hbMatchCount, hbBufferSize, vbTilePacks, hbSaveLoad);

    spMatchCount.setPrefWidth(70);
    spBufferSize.setPrefWidth(70);
    hbMatchCount.setAlignment(Pos.BASELINE_LEFT);
    hbBufferSize.setAlignment(Pos.BASELINE_LEFT);

    hbButtons.setAlignment(Pos.BASELINE_LEFT);
    btnAddLayer.setOnAction(e -> doAddLayer());
    btnDeleteLayer.setOnAction(e -> doDeleteLayer());

    lvTilePacks.setPrefHeight(100);

    hbSaveLoad.setAlignment(Pos.BASELINE_LEFT);
    btnSave.setOnAction(e -> saveGameStage());
    btnLoad.setOnAction(e -> loadGameStage());

    initLvLayers();

    btnPlay.setOnAction(e -> play());

    return vBox;
  }

  private void loadGameStage() {
    var fileChooser = initFileChooser();
    var file = fileChooser.showOpenDialog(FxApp.primaryWindow);
    if (file == null) {
      return;
    }
    try {
      var json = Files.readString(file.toPath());
      loadGameStage(json);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void loadGameStage(String json) throws IOException {
    gameStage = OBJECT_MAPPER.reader().readValue(json, GameStage.class);
    gameStage.setReadOnly(true);
    gameEditorBoard.loadGameStage(gameStage);
    gameStage.setReadOnly(false);

    lvLayers.getItems().setAll(gameEditorBoard.getBoardLayers());
    Collections.reverse(lvLayers.getItems());
    lvLayers.getSelectionModel().select(0);

    spMatchCount.getValueFactory().setValue(gameStage.getMatchCount());
    spBufferSize.getValueFactory().setValue(gameStage.getBufferSize());

    var tilePack = gameStage.getTilePack();
    tilePack.setName(INNER_PACK);
    var tilePacks = lvTilePacks.getItems();
    if (!tilePacks.isEmpty() && tilePacks.get(0).getName().equals(INNER_PACK)) {
      tilePacks.remove(0);
    }
    tilePacks.add(0, tilePack);
    lvTilePacks.getSelectionModel().select(0);
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

  private void initLvLayers() {
    setLvCellFactory(lvLayers, item -> "Layer " + item.getLayer());
    lvLayers.setPrefHeight(240);
    lvLayers.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) -> lvLayers.getItems().forEach(boardLayer -> boardLayer.setActive(boardLayer == newValue))
    );
  }

  private static <T> void setLvCellFactory(ListView<T> lv, Function<T, String> toString) {
    lv.setCellFactory(__lv -> new ListCell<>() {
      @Override
      protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
        } else {
          setText(toString.apply(item));
        }
      }
    });
  }

  private void doDeleteLayer() {
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

  private void doAddLayer() {
    if (this.gameEditorBoard.getBoardLayers().size() >= 50) {
      return;
    }
    var boardLayer = this.gameEditorBoard.addLayer(this.gameStage.getStageLayers().size());
    this.gameStage.addLayer(boardLayer.getLayer());

    var prevSelected = lvLayers.getSelectionModel().getSelectedItem();
    lvLayers.getItems().add(0, boardLayer);
    lvLayers.getSelectionModel().select(null);
    lvLayers.getSelectionModel().select(Objects.requireNonNullElse(prevSelected, boardLayer));
  }

  private void play() {
    prepareGameStage();

    if (!validateGameStage()) {
      return;
    }

    PlayWindow playWindow = new PlayWindow(gameStage);
    playWindow.start();
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

    if (lvTilePacks.getSelectionModel().getSelectedItem() != null) {
      gameStage.setTilePack(lvTilePacks.getSelectionModel().getSelectedItem());
    }
  }
}
