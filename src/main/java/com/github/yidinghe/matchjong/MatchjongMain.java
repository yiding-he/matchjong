package com.github.yidinghe.matchjong;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchjongMain {

    public static void main(String[] args) {
        SpringApplication.run(MatchjongMain.class, args);
        Application.launch(FxApp.class, args);
    }
}
