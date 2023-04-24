package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示一个关卡，关卡中有以下要素：
 * 1、棋盘（包含多个 GameStageLayer 即层，棋子放置在层中）
 * 2、缓冲区（可定制大小）
 * 3、玩法规则（允许不同关卡有不同玩法）
 * 4、花色包（决定了关卡中有多少种棋子）
 * 5、其他状态（例如只读）
 */
public class GameStage {

  private int cols;   // 棋盘列数

  private int rows;   // 棋盘行数

  // 棋盘层数
  private List<GameStageLayer> stageLayers = new ArrayList<>();

  private GameTilePack tilePack;

  private int matchCount = 3;

  private int bufferSize = 8;

  private boolean readOnly;

  //////////////////////////////////////////////

  public GameStage() {
  }

  public GameStage(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public int getCols() {
    return cols;
  }

  public int getRows() {
    return rows;
  }

  public List<GameStageLayer> getStageLayers() {
    return stageLayers;
  }

  public void setCols(int cols) {
    this.cols = cols;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public void setStageLayers(List<GameStageLayer> stageLayers) {
    this.stageLayers = stageLayers;
  }

  public GameTilePack getTilePack() {
    return tilePack;
  }

  public void setTilePack(GameTilePack tilePack) {
    this.tilePack = tilePack;
  }

  public int getMatchCount() {
    return matchCount;
  }

  public void setMatchCount(int matchCount) {
    this.matchCount = matchCount;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public void addLayer(int layer) {
    this.stageLayers.add(new GameStageLayer(layer));
  }

  public GameStageLayer getOrCreateLayer(int layer) {
    return stageLayers.stream()
      .filter(l -> l.getLayer() == layer)
      .findFirst()
      .orElseGet(() -> {
          GameStageLayer newLayer = new GameStageLayer(layer);
          this.stageLayers.add(newLayer);
          return newLayer;
        }
      );
  }

  public void removeLayer(int layer) {
    this.stageLayers.removeIf(l -> l.getLayer() == layer);
    this.stageLayers.forEach(l -> {
      if (l.getLayer() > layer) {
        l.setLayer(l.getLayer() - 1);
      }
    });
  }

  /**
   * 检查关卡是否可以游玩，返回 null 表示可以，否则返回相关错误信息
   */
  public String validate() {
    if (stageLayers.isEmpty()) {
      return "游戏至少要有一层layer，请点击“添加layer”";
    }

    var tilesCount = tilesCount();
    if (tilesCount == 0 || tilesCount % matchCount != 0) {
      return "游戏至少要有" + matchCount + "倍数的块，现在有 " + tilesCount + " 个块";
    }

    if (bufferSize < matchCount) {
      return "缓冲区域大小不能小于消除个数，即" + matchCount + "个";
    }

    if (tilePack == null) {
      return "请选择或添加图标包";
    }

    return null;
  }

  /**
   * 获取关卡中全部的棋子数量
   */
  public int tilesCount() {
    return stageLayers.stream().mapToInt(l -> l.getTiles().size()).sum();
  }
}
