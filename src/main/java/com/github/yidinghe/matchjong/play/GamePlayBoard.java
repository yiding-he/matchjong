package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import com.github.yidinghe.matchjong.editor.component.Tile;
import com.github.yidinghe.matchjong.util.EventBus;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GamePlayBoard extends Pane {

  private final int layers;

  private final List<Tile> tiles = new ArrayList<>();

  public GamePlayBoard(int cols, int rows, int layers) {
    this.layers = layers;
    var size = new double[]{
      cols * GameEditorBoard.CELL_WIDTH,
      rows * GameEditorBoard.CELL_HEIGHT + layers * GameEditorBoard.CELL_DEPTH
    };
    setPrefSize(size[0], size[1]);
    setMinSize(size[0], size[1]);
    setMaxSize(size[0], size[1]);
  }

  public void addTile(Tile tile) {
    tile.setLayoutX(getTileLayoutX(tile));
    tile.setLayoutY(getTileLayoutY(tile));
    tile.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
      if (e.getButton() == MouseButton.PRIMARY) {
        this.tileClicked(tile);
      }
    });
    this.getChildren().add(tile);
    this.tiles.add(tile);
    EventBus.fire(new PlayEvent.TilesCountChanged(this.tiles.size()));
  }

  private void tileClicked(Tile tile) {
    if (tile.isActive()) {
      this.getChildren().remove(tile);
      this.tiles.remove(tile);
      updateTileActive(tile);
      EventBus.fire(new PlayEvent.TileBuffered(tile));
      EventBus.fire(new PlayEvent.TilesCountChanged(this.tiles.size()));
    }
  }

  public void updateTileActive(Tile removed) {
    this.tiles.stream().filter(
      t -> removed == null || t.overlaps(removed)
    ).forEach(t -> t.setActive(this.tiles.stream().noneMatch(
      tt -> tt.getLayer() > t.getLayer() && tt.overlaps(t))
    ));
  }

  private double getTileLayoutY(Tile tile) {
    return (layers - tile.getLayer() - 1) * GameEditorBoard.CELL_DEPTH +
      tile.getRowIndex() * GameEditorBoard.CELL_HEIGHT;
  }

  private double getTileLayoutX(Tile tile) {
    return tile.getColIndex() * GameEditorBoard.CELL_WIDTH;
  }

}
