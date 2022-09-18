package com.github.yidinghe.matchjong.component;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends StackPane {

    public static final int CELL_SIZE = 20;

    private final int rows;

    private final int cols;

    private final Point2D size;

    private final Rectangle indicator = new Rectangle();

    private final Canvas background;

    private boolean mouseMoving = false;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = new Point2D(this.cols * CELL_SIZE, this.rows * CELL_SIZE);
        this.setPrefSize(this.cols * CELL_SIZE, this.rows * CELL_SIZE);
        this.setMinSize(this.cols * CELL_SIZE, this.rows * CELL_SIZE);
        this.setMaxSize(this.cols * CELL_SIZE, this.rows * CELL_SIZE);

        background = createBackground();
        this.getChildren().add(background);

        this.indicator.setWidth(CELL_SIZE * 2);
        this.indicator.setHeight(CELL_SIZE * 2);
        this.indicator.setStroke(Color.web("#4499AA"));
        this.indicator.setStrokeWidth(1);
        this.indicator.setVisible(false);
        this.indicator.setFill(Color.TRANSPARENT);
        this.indicator.setManaged(false);
        this.getChildren().add(this.indicator);

        this.background.addEventFilter(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
    }

    private static void line(GraphicsContext c, int x1, int y1, int x2, int y2) {
        c.moveTo(x1 + 0.5, y1 + 0.5);
        c.lineTo(x2 + 0.5, y2 + 0.5);
        c.stroke();
    }

    private Canvas createBackground() {
        var canvas = new Canvas(this.size.getX(), this.size.getY());
        var c = canvas.getGraphicsContext2D();
        c.setStroke(Color.web("#DDDDDD"));
        c.setLineWidth(1);
        for (int i = 0; i < this.cols; i++) {
            line(c, i * CELL_SIZE, 0, i * CELL_SIZE, (int) this.size.getY());
        }
        for (int i = 0; i < this.rows; i++) {
            line(c, 0, i * CELL_SIZE, (int) this.size.getX(), i * CELL_SIZE);
        }
        line(c, 0, (int) this.size.getY() - 1, (int) this.size.getX() - 1, (int) this.size.getY() - 1);
        line(c, (int) this.size.getX() - 1, 0, (int) this.size.getX() - 1, (int) this.size.getY() - 1);
        return canvas;
    }

    private void onMouseMoved(MouseEvent e) {
        if (e.getX() < 0 || e.getX() > this.size.getX() || e.getY() < 0 || e.getY() > this.size.getY()) {
            return;
        }
        var col = Math.min((int) e.getX() / CELL_SIZE, this.cols - 2);
        var row = Math.min((int) e.getY() / CELL_SIZE, this.rows - 2);
        indicator.setLayoutX(col * CELL_SIZE);
        indicator.setLayoutY(row * CELL_SIZE);
        indicator.setVisible(true);
    }
}
