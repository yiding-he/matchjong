package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;
import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameStageLayer;
import javafx.scene.image.Image;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GamePlay {

  private static final Random RANDOM = new SecureRandom();

  private final GameStage gameStage;

  private final List<Image> tileImages;

  public GamePlay(GameStage gameStage, List<Image> tileImages) {
    this.gameStage = gameStage;
    this.tileImages = tileImages;
  }

  public GameStage getGameStage() {
    return gameStage;
  }

  public void fillTiles(GamePlayBoard gamePlayBoard) {
    var boardLayers = new ArrayList<>(gamePlayBoard.getBoardLayers());
    boardLayers.sort(Comparator.comparing(EditorBoardLayer::getLayer));

    var indexes = new ArrayList<Integer>(this.tileImages.size());
    for (int i = 0; i < this.tileImages.size(); i++) {
      indexes.add(i);
    }
    Collections.shuffle(indexes);

    var counter = new AtomicInteger();
    var index = new AtomicInteger();

    this.gameStage.getStageLayers().stream()
      .sorted(Comparator.comparing(GameStageLayer::getLayer))
      .flatMap(layer -> {
        var tiles = new ArrayList<>(layer.getTiles());
        Collections.shuffle(tiles);
        return tiles.stream();
      }).forEach(stageTile -> {
        if (counter.getAndIncrement() % this.gameStage.getMatchCount() == 0) {
          if (index.incrementAndGet() >= indexes.size()) {
            index.set(0);
          }
        }
        var value = indexes.get(index.get());
        var tile = new Tile(
          value, stageTile.getLayer(),
          stageTile.getColIndex(), stageTile.getRowIndex(),
          this.tileImages.get(value)
        );
        System.out.println(value);
        boardLayers.get(stageTile.getLayer()).addTile(tile);
      });
  }
}
