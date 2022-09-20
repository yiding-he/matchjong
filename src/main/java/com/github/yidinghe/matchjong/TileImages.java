package com.github.yidinghe.matchjong;

import com.github.yidinghe.matchjong.editor.model.GameTileImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TileImages {

  private static List<GameTileImage> tileImages;

  public static void load(String path) {
    try (var list = Files.list(Path.of(path))) {
      tileImages = list.map(p -> {
        try {
          return GameTileImage.create(Files.newInputStream(p));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<GameTileImage> getTileImages() {
    return tileImages;
  }
}
