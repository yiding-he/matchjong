package com.github.yidinghe.matchjong.play;

import com.github.yidinghe.matchjong.editor.component.GameEditorBoard;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;

public class GamePlayBoard extends GameEditorBoard {

  public GamePlayBoard(int cols, int rows) {
    super(cols, rows);
    this.setBorder(new Border(new BorderStroke(Color.web("#DDDDDD"),
      BorderStrokeStyle.SOLID, null, BorderStroke.DEFAULT_WIDTHS))
    );
  }
}
