package com.github.yidinghe.matchjong.editor.model;

import java.util.ArrayList;
import java.util.List;

public class GameTilePack {

  private String name;

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
