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

  private final List<EditorBoardLayer> boardLayers = new ArrayList<>();

  public GameEditorBoard(int cols, int rows) {
    this.rows = rows;
    this.cols = cols;

    this.prefSize = new double[]{this.cols * CELL_WIDTH, (this.rows + 1) * CELL_HEIGHT};
    this.setPrefSize(this.prefSize[0], this.prefSize[1]);
    this.setMinSize(this.prefSize[0], this.prefSize[1]);
    this.setMaxSize(this.prefSize[0], this.prefSize[1]);
  }

  public EditorBoardLayer addLayer(int layer) {
    var boardLayer = createBoardLayer(cols, rows);
    boardLayer.setLayer(layer);
    this.boardLayers.add(boardLayer);
    this.getChildren().add(boardLayer);
    return boardLayer;
  }

  public List<EditorBoardLayer> getBoardLayers() {
    return boardLayers;
  }

  private EditorBoardLayer createBoardLayer(int cols, int rows) {
    var boardLayer = new EditorBoardLayer(cols, rows);
    var height = this.prefSize[1];
    boardLayer.layerProperty().addListener((observable, oldValue, newValue) -> {
      boardLayer.setLayoutX(0);
      boardLayer.setLayoutY((height - boardLayer.getHeight()) + (newValue.intValue() * -5));
    });
    return boardLayer;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public void removeLayer(int layer) {
    this.boardLayers.removeIf(l -> l.getLayer() == layer);
    this.getChildren().removeIf(l -> l instanceof EditorBoardLayer && ((EditorBoardLayer)l).getLayer() == layer);
    this.boardLayers.forEach(l -> {
      if (l.getLayer() > layer) {
        l.setLayer(l.getLayer() - 1);
      }
    });

    System.out.println(this.boardLayers);
  }
}
