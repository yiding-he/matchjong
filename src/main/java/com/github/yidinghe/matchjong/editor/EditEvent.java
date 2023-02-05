package com.github.yidinghe.matchjong.editor;

import com.github.yidinghe.matchjong.editor.component.Tile;

/**
 * 棋盘编辑事件
 */
public class EditEvent {

  // 删除棋子
  public record DeleteTileEvent(Tile tile) {

  }

  // 添加棋子
  public record AddTileEvent(Tile tile) {

  }
}
