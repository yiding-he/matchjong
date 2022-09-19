module com.github.yidinghe.matchjong {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.yidinghe.matchjong to javafx.fxml;
    opens com.github.yidinghe.matchjong.editor.component to javafx.fxml;
    exports com.github.yidinghe.matchjong;
    exports com.github.yidinghe.matchjong.controller;
    opens com.github.yidinghe.matchjong.controller to javafx.fxml;
}
