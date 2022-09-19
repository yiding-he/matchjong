package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.model.GameStage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayWindow extends Stage {

  private GameStage gameStage;

  public void start(GameStage gameStage) {
    this.gameStage = gameStage;
    this.initModality(Modality.APPLICATION_MODAL);
    this.setScene(new Scene(root()));
    this.show();
  }

  private BorderPane root() {
    var borderPane = new BorderPane();
    borderPane.setCenter(createGameBoard());
    borderPane.autosize();
    return borderPane;
  }

  private GamePlayBoard createGameBoard() {
    var gamePlayBoard = new GamePlayBoard(this.gameStage.getCols(), this.gameStage.getRows());
    this.gameStage.getStageLayers().forEach(stageLayer -> {
      var boardLayer = gamePlayBoard.addLayer(stageLayer.getLayer());
      stageLayer.getTiles().forEach(stageTile -> {
        boardLayer.addTile(stageTile.getColIndex(), stageTile.getRowIndex());
      });
      boardLayer.setOpacity(1);
      boardLayer.enableBackground(false);
    });
    return gamePlayBoard;
  }
}
