package com.github.yidinghe.matchjong.editor.model;

public class GameStageTile {

  private int colIndex;

  private int rowIndex;

  public GameStageTile() {
  }

  public GameStageTile(int colIndex, int rowIndex) {
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
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
