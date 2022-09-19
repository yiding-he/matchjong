package com.github.yidinghe.matchjong.editor.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Tile extends Canvas {

  private final int colIndex;

  private final int rowIndex;

  public Tile(int colIndex, int rowIndex, Image image) {
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
    var tileWidth = GameEditorBoard.CELL_WIDTH * 2;
    var tileHeight = GameEditorBoard.CELL_HEIGHT * 2;
    this.setWidth(tileWidth);
    this.setHeight(tileHeight);

    var imgRatio = image.getWidth() / image.getHeight();
    var tileRatio = (double) tileWidth / tileHeight;
    var scale = imgRatio > tileRatio ? (tileWidth / image.getWidth()) : (tileHeight / image.getHeight());
    var imgSize = new double[]{image.getWidth() * scale - 2, image.getHeight() * scale - 2};
    var imgPos = new double[]{(tileWidth - imgSize[0]) / 2.0, (tileHeight - imgSize[1]) / 2.0};

    var context = getGraphicsContext2D();
    context.setImageSmoothing(true);
    context.drawImage(image, imgPos[0], imgPos[1], imgSize[0], imgSize[1]);
    context.setStroke(Color.ROYALBLUE);
    context.setLineWidth(2);
    context.strokeRect(0, 0, tileWidth, tileHeight);
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
}
