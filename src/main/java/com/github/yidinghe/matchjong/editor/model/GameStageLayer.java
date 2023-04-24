package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图中的层，棋子放在某一层中
 */
public class GameStageLayer {

  /**
   * 层位置
   */
  private int layer;

  /**
   * 层中摆放的棋子。每个棋子包含了坐标位置。如果 tiles 为空，则表示本层没有棋子
   */
  private List<GameStageTile> tiles = new ArrayList<>();

  public GameStageLayer() {
  }

  public GameStageLayer(int layer) {
    this.layer = layer;
  }

  public int getLayer() {
    return layer;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public List<GameStageTile> getTiles() {
    return tiles;
  }

  public void setTiles(List<GameStageTile> tiles) {
    this.tiles = tiles;
  }

  public void addTile(int colIndex, int rowIndex) {
    this.tiles.add(new GameStageTile(layer, colIndex, rowIndex));
  }

  public void deleteTile(int colIndex, int rowIndex) {
    this.tiles.removeIf(t -> t.getColIndex() == colIndex && t.getRowIndex() == rowIndex);
  }
}
