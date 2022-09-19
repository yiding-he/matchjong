package com.github.yidinghe.matchjong.editor.model;

public class GameStageTile {

  private int layer;

  private int colIndex;

  private int rowIndex;

  public GameStageTile(int layer, int colIndex, int rowIndex) {
    this.layer = layer;
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
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
}
