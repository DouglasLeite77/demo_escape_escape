package com.example.demo_escape_escape;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class HelloApplication extends Application {

    private double jogadorX = 400;
    private double jogadorY = 300;
    private final double VELOCIDADE = 200;

    private final Set<KeyCode> teclasAtivas = new HashSet<>();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> teclasAtivas.add(event.getCode()));
        scene.setOnKeyReleased(event -> teclasAtivas.remove(event.getCode()));

        final long[] tempoAnterior = {System.nanoTime()};

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long tempoAtual) {
                double deltaTime = (tempoAtual - tempoAnterior[0]) / 1_000_000_000.0;
                tempoAnterior[0] = tempoAtual;

                atualizarLogica(deltaTime);
                renderizar(gc, deltaTime);
            }
        };

        stage.setTitle("Escape Escape - Prática Guiada");
        stage.setScene(scene);
        stage.show();

        gameLoop.start();
    }

    private void atualizarLogica(double deltaTime) {
        if (teclasAtivas.contains(KeyCode.W) || teclasAtivas.contains(KeyCode.UP)) {
            jogadorY -= VELOCIDADE * deltaTime;
        }
        if (teclasAtivas.contains(KeyCode.S) || teclasAtivas.contains(KeyCode.DOWN)) {
            jogadorY += VELOCIDADE * deltaTime;
        }
        if (teclasAtivas.contains(KeyCode.A) || teclasAtivas.contains(KeyCode.LEFT)) {
            jogadorX -= VELOCIDADE * deltaTime;
        }
        if (teclasAtivas.contains(KeyCode.D) || teclasAtivas.contains(KeyCode.RIGHT)) {
            jogadorX += VELOCIDADE * deltaTime;
        }
    }

    private void renderizar(GraphicsContext gc, double deltaTime) {
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, 800, 600);

        gc.setFill(Color.ORANGE);
        gc.fillRect(jogadorX, jogadorY, 30, 30);

        gc.setFill(Color.LIME);
        int fps = (int) (1 / deltaTime);
        gc.fillText("FPS: " + fps, 10, 20);
        gc.fillText(String.format("Prisioneiro -> X: %.0f | Y: %.0f", jogadorX, jogadorY), 10, 40);
    }

    public static void main(String[] args) {
        launch();
    }
}