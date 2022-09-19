package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

public class GameStage {

  private final List<GameStageLayer> stageLayers = new ArrayList<>();

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
}
