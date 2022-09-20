package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.TileImages;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayWindow extends Stage {

  private GamePlay gamePlay;

  public void start(GameStage gameStage) {
    this.gamePlay = new GamePlay(gameStage, TileImages.getTileImages());
    this.initModality(Modality.APPLICATION_MODAL);
    this.setScene(new Scene(new GamePlayGround(gamePlay)));
    this.show();

    System.out.println("开始一局，共 " + gameStage.tilesCount() + " 个块，每 " +
      gameStage.getMatchCount() + " 个相同块消除，缓冲区域可放置 " + gameStage.getBufferSize() + " 个块。");
  }
}
