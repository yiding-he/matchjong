package com.github.yidinghe.matchjong.editor.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Tile extends Canvas {

  public static final Color BORDER_COLOR = Color.web("#66AAFF");

  private final int layer;

  private final int colIndex;

  private final int rowIndex;

  private final int value;

  public Tile(int value, int layer, int colIndex, int rowIndex, Image image) {
    this(value, layer, colIndex, rowIndex, image, BORDER_COLOR);
  }

  public Tile(int value, int layer, int colIndex, int rowIndex, Image image, Color borderColor) {
    this.value = value;
    this.layer = layer;
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
    var tileWidth = GameEditorBoard.CELL_WIDTH * 2;
    var tileHeight = GameEditorBoard.CELL_HEIGHT * 2;
    this.setWidth(tileWidth);
    this.setHeight(tileHeight);

    var context = getGraphicsContext2D();
    context.setFill(borderColor);
    context.fillRoundRect(0, 0, tileWidth, tileHeight, 10, 10);

    context.setFill(Color.WHITE);
    context.fillRoundRect(1, 1, tileWidth - 2, tileHeight - 5, 10, 10);

    if (image != null) {
      var imgRatio = image.getWidth() / image.getHeight();
      var tileRatio = (double) tileWidth / tileHeight;
      var scale = imgRatio > tileRatio ? (tileWidth / image.getWidth()) : (tileHeight / image.getHeight());
      var imgSize = new double[]{image.getWidth() * scale - 2, image.getHeight() * scale - 2};
      var imgPos = new double[]{(tileWidth - imgSize[0]) / 2.0, (tileHeight - imgSize[1]) / 2.0};

      context.setImageSmoothing(true);
      context.drawImage(image, imgPos[0], imgPos[1] - 2, imgSize[0], imgSize[1]);
    }
  }

  public int getLayer() {
    return layer;
  }

  public int getValue() {
    return value;
  }

  public int getColIndex() {
    return colIndex;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public boolean covers(int colIndex, int rowIndex) {
    return (this.colIndex == colIndex || this.colIndex + 1 == colIndex) &&
      (this.rowIndex == rowIndex || this.rowIndex + 1 == rowIndex);
  }

  public boolean overlaps(int colIndex, int rowIndex) {
    return colIndex >= this.colIndex - 1 && colIndex <= this.colIndex + 1 &&
      rowIndex >= this.rowIndex - 1 && rowIndex <= this.rowIndex + 1;
  }
}
