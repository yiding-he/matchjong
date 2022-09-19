package com.github.yidinghe.matchjong.editor.component;

import com.github.yidinghe.matchjong.editor.EditorEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.yidinghe.matchjong.editor.component.GameEditorBoard.CELL_HEIGHT;
import static com.github.yidinghe.matchjong.editor.component.GameEditorBoard.CELL_WIDTH;

public class BoardLayer extends StackPane {

  public static final Image TILE_IMAGE = new Image(
    GameEditorBoard.class.getResourceAsStream("/com/github/yidinghe/matchjong/tiles/7.png")
  );

  private final int cols;

  private final int rows;

  private final int width;

  private final int height;

  private final Canvas background;

  private final Rectangle indicator;

  private final IntegerProperty layer = new SimpleIntegerProperty(-1);

  private final BooleanProperty active = new SimpleBooleanProperty(false);

  private final List<Tile> tiles = new ArrayList<>();

  private Consumer<EditorEvent.AddTile> onAddTile;

  private Consumer<EditorEvent.DeleteTile> onDeleteTile;

  public BoardLayer(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
    this.width = this.cols * CELL_WIDTH;
    this.height = this.rows * CELL_HEIGHT;

    this.background = createBackground(cols, rows, CELL_WIDTH, CELL_HEIGHT);
    this.setOpacity(0.5);
    this.indicator = createIndicator();

    this.getChildren().addAll(background, indicator);
    this.addEventFilter(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
    this.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
    this.setManaged(false);
    this.autosize();

    this.activeProperty().addListener((observable, oldValue, newValue) -> onActiveChanged(newValue));
    this.onActiveChanged(false);
  }

  private void onActiveChanged(Boolean active) {
    this.setOpacity(active ? 1 : 0.5);
    if (active) {
      this.toFront();
    } else {
      this.indicator.setVisible(false);
    }
  }

  public static void line(GraphicsContext c, int x1, int y1, int x2, int y2) {
    c.moveTo(x1 + 0.5, y1 + 0.5);
    c.lineTo(x2 + 0.5, y2 + 0.5);
    c.stroke();
  }

  public static Canvas createBackground(int cols, int rows, int cellWidth, int cellHeight) {
    int width = cols * cellWidth, height = rows * cellHeight;
    var canvas = new Canvas(width, height);
    var c = canvas.getGraphicsContext2D();
    c.setStroke(Color.web("#DDDDDD"));
    c.setLineWidth(1);
    for (int i = 0; i < cols; i++) {
      line(c, i * CELL_WIDTH, 0, i * CELL_WIDTH, height);
    }
    for (int i = 0; i < rows; i++) {
      line(c, 0, i * CELL_HEIGHT, width, i * CELL_HEIGHT);
    }
    line(c, 0, height - 1, width - 1, height - 1);
    line(c, width - 1, 0, width - 1, height - 1);
    return canvas;
  }

  public void setOnAddTile(Consumer<EditorEvent.AddTile> onAddTile) {
    this.onAddTile = onAddTile;
  }

  public void setOnDeleteTile(Consumer<EditorEvent.DeleteTile> onDeleteTile) {
    this.onDeleteTile = onDeleteTile;
  }

  public int getLayer() {
    return layer.get();
  }

  public IntegerProperty layerProperty() {
    return layer;
  }

  public void setLayer(int layer) {
    this.layer.set(layer);
  }

  public boolean isActive() {
    return active.get();
  }

  public BooleanProperty activeProperty() {
    return active;
  }

  public void setActive(boolean active) {
    this.active.set(active);
  }

  private Rectangle createIndicator() {
    var rectangle = new Rectangle();
    rectangle.setWidth(CELL_WIDTH * 2);
    rectangle.setHeight(CELL_HEIGHT * 2);
    rectangle.setStroke(Color.web("#4499AA"));
    rectangle.setStrokeWidth(1);
    rectangle.setVisible(false);
    rectangle.setFill(Color.TRANSPARENT);
    rectangle.setManaged(false);
    return rectangle;
  }

  private boolean outsideBoard(MouseEvent e) {
    return e.getX() < 0 || e.getX() > this.width || e.getY() < 0 || e.getY() > this.height;
  }

  // [colIndex, rowIndex, layoutX, layoutY]
  private int[] cellPosition(MouseEvent e) {
    var colIndex = Math.min((int) e.getX() / CELL_WIDTH, this.cols - 2);
    var rowIndex = Math.min((int) e.getY() / CELL_HEIGHT, this.rows - 2);
    return new int[]{
      colIndex, rowIndex, colIndex * CELL_WIDTH, rowIndex * CELL_HEIGHT
    };
  }

  private void onMouseMoved(MouseEvent e) {
    if (!isActive() || outsideBoard(e)) {
      return;
    }
    var cellPosition = cellPosition(e);
    indicator.setLayoutX(cellPosition[2]);
    indicator.setLayoutY(cellPosition[3]);
    indicator.setVisible(true);
    e.consume();
  }

  private void onMouseClicked(MouseEvent e) {
    if (!isActive() || outsideBoard(e)) {
      return;
    }

    var cellPosition = cellPosition(e);
    if (e.getButton() == MouseButton.PRIMARY) {  // add tile
      createTile(cellPosition);
      if (this.onAddTile != null) {
        this.onAddTile.accept(new EditorEvent.AddTile(cellPosition[0], cellPosition[1]));
      }
    } else if (e.getButton() == MouseButton.SECONDARY) {  // delete tile
      findTiles(cellPosition[0], cellPosition[1]).forEach(t -> {
        this.tiles.remove(t);
        this.getChildren().remove(t);
      });
      if (this.onDeleteTile != null) {
        this.onDeleteTile.accept(new EditorEvent.DeleteTile(cellPosition[0], cellPosition[1]));
      }
    }

    e.consume();
  }

  private void createTile(int[] cellPosition) {
    Tile tile = new Tile(cellPosition[0], cellPosition[1], TILE_IMAGE);
    tile.setManaged(false);
    tile.setLayoutX(cellPosition[2]);
    tile.setLayoutY(cellPosition[3]);
    this.getChildren().add(tile);
    this.tiles.add(tile);
  }

  private List<Tile> findTiles(int colIndex, int rowIndex) {
    return this.tiles.stream().filter(t -> t.covers(colIndex, rowIndex)).collect(Collectors.toList());
  }
}
