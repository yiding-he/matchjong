package com.github.yidinghe.matchjong.editor.component;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 棋子界面控件。每个棋子有坐标、图片、值、边框颜色
 */
public class Tile extends Canvas {

  /**
   * 默认棋子边框颜色
   */
  public static final Color DEFAULT_BORDER_COLOR = Color.web("#66AAFF");

  /**
   * 空白棋子的值。空白棋子用于：
   * 1、关卡编辑器；
   * 2、游玩时展示缓冲区。
   */
  public static final int BLANK = -1;

  private final int layer;

  private final int colIndex;

  private final int rowIndex;

  private final Color borderColor;

  /**
   * 表示棋子是否可以被点击消除。
   * 一个棋子上面没有被其他棋子覆盖时，方可被点击消除。
   */
  private boolean active;

  private Image image;

  /**
   * 棋子的值。
   * 被选中的相同值的棋子达到指定数量时，会被消除。
   */
  private int value;

  public Tile(int value, int layer, int colIndex, int rowIndex, Image image) {
    this(value, layer, colIndex, rowIndex, image, DEFAULT_BORDER_COLOR);
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
    // 注意这里的棋子高度在渲染的时候是包含了厚度的
    var tileHeight = getHeight();
    var context = getGraphicsContext2D();
    var round = 5;
    var border = 2;

    // 棋子边框和侧面
    context.setFill(borderColor);
    context.fillRoundRect(0, 0, tileWidth, tileHeight, round * 2, round * 2);

    // 底部和右边的神色线条，模拟阴影
    // 相关的计算方式和相乘系数是不断调试得到的，以达到美观目的
    var shadowColor = borderColor.darker();
    context.setStroke(shadowColor);
    context.moveTo(0.5, tileHeight - border * 2);
    context.arcTo(border * 0.8284, tileHeight - border * 0.8284, border * 2, tileHeight - 0.5, border * 2);
    context.lineTo(tileWidth - border * 2, tileHeight - 0.5);
    context.arcTo(tileWidth - border * 0.8284, tileHeight - border * 0.8284, tileWidth, tileHeight - border * 2, border * 2);
    context.lineTo(tileWidth, border * 2);
    context.stroke();

    // 棋子面板
    // 相关的计算方式和相乘系数是不断调试得到的，以达到美观目的
    context.setFill(Color.WHITE);
    context.fillRoundRect(border, border, tileWidth - 2 * border, GameEditorBoard.CELL_HEIGHT * 2 - 2 * border, round, round);

    // 棋子图案
    drawTileImage(context);

    // 如果不可点击，则将面板颜色调暗
    if (!active) {
      ColorAdjust c = new ColorAdjust();
      c.setBrightness(-0.15);
      this.setEffect(c);
    } else {
      this.setEffect(null);
    }
  }

  private void drawTileImage(GraphicsContext context) {
    // 根据比例缩放图片再绘制上去
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

  public Image getImage() {
    return image;
  }

  public void update(Image image, int value) {
    this.image = image;
    this.value = value;
    draw();
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

  public boolean isBlank() {
    return this.value == BLANK;
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
