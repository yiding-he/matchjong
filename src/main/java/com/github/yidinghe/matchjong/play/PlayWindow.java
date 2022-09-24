package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.util.EventBus;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayWindow extends Stage {

  public static PlayWindow INSTANCE;

  private GamePlay gamePlay;

  public void start(GameStage gameStage) {
    INSTANCE = this;
    var tilesCount = gameStage.tilesCount();
    EventBus.on(PlayEvent.TilesCountChanged.class, e -> setTitle(e.count() + "/" + tilesCount));

    this.gamePlay = new GamePlay(gameStage);
    this.initModality(Modality.APPLICATION_MODAL);
    this.setScene(new Scene(new GamePlayGround(gamePlay)));
    this.show();
  }
}
