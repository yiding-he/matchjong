package com.github.yidinghe.matchjong.play;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class GamePlayGround extends BorderPane {

  private final GamePlay gamePlay;

  public GamePlayGround(GamePlay gamePlay) {
    this.gamePlay = gamePlay;

    var gameStage = this.gamePlay.getGameStage();
    var gamePlayBoard = new GamePlayBoard(gameStage.getCols(), gameStage.getRows());

    this.setCenter(gamePlayBoard);
    this.setPadding(new Insets(20));
  }
}
