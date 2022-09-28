package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.util.EventBus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GamePlayGround extends BorderPane {

  private static GamePlayGround INSTANCE;

  private final GamePlay gamePlay;

  static {
    EventBus.on(PlayEvent.TileBuffered.class, e -> {
      INSTANCE.gamePlay.addBufferedTile(e.tile());
      INSTANCE.hbBuffers.getChildren().setAll(INSTANCE.gamePlay.getBufferTiles());
    });

    EventBus.on(PlayEvent.GameOver.class,
      e -> INSTANCE.gamePlayBoard.setDisable(true)
    );
  }

  private final HBox hbBuffers;

  private final GamePlayBoard gamePlayBoard;

  public GamePlayGround(GamePlay gamePlay) {
    INSTANCE = this;
    this.gamePlay = gamePlay;

    var gameStage = this.gamePlay.getGameStage();
    gamePlayBoard = new GamePlayBoard(
      gameStage.getCols(), gameStage.getRows(), gameStage.getStageLayers().size()
    );

    this.setCenter(gamePlayBoard);
    this.setPadding(new Insets(20));
    GameInitializer.initialize(this.gamePlay, this.gamePlayBoard);
    this.gamePlay.fillTiles(gamePlayBoard);

    hbBuffers = new HBox();
    hbBuffers.setAlignment(Pos.CENTER);
    hbBuffers.setPadding(new Insets(10, 10, 30, 10));
    hbBuffers.getChildren().setAll(gamePlay.getBufferTiles());
    this.setTop(hbBuffers);
  }
}
