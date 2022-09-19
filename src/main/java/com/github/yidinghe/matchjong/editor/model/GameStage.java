package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

public class GameStage {

  private int matchCount = 3;

  private int bufferSize = 8;

  private final int cols;

  private final int rows;

  private final List<GameStageLayer> stageLayers = new ArrayList<>();

  //////////////////////////////////////////////

  public GameStage(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
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
