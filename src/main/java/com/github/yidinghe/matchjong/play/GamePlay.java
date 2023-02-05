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

/**
 * 表示一局游戏
 */
public class GamePlay {

  private static final Random RANDOM = new SecureRandom();

  /**
   * 关卡内容
   */
  private final GameStage gameStage;

  /**
   * 棋子图案列表
   */
  private final List<GameTileImage> tileImages;

  /**
   * 缓冲区
   */
  private final List<Tile> bufferTiles;

  /**
   * 是否游戏结束
   */
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

  /**
   * 将棋子添加到缓冲区
   */
  public void addBufferedTile(Tile tile) {
    // 更新缓冲区中第一个空白棋子，并重新排序
    var emptyTile = this.bufferTiles.stream().filter(Tile::isBlank).findFirst().orElseThrow();
    emptyTile.update(tile.getImage(), tile.getValue());
    this.bufferTiles.sort(Comparator.comparing(Tile::getValue).reversed());

    // 检查是否满足消除条件
    for (int i = 0; i <= this.bufferTiles.size() - gameStage.getMatchCount(); i++) {
      boolean matched = true;
      for (int j = i; j < i + gameStage.getMatchCount(); j++) {
        if (this.bufferTiles.get(i).getValue() != this.bufferTiles.get(j).getValue()) {
          matched = false;
          break;
        }
      }
      // 将被消除的棋子替换成空白，然后放到末尾
      if (matched) {
        for (int j = 0; j < gameStage.getMatchCount(); j++) {
          var removed = this.bufferTiles.remove(i);
          removed.update(null, Tile.BLANK);
          this.bufferTiles.add(removed);
        }
      }
    }

    // 再排序一次，免得出什么问题
    this.bufferTiles.sort(Comparator.comparing(Tile::getValue).reversed());

    // 如果缓冲区满了，则判定玩家失败
    if (this.bufferTiles.stream().noneMatch(Tile::isBlank)) {
      new Alert(Alert.AlertType.INFORMATION, "你输了", ButtonType.OK).show();
      this.over = true;
      EventBus.fire(new PlayEvent.GameOver());
    }
  }

  /**
   * 摆放棋子：按照一定的算法将图案填充到棋盘的棋子上
   */
  public void fillTiles(GamePlayBoard gamePlayBoard) {
    var indexes = new ArrayList<Integer>(this.tileImages.size());
    for (int i = 0; i < this.tileImages.size(); i++) {
      indexes.add(i);
    }
    Collections.shuffle(indexes);

    // 构建棋子摆放队列，方式是按照消除个数一组一组的生成，每组的图案是随机的
    var tilesCount = this.gameStage.tilesCount();
    var queue = new ArrayList<Integer>(tilesCount);
    int tilePointer = 0;
    while (queue.size() < tilesCount) {
      for (int i = 0; i < gameStage.getMatchCount(); i++) {
        queue.add(indexes.get(tilePointer));
      }
      tilePointer += 1;
      if (tilePointer == indexes.size() - 1) {
        Collections.shuffle(indexes);
        tilePointer = 0;
      }
    }

    // 摆放棋子的顺序为从底层向顶层摆放
    // 每次在当前层中随机选择一个位置，然后从摆放队列中取图案赋给该位置的棋子
    // 这能够保证游戏一定有一个解，但缺点是大幅降低了游戏难度，且没有了使用道具的必要
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
