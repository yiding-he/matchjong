package com.github.yidinghe.matchjong.editor.component;

import com.github.yidinghe.matchjong.editor.model.GameStage;
import com.github.yidinghe.matchjong.editor.model.GameStageLayer;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameEditorBoard extends StackPane {

  public static final int CELL_WIDTH = 15;

  public static final int CELL_HEIGHT = 20;

  public static final int CELL_DEPTH = 5;

  private final int rows;

  private final int cols;

  private final List<EditorBoardLayer> boardLayers = new ArrayList<>();

  public GameEditorBoard(int cols, int rows) {
    this.rows = rows;
    this.cols = cols;
    updateSize();
  }

  private void updateSize() {
    var prefSize = new double[]{
      this.cols * CELL_WIDTH,
      this.rows * CELL_HEIGHT + Math.max(0, (this.boardLayers.size() - 1)) * CELL_DEPTH
    };
    this.setPrefSize(prefSize[0], prefSize[1]);
    this.setMinSize(prefSize[0], prefSize[1]);
    this.setMaxSize(prefSize[0], prefSize[1]);
  }

  public EditorBoardLayer addLayer(int layer) {
    var boardLayer = new EditorBoardLayer(cols, rows);
    boardLayer.setLayer(layer);
    this.boardLayers.add(boardLayer);
    this.getChildren().add(boardLayer);
    updateSize();
    relocateLayers();
    return boardLayer;
  }

  public List<EditorBoardLayer> getBoardLayers() {
    return boardLayers;
  }

  private int layerLayoutY(int layer) {
    return (boardLayers.size() - layer - 1) * 5;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public void removeLayer(int layer) {
    this.boardLayers.removeIf(l -> l.getLayer() == layer);
    this.getChildren().removeIf(l -> l instanceof EditorBoardLayer && ((EditorBoardLayer) l).getLayer() == layer);
    this.boardLayers.forEach(l -> {
      if (l.getLayer() > layer) {
        l.setLayer(l.getLayer() - 1);
      }
    });
    updateSize();
    relocateLayers();
  }

  private void relocateLayers() {
    this.boardLayers.forEach(layer -> layer.setLayoutY(layerLayoutY(layer.getLayer())));
  }

  public void loadGameStage(GameStage gameStage) {
    this.boardLayers.clear();
    this.getChildren().removeIf(c -> c instanceof EditorBoardLayer);

    var boardLayers = new ArrayList<>(gameStage.getStageLayers());
    boardLayers.sort(Comparator.comparing(GameStageLayer::getLayer));

    boardLayers.forEach(bl -> {
      var eLayer = addLayer(bl.getLayer());
      bl.getTiles().forEach(bt -> {
        var tile = new Tile(-1, bl.getLayer(), bt.getColIndex(), bt.getRowIndex(), null);
        tile.setActive(true);
        eLayer.addTile(tile);
      });
      eLayer.setActive(true);
    });
  }
}
