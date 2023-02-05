package com.github.yidinghe.matchjong.editor.model;

/**
 * 地图中的棋子，包含其坐标位置
 */
public class GameStageTile {

  private int layer;      // 所在的层

  private int colIndex;   // 所在的列

  private int rowIndex;   // 所在的行

  public GameStageTile() {
  }

  public GameStageTile(int layer, int colIndex, int rowIndex) {
    this.layer = layer;
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public int getLayer() {
    return layer;
  }

  public int getColIndex() {
    return colIndex;
  }

  public void setColIndex(int colIndex) {
    this.colIndex = colIndex;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

  @Override
  public String toString() {
    return "Tile{" +
      "layer=" + layer +
      ", colIndex=" + colIndex +
      ", rowIndex=" + rowIndex +
      '}';
  }
}
