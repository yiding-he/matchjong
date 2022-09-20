package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

public class GameStageLayer {

  private int layer;

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
