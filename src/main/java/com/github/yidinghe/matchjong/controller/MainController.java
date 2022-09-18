package com.github.yidinghe.matchjong.controller;

import com.github.yidinghe.matchjong.component.Board;
import javafx.scene.layout.BorderPane;

public class MainController {

    public BorderPane root;

    private Board board;

    public void initialize() {
        board = new Board(30, 30);
        this.root.setCenter(board);
    }
}