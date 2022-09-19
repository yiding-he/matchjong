package com.github.yidinghe.matchjong.controller;

import com.github.yidinghe.matchjong.editor.component.BoardLayer;
import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

  public HBox root;

  private GameStage gameStage = new GameStage();

  private GameEditorBoard editorBoard;

  public void initialize() {
    editorBoard = new GameEditorBoard(40, 30);
    this.root.getChildren().addAll(
      createLayerList(),
      editorBoard
    );
  }

  private VBox createLayerList() {
    var lvLayers = new ListView<BoardLayer>();
    var btnAddLayer = new Button("添加Layer");
    var btnDeleteLayer = new Button("删除Layer");
    var hbButtons = new HBox(10, btnAddLayer, btnDeleteLayer);
    var tile = new Tile(-1, -1, BoardLayer.TILE_IMAGE, Tile.BORDER_COLOR);
    var vBox = new VBox(10, hbButtons, lvLayers, tile);

    hbButtons.setAlignment(Pos.BASELINE_LEFT);
    btnAddLayer.setOnAction(e -> {
      if (this.editorBoard.getBoardLayers().size() >= 5) {
        return;
      }
      var boardLayer = this.editorBoard.addLayer();
      boardLayer.setOnAddTile(event -> gameStage.getLayer(boardLayer.getLayer()).addTile(event.colIndex(), event.rowIndex()));
      boardLayer.setOnDeleteTile(event -> gameStage.getLayer(boardLayer.getLayer()).deleteTile(event.colIndex(), event.rowIndex()));
      lvLayers.getItems().add(0, boardLayer);
    });

    btnDeleteLayer.setOnAction(e -> {
      var boardLayer = lvLayers.getSelectionModel().getSelectedItem();
      if (boardLayer == null) {
        return;
      }

      this.editorBoard.removeLayer(boardLayer);
      this.gameStage.removeLayer(boardLayer.getLayer());
      lvLayers.getItems().remove(boardLayer);
    });

    lvLayers.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(BoardLayer item, boolean empty) {
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

    lvLayers.setPrefHeight(120);

    return vBox;
  }
}
