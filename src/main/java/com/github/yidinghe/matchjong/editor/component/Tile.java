package com.github.yidinghe.matchjong.editor.component;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Tile extends Canvas {

  public static final Color BORDER_COLOR = Color.web("#66AAFF");

  private final int layer;

  private final int colIndex;

  private final int rowIndex;

  private final int value;

  private final Image image;

  private final Color borderColor;

  private boolean active;

  public Tile(int value, int layer, int colIndex, int rowIndex, Image image) {
    this(value, layer, colIndex, rowIndex, image, BORDER_COLOR);
  }

  public Tile(int value, int layer, int colIndex, int rowIndex, Image image, Color borderColor) {
    this.value = value;
    this.layer = layer;
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
    this.image = image;
    this.borderColor = borderColor;

    var tileWidth = GameEditorBoard.CELL_WIDTH * 2;
    var tileHeight = GameEditorBoard.CELL_HEIGHT * 2 + GameEditorBoard.CELL_DEPTH;
    this.setWidth(tileWidth);
    this.setHeight(tileHeight);

    draw();
  }

  private void draw() {
    var tileWidth = getWidth();
    var tileHeight = getHeight();
    var context = getGraphicsContext2D();
    var round = 5;
    var border = 2;
    context.setFill(borderColor);
    context.fillRoundRect(0, 0, tileWidth, tileHeight, round * 2, round * 2);

    var shadowColor = borderColor.darker();
    context.setStroke(shadowColor);
    context.moveTo(0.5, tileHeight - border * 2);
    context.arcTo(border * 0.8284, tileHeight - border * 0.8284, border * 2, tileHeight - 0.5, border * 2);
    context.lineTo(tileWidth - border * 2, tileHeight - 0.5);
    context.arcTo(tileWidth - border * 0.8284, tileHeight - border * 0.8284, tileWidth, tileHeight - border * 2, border * 2);
    context.lineTo(tileWidth, border * 2);
    context.stroke();

    context.setFill(Color.WHITE);
    context.fillRoundRect(border, border, tileWidth - 2 * border, GameEditorBoard.CELL_HEIGHT * 2 - 2 * border, round, round);

    drawTileImage(context);

    if (!active) {
      ColorAdjust c = new ColorAdjust();
      c.setBrightness(-0.15);
      this.setEffect(c);
    } else {
      this.setEffect(null);
    }
  }

  private void drawTileImage(GraphicsContext context) {
    if (image != null) {
      var tileWidth = getWidth();
      var tileHeight = getHeight();
      var imgRatio = image.getWidth() / image.getHeight();
      var tileRatio = tileWidth / tileHeight;
      var scale = imgRatio > tileRatio ? (tileWidth / image.getWidth()) : (tileHeight / image.getHeight());
      var padding = Math.max(2, tileWidth / 8.0);
      var imgSize = new double[]{image.getWidth() * scale - padding * 2, image.getHeight() * scale - padding * 2};
      var imgPos = new double[]{(tileWidth - imgSize[0]) / 2.0, (tileHeight - imgSize[1]) / 2.0};

      context.setImageSmoothing(true);
      context.drawImage(image, imgPos[0], imgPos[1] - 2, imgSize[0], imgSize[1]);
    }
  }

  public void setActive(boolean active) {
    if (this.active != active) {
      Platform.runLater(this::draw);
    }
    this.active = active;
  }

  public boolean isActive() {
    return active;
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

  public boolean overlaps(Tile t) {
    return overlaps(t.getColIndex(), t.getRowIndex());
  }

  @Override
  public String toString() {
    return "Tile{" +
      "layer=" + layer +
      ", colIndex=" + colIndex +
      ", rowIndex=" + rowIndex +
      ", active=" + active +
      '}';
  }
}
