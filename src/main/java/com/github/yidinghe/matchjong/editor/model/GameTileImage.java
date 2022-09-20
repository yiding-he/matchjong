package com.github.yidinghe.matchjong.editor.model;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class GameTileImage extends Image {

  public static GameTileImage create(InputStream is) throws IOException {
    var bytes = is.readAllBytes();
    return new GameTileImage(new ByteArrayInputStream(bytes), bytes);
  }

  private final byte[] rawData;

  private GameTileImage(InputStream is, byte[] rawData) {
    super(is);
    this.rawData = rawData;
  }

  public String rawDataBase64() {
    return Base64.getEncoder().encodeToString(rawData);
  }
}
