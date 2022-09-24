module com.github.yidinghe.matchjong {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.fasterxml.jackson.databind;

  exports com.github.yidinghe.matchjong;
  exports com.github.yidinghe.matchjong.controller;

  opens com.github.yidinghe.matchjong to javafx.fxml;
  opens com.github.yidinghe.matchjong.editor.component to javafx.fxml;
  opens com.github.yidinghe.matchjong.controller to javafx.fxml;
  opens com.github.yidinghe.matchjong.editor.model to com.fasterxml.jackson.databind;
  exports com.github.yidinghe.matchjong.play.model;
  opens com.github.yidinghe.matchjong.play.model to com.fasterxml.jackson.databind, javafx.fxml;

}
