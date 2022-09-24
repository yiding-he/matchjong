package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.util.EventBus;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayWindow extends Stage {

  public static PlayWindow INSTANCE;

  private final GamePlay gamePlay;

  private final GameStage gameStage;

  static {
    EventBus.on(PlayEvent.TilesCountChanged.class, e -> {
      INSTANCE.setTitle(e.count() + "/" + INSTANCE.gameStage.tilesCount());

    });
  }

  public PlayWindow(GameStage gameStage) {
    this.gameStage = gameStage;
    this.gamePlay = new GamePlay(gameStage);
    INSTANCE = this;
  }

  public void start() {
    this.initModality(Modality.APPLICATION_MODAL);
    this.setScene(new Scene(new GamePlayGround(gamePlay)));
    this.show();
  }
}
