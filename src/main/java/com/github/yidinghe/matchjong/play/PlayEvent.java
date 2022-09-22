package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.Tile;

public class PlayEvent {

  public record DeleteTileEvent(Tile tile) {
  }

  public record TilesCountChanged(int count) {
  }
}
