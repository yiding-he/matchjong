package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameStageLayer;
import com.github.yidinghe.matchjong.editor.model.GameStageTile;
import com.github.yidinghe.matchjong.play.model.GameTileImage;
import com.github.yidinghe.matchjong.util.EventBus;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.security.SecureRandom;
import java.util.*;

public class GamePlay {

  private static final Random RANDOM = new SecureRandom();

  private final GameStage gameStage;

  private final List<GameTileImage> tileImages;

  private final List<Tile> bufferTiles;

  private boolean over;

  public GamePlay(GameStage gameStage) {
    this.gameStage = gameStage;
    this.tileImages = GameTileImage.parseGameStage(gameStage);
    this.bufferTiles = new ArrayList<>();

    for (int i = 0; i < gameStage.getBufferSize(); i++) {
      var tile = new Tile(-1, -1, 0, 0, null);
      tile.setActive(true);
      this.bufferTiles.add(tile);
    }
  }

  public GameStage getGameStage() {
    return gameStage;
  }

  public List<GameTileImage> getTileImages() {
    return tileImages;
  }

  public List<Tile> getBufferTiles() {
    return this.bufferTiles;
  }

  public void addBufferedTile(Tile tile) {
    var emptyTile = this.bufferTiles.stream().filter(Tile::isBlank).findFirst().orElseThrow();
    emptyTile.update(tile.getImage(), tile.getValue());
    this.bufferTiles.sort(Comparator.comparing(Tile::getValue).reversed());

    for (int i = 0; i <= this.bufferTiles.size() - gameStage.getMatchCount(); i++) {
      boolean matched = true;
      for (int j = i; j < i + gameStage.getMatchCount(); j++) {
        if (this.bufferTiles.get(i).getValue() != this.bufferTiles.get(j).getValue()) {
          matched = false;
          break;
        }
      }
      if (matched) {
        for (int j = 0; j < gameStage.getMatchCount(); j++) {
          var removed = this.bufferTiles.remove(i);
          removed.update(null, Tile.BLANK);
          this.bufferTiles.add(removed);
        }
      }
    }
    this.bufferTiles.sort(Comparator.comparing(Tile::getValue).reversed());

    if (this.bufferTiles.stream().noneMatch(Tile::isBlank)) {
      new Alert(Alert.AlertType.INFORMATION, "你输了", ButtonType.OK).show();
      this.over = true;
      EventBus.fire(new PlayEvent.GameOver());
    }
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
      this.gameStage.getStageLayers().stream()
        .sorted(Comparator.comparing(GameStageLayer::getLayer))
        .flatMap(layer -> {
          var tiles = new ArrayList<>(layer.getTiles());
          Collections.shuffle(tiles);
          return tiles.stream().sorted(Comparator.comparing(GameStageTile::getRowIndex));
        }).forEach(stageTile -> {
          var pick = RANDOM.nextInt(Math.min(queue.size(), gameStage.getBufferSize() * 2));
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
      Platform.runLater(() -> gamePlayBoard.updateTileActive(null));
    }).start();
  }

}
