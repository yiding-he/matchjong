package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.TileImages;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayWindow extends Stage {

  private GamePlay gamePlay;

  public void start(GameStage gameStage) {
    this.gamePlay = new GamePlay(gameStage, TileImages.getTileImages());
    this.initModality(Modality.APPLICATION_MODAL);
    this.setScene(new Scene(root()));
    this.show();
    System.out.println("开始一局，共 " + gameStage.tilesCount() + " 个块，每 " +
      gameStage.getMatchCount() + " 个相同块消除，缓冲区域可放置 " + gameStage.getBufferSize() + " 个块。");
  }

  private BorderPane root() {
    var borderPane = new BorderPane();
    borderPane.setCenter(createGameBoard());
    borderPane.autosize();
    borderPane.setPadding(new Insets(10));
    return borderPane;
  }

  private GamePlayBoard createGameBoard() {
    var gameStage = this.gamePlay.getGameStage();
    var gamePlayBoard = new GamePlayBoard(gameStage.getCols(), gameStage.getRows());
    gameStage.getStageLayers().forEach(stageLayer -> {
      var boardLayer = gamePlayBoard.addLayer(stageLayer.getLayer());
      boardLayer.setOpacity(1);
      boardLayer.enableBackground(false);
    });
    this.gamePlay.fillTiles(gamePlayBoard);
    return gamePlayBoard;
  }
}
