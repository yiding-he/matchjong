package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameStageLayer;
import com.github.yidinghe.matchjong.editor.model.GameStageTile;
import com.github.yidinghe.matchjong.play.model.GameTileImage;
import javafx.application.Platform;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GamePlay {

  private static final Random RANDOM = new SecureRandom();

  private final GameStage gameStage;

  private final List<GameTileImage> tileImages;

  public GamePlay(GameStage gameStage) {
    this.gameStage = gameStage;
    this.tileImages = GameTileImage.parseGameStage(gameStage);
  }

  public GameStage getGameStage() {
    return gameStage;
  }

  public void fillTiles(GamePlayBoard gamePlayBoard) {
    var indexes = new ArrayList<Integer>(this.tileImages.size());
    for (int i = 0; i < this.tileImages.size(); i++) {
      indexes.add(i);
    }
    Collections.shuffle(indexes);

    var tilesCount = this.gameStage.tilesCount();
    var queue = new ArrayList<Integer>(tilesCount);
    int tilePointer = 0;
    while (queue.size() < tilesCount) {
      for (int i = 0; i < gameStage.getMatchCount(); i++) {
        queue.add(indexes.get(tilePointer));
      }
      tilePointer+=1;
      if (tilePointer == indexes.size() - 1) {
        Collections.shuffle(indexes);
        tilePointer = 0;
      }
    }

    new Thread(() -> {
      var counter = new AtomicInteger();
      this.gameStage.getStageLayers().stream()
        .sorted(Comparator.comparing(GameStageLayer::getLayer))
        .flatMap(layer -> {
          var tiles = new ArrayList<>(layer.getTiles());
          Collections.shuffle(tiles);
          return tiles.stream().sorted(Comparator.comparing(GameStageTile::getRowIndex));
        }).forEach(stageTile -> {
          var pick = RANDOM.nextInt(Math.min(queue.size(), gameStage.getBufferSize()));
          var value = queue.remove(pick);
          Platform.runLater(() -> {
            var tile = new Tile(
              value, stageTile.getLayer(),
              stageTile.getColIndex(), stageTile.getRowIndex(),
              this.tileImages.get(value)
            );
            gamePlayBoard.addTile(tile);
          });
        });
      Platform.runLater(gamePlayBoard::updateTileActive);
    }).start();
  }

}
