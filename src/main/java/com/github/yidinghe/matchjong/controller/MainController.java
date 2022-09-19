package com.github.yidinghe.matchjong.controller;

import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;
import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.play.PlayWindow;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

  public HBox root;

  private GameStage gameStage;

  private GameEditorBoard editorBoard;

  public void initialize() {
    var cols = 40;
    var rows = 30;

    this.gameStage = new GameStage(cols, rows);
    this.editorBoard = new GameEditorBoard(cols, rows);

    this.root.getChildren().addAll(
      createLayerList(),
      editorBoard
    );
  }

  private VBox createLayerList() {
    var lvLayers = new ListView<EditorBoardLayer>();
    var btnAddLayer = new Button("添加Layer");
    var btnDeleteLayer = new Button("删除Layer");
    var hbButtons = new HBox(10, btnAddLayer, btnDeleteLayer);
    var sampleTile = new Tile(-1, -1, -1, -1, EditorBoardLayer.TILE_IMAGE, Tile.BORDER_COLOR);
    var btnPlay = new Button("开始玩");

    var vBox = new VBox(10, hbButtons, lvLayers, sampleTile, btnPlay);

    hbButtons.setAlignment(Pos.BASELINE_LEFT);
    btnAddLayer.setOnAction(e -> {
      if (this.editorBoard.getBoardLayers().size() >= 5) {
        return;
      }
      var boardLayer = this.editorBoard.addLayer(this.gameStage.getStageLayers().size());
      boardLayer.setOnAddTile(event -> gameStage.getLayer(boardLayer.getLayer()).addTile(event.colIndex(), event.rowIndex()));
      boardLayer.setOnDeleteTile(event -> gameStage.getLayer(boardLayer.getLayer()).deleteTile(event.colIndex(), event.rowIndex()));
      this.gameStage.addLayer(boardLayer.getLayer());
      lvLayers.getItems().add(0, boardLayer);
    });

    btnDeleteLayer.setOnAction(e -> {
      var boardLayer = lvLayers.getSelectionModel().getSelectedItem();
      if (boardLayer == null) {
        return;
      }

      var layerNum = boardLayer.getLayer();
      this.gameStage.removeLayer(layerNum);
      this.editorBoard.removeLayer(layerNum);
      lvLayers.getItems().remove(boardLayer);
    });

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

    btnPlay.setOnAction(e -> play());

    lvLayers.setPrefHeight(120);

    return vBox;
  }

  private void play() {
    var validateResult = gameStage.validate();
    if (validateResult != null) {
      new Alert(Alert.AlertType.ERROR, validateResult, ButtonType.OK).showAndWait();
      return;
    }

    PlayWindow playWindow = new PlayWindow();
    playWindow.start(this.gameStage);
  }
}
