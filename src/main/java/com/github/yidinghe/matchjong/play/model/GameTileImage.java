package com.github.yidinghe.matchjong.play.model;

import com.github.yidinghe.matchjong.editor.model.GameStage;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class GameTileImage extends Image {

  public static List<GameTileImage> parseGameStage(GameStage gameStage) {
    return gameStage.getTilePack().getTileImages().stream().map(s -> {
      var bytes = Base64.getDecoder().decode(s);
      return create(new ByteArrayInputStream(bytes));
    }).collect(Collectors.toList());
  }

  public static GameTileImage create(InputStream is) {
    try {
      var bytes = is.readAllBytes();
      return new GameTileImage(new ByteArrayInputStream(bytes), bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
