package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 图标包
 */
public class GameTilePack {

  /**
   * 名称
   */
  private String name;

  /**
   * 图标列表，内容为图片二进制内容的 Base64 编码
   */
  private List<String> tileImages = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getTileImages() {
    return tileImages;
  }

  public void setTileImages(List<String> tileImages) {
    this.tileImages = tileImages;
  }
}
