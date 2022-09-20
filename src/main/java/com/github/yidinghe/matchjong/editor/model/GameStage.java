package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

public class GameStage {

  private int cols;

  private int rows;

  private List<GameStageLayer> stageLayers = new ArrayList<>();

  private List<String> tileImages = new ArrayList<>();

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

  public List<String> getTileImages() {
    return tileImages;
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

  public void setTileImages(List<String> tileImages) {
    this.tileImages = tileImages;
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

  public GameStageLayer getLayer(int layer) {
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

  public String validate() {
    if (stageLayers.isEmpty()) {
      return "游戏至少要有一层layer，请点击“添加layer”";
    }

    var tilesCount = tilesCount();
    if (tilesCount == 0 || tilesCount % matchCount != 0) {
      return "游戏至少要有" + matchCount + "倍数的块，现在有 " + tilesCount + " 个块";
    }

    if (bufferSize < matchCount) {
      return "缓冲区域大小不能小于消除个数";
    }

    return null;
  }

  public int tilesCount() {
    return stageLayers.stream().mapToInt(l -> l.getTiles().size()).sum();
  }
}
