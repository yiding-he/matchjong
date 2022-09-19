package com.github.yidinghe.matchjong.editor;

public class EditorEvent {

  public record AddTile(int colIndex, int rowIndex) {

  }

  public record DeleteTile(int colIndex, int rowIndex) {

  }

}
