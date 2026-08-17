package com.example.demo_escape_escape;

import com.example.demo_escape_escape.rendering.Renderer;
import com.example.demo_escape_escape.world.TileMap;

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

    private double jogadorX = 64;
    private double jogadorY = 64;

    private static final double VELOCIDADE = 200;
    private static final double TAMANHO_JOGADOR = 30;

    private final TileMap tileMap = new TileMap();
    private final Renderer renderer = new Renderer();

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
        double deslocamento = VELOCIDADE * deltaTime;
        if (teclasAtivas.contains(KeyCode.W) || teclasAtivas.contains(KeyCode.UP)) {
            double novoY = jogadorY - deslocamento;
            if (podeMoverPara(jogadorX, novoY)) {
                jogadorY = novoY;
            }
        }

        if (teclasAtivas.contains(KeyCode.S) || teclasAtivas.contains(KeyCode.DOWN)) {
            double novoY = jogadorY + deslocamento;
            if (podeMoverPara(jogadorX, novoY)) {
                jogadorY = novoY;
            }
        }

        if (teclasAtivas.contains(KeyCode.A) || teclasAtivas.contains(KeyCode.LEFT)) {
            double novoX = jogadorX - deslocamento;
            if (podeMoverPara(novoX, jogadorY)) {
                jogadorX = novoX;
            }
        }

        if (teclasAtivas.contains(KeyCode.D) || teclasAtivas.contains(KeyCode.RIGHT)) {
            double novoX = jogadorX + deslocamento;
            if (podeMoverPara(novoX, jogadorY)) {
                jogadorX = novoX;
            }
        }
    }

    private boolean podeMoverPara(double novoX, double novoY) {

        int colunaEsquerda =
                (int) Math.floor(novoX / TileMap.TILE_SIZE);

        int colunaDireita =
                (int) Math.floor(
                        (novoX + TAMANHO_JOGADOR - 1)
                                / TileMap.TILE_SIZE
                );

        int linhaSuperior =
                (int) Math.floor(novoY / TileMap.TILE_SIZE);

        int linhaInferior =
                (int) Math.floor(
                        (novoY + TAMANHO_JOGADOR - 1)
                                / TileMap.TILE_SIZE
                );

        return tileMap.isWalkable(linhaSuperior, colunaEsquerda)
                && tileMap.isWalkable(linhaSuperior, colunaDireita)
                && tileMap.isWalkable(linhaInferior, colunaEsquerda)
                && tileMap.isWalkable(linhaInferior, colunaDireita);
    }

    private void renderizar(GraphicsContext gc, double deltaTime) {
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, 800, 600);

        renderer.renderMap(gc, tileMap);

        gc.setFill(Color.ORANGE);
        gc.fillRect(
                jogadorX,
                jogadorY,
                TAMANHO_JOGADOR,
                TAMANHO_JOGADOR
        );

        gc.setFill(Color.LIME);

        int fps = (int) (1 / deltaTime);

        gc.fillText("FPS: " + fps, 10, 20);

        gc.fillText(
                String.format(
                        "Prisioneiro -> X: %.0f | Y: %.0f",
                        jogadorX,
                        jogadorY
                ),
                10,
                40
        );
    }

    public static void main(String[] args) {
        launch();
    }
}