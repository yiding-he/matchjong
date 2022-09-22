package com.github.yidinghe.matchjong.editor;

import com.github.yidinghe.matchjong.editor.component.Tile;

public class EditEvent {

  public record DeleteTileEvent(Tile tile) {

  }

  public record AddTileEvent(Tile tile) {

  }
}
