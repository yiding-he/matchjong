package com.github.yidinghe.matchjong.editor.component;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class GameEditorBoard extends StackPane {

  public static final int CELL_WIDTH = 15;

  public static final int CELL_HEIGHT = 20;

  private final int rows;

  private final int cols;

  private final double[] prefSize;

  private final List<BoardLayer> boardLayers = new ArrayList<>();

  public GameEditorBoard(int cols, int rows) {
    this.rows = rows;
    this.cols = cols;

    this.prefSize = new double[]{this.cols * CELL_WIDTH, (this.rows + 1) * CELL_HEIGHT};
    this.setPrefSize(this.prefSize[0], this.prefSize[1]);
    this.setMinSize(this.prefSize[0], this.prefSize[1]);
    this.setMaxSize(this.prefSize[0], this.prefSize[1]);
  }

  public BoardLayer addLayer() {
    var boardLayer = createBoardLayer(cols, rows);
    boardLayer.setLayer(this.boardLayers.size());
    this.boardLayers.add(boardLayer);
    this.getChildren().add(boardLayer);
    return boardLayer;
  }

  public List<BoardLayer> getBoardLayers() {
    return boardLayers;
  }

  private BoardLayer createBoardLayer(int cols, int rows) {
    var boardLayer = new BoardLayer(cols, rows);
    var height = this.prefSize[1];
    boardLayer.layerProperty().addListener((observable, oldValue, newValue) -> {
      boardLayer.setLayoutX(0);
      boardLayer.setLayoutY((height - boardLayer.getHeight()) + (newValue.intValue() * -3));
    });
    return boardLayer;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

}
