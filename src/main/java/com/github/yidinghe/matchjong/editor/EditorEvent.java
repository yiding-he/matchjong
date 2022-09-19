package com.github.yidinghe.matchjong.editor;

import com.github.yidinghe.matchjong.editor.component.EditorBoardLayer;

public class EditorEvent {

  public record AddTile(int colIndex, int rowIndex) {

  }

  public record DeleteTile(int colIndex, int rowIndex) {

  }

  public record DeleteLayer(EditorBoardLayer layer) {


  }
}
