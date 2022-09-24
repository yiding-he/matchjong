package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.Tile;

public class PlayEvent {

  private PlayEvent() {

  }

  public record TileBuffered(Tile tile) {

  }

  public record TilesCountChanged(int count) {

  }

  public record GameOver() {

  }

  public record Win() {

  }
}
