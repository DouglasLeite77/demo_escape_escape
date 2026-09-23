package com.example.demo_escape_escape;

import com.example.demo_escape_escape.rendering.Renderer;
import com.example.demo_escape_escape.world.TileMap;
import com.example.demo_escape_escape.entity.Agent;
import com.example.demo_escape_escape.entity.Guard;
import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.ai.state.EnemyState;
import com.example.demo_escape_escape.ai.Noise;
import com.example.demo_escape_escape.rendering.GameAssets;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class HelloApplication extends Application {

    private double jogadorX = 64;
    private double jogadorY = 64;

    private static final double VELOCIDADE = 200;
    private static final double TAMANHO_JOGADOR = 50;
    private static final double TAMANHO_HITBOX = 30;

    private final TileMap tileMap = new TileMap();
    private final Renderer renderer = new Renderer();
    private final GameAssets assets = new GameAssets();

    private final Agent agent = new Agent(2, 2);
    private final Guard guard = new Guard(2, 20);
    private final Noise noise = new Noise();

    private int goalRow = 16;
    private int goalColumn = 23;

    private List<GridNode> path;

    private final Set<KeyCode> teclasAtivas = new HashSet<>();
    private boolean debugMode = false;

    private static final int EXIT_ROW = 16;
    private static final int EXIT_COLUMN = 23;

    private boolean gameWon = false;
    private boolean gameOver = false;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);


        agent.setDestination(goalRow, goalColumn);

        path = agent.getPath();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            teclasAtivas.add(event.getCode());

            if (event.getCode() == KeyCode.F3) {
                debugMode = !debugMode;
            }

            if (event.getCode() == KeyCode.R
                    && (gameWon || gameOver)) {

                reiniciarJogo();
            }

            if (event.getCode() == KeyCode.DIGIT1
                    || event.getCode() == KeyCode.NUMPAD1) {

                goalRow = 2;
                goalColumn = 20;

                agent.setDestination(
                        goalRow,
                        goalColumn
                );
            }

            if (event.getCode() == KeyCode.DIGIT2
                    || event.getCode() == KeyCode.NUMPAD2) {

                goalRow = 14;
                goalColumn = 23;

                agent.setDestination(
                        goalRow,
                        goalColumn
                );
            }


            if (event.getCode() == KeyCode.DIGIT3
                    || event.getCode() == KeyCode.NUMPAD3) {

                goalRow = 16;
                goalColumn = 2;

                agent.setDestination(
                        goalRow,
                        goalColumn
                );
            }
        });

        scene.addEventHandler(
                KeyEvent.KEY_RELEASED,
                event -> teclasAtivas.remove(event.getCode())
        );

        final long[] tempoAnterior = {System.nanoTime()};


        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long tempoAtual) {

                double deltaTime =
                        (tempoAtual - tempoAnterior[0])
                                / 1_000_000_000.0;

                tempoAnterior[0] = tempoAtual;

                atualizarLogica(deltaTime);

                renderizar(gc, deltaTime);
            }
        };

        stage.setTitle("Escape Escape - A* Pathfinding");

        stage.setScene(scene);
        stage.show();

        canvas.requestFocus();

        gameLoop.start();
    }

    private void reiniciarJogo() {

        jogadorX = 64;
        jogadorY = 64;

        gameWon = false;
        gameOver = false;

        guard.reset();
    }


    private void atualizarLogica(double deltaTime) {

        if (gameWon || gameOver) {
            return;
        }

        double jogadorXAnterior = jogadorX;
        double jogadorYAnterior = jogadorY;

        noise.clear();

        double deslocamento =
                VELOCIDADE * deltaTime;


        if (teclasAtivas.contains(KeyCode.W)
                || teclasAtivas.contains(KeyCode.UP)) {

            double novoY =
                    jogadorY - deslocamento;

            if (podeMoverPara(
                    jogadorX,
                    novoY
            )) {
                jogadorY = novoY;
            }
        }

        if (teclasAtivas.contains(KeyCode.S)
                || teclasAtivas.contains(KeyCode.DOWN)) {

            double novoY =
                    jogadorY + deslocamento;

            if (podeMoverPara(
                    jogadorX,
                    novoY
            )) {
                jogadorY = novoY;
            }
        }

        if (teclasAtivas.contains(KeyCode.A)
                || teclasAtivas.contains(KeyCode.LEFT)) {

            double novoX =
                    jogadorX - deslocamento;

            if (podeMoverPara(
                    novoX,
                    jogadorY
            )) {
                jogadorX = novoX;
            }
        }

        if (teclasAtivas.contains(KeyCode.D)
                || teclasAtivas.contains(KeyCode.RIGHT)) {

            double novoX =
                    jogadorX + deslocamento;

            if (podeMoverPara(
                    novoX,
                    jogadorY
            )) {
                jogadorX = novoX;
            }
        }

        if (jogadorX != jogadorXAnterior
                || jogadorY != jogadorYAnterior) {

            noise.emit(
                    jogadorX + TAMANHO_JOGADOR / 2,
                    jogadorY + TAMANHO_JOGADOR / 2,
                    128
            );
        }

        agent.update(
                deltaTime,
                tileMap
        );

        path = agent.getPath();

        guard.update(
                deltaTime,
                tileMap,
                jogadorX,
                jogadorY,
                TAMANHO_JOGADOR,
                noise
        );

        if (guardaCapturouJogador()) {
            gameOver = true;
            return;
        }

        int playerRow =
                (int) (
                        (jogadorY + TAMANHO_JOGADOR / 2)
                                / TileMap.TILE_SIZE
                );

        int playerColumn =
                (int) (
                        (jogadorX + TAMANHO_JOGADOR / 2)
                                / TileMap.TILE_SIZE
                );

        if (playerRow == EXIT_ROW
                && playerColumn == EXIT_COLUMN) {

            gameWon = true;
        }

        path = agent.getPath();
    }



    private boolean podeMoverPara(
            double novoX,
            double novoY
    ) {

        double margem =
                (TAMANHO_JOGADOR - TAMANHO_HITBOX) / 2;

        double hitboxX = novoX + margem;
        double hitboxY = novoY + margem;

        int colunaEsquerda =
                (int) Math.floor(
                        hitboxX / TileMap.TILE_SIZE
                );

        int colunaDireita =
                (int) Math.floor(
                        (hitboxX + TAMANHO_HITBOX - 1)
                                / TileMap.TILE_SIZE
                );

        int linhaSuperior =
                (int) Math.floor(
                        hitboxY / TileMap.TILE_SIZE
                );

        int linhaInferior =
                (int) Math.floor(
                        (hitboxY + TAMANHO_HITBOX - 1)
                                / TileMap.TILE_SIZE
                );

        return tileMap.isWalkable(
                linhaSuperior,
                colunaEsquerda
        )
                && tileMap.isWalkable(
                linhaSuperior,
                colunaDireita
        )
                && tileMap.isWalkable(
                linhaInferior,
                colunaEsquerda
        )
                && tileMap.isWalkable(
                linhaInferior,
                colunaDireita
        );
    }

    private boolean guardaCapturouJogador() {

        double margemPlayer =
                (TAMANHO_JOGADOR - TAMANHO_HITBOX) / 2;

        double playerX = jogadorX + margemPlayer;
        double playerY = jogadorY + margemPlayer;

        double hitboxGuarda = 24;

        double margemGuarda =
                (guard.getSize() - hitboxGuarda) / 2;

        double guardX =
                guard.getX() + margemGuarda;

        double guardY =
                guard.getY() + margemGuarda;

        return playerX < guardX + hitboxGuarda
                && playerX + TAMANHO_HITBOX > guardX
                && playerY < guardY + hitboxGuarda
                && playerY + TAMANHO_HITBOX > guardY;
    }

    private void renderizar(
            GraphicsContext gc,
            double deltaTime
    ) {


        gc.setFill(
                Color.web("#1e1e1e")
        );

        gc.fillRect(
                0,
                0,
                800,
                600
        );

        renderer.renderMap(
                gc,
                tileMap
        );

        double exitX = EXIT_COLUMN * TileMap.TILE_SIZE;
        double exitY = EXIT_ROW * TileMap.TILE_SIZE;

        double exitSize = 48;

        gc.drawImage(
                assets.getExit(),
                exitX - 8,
                exitY - 8,
                exitSize,
                exitSize
        );

        if (debugMode) {
            renderer.renderPath(
                    gc,
                    path
            );
        }

        gc.drawImage(
                assets.getPlayer(),
                jogadorX,
                jogadorY,
                TAMANHO_JOGADOR,
                TAMANHO_JOGADOR
        );

        gc.drawImage(
                assets.getGuard(),
                guard.getX(),
                guard.getY(),
                guard.getSize(),
                guard.getSize()
        );

        if (debugMode) {

            gc.setFill(Color.LIME);

            int fps;

            if (deltaTime > 0) {
                fps = (int) (1 / deltaTime);
            } else {
                fps = 0;
            }

            gc.fillText(
                    "FPS: " + fps,
                    10,
                    20
            );

            gc.fillText(
                    String.format(
                            "Prisioneiro -> X: %.0f | Y: %.0f",
                            jogadorX,
                            jogadorY
                    ),
                    10,
                    40
            );

            gc.fillText(
                    "Destino A*: Linha "
                            + goalRow
                            + " | Coluna "
                            + goalColumn,
                    10,
                    60
            );

            gc.fillText(
                    "1 = Destino 1 | 2 = Destino 2 | 3 = Destino 3",
                    10,
                    80
            );

            gc.fillText(
                    "Estado do guarda: " + guard.getState(),
                    10,
                    100
            );
        }
        if (gameWon) {

            gc.setFill(
                    Color.rgb(
                            0,
                            0,
                            0,
                            0.75
                    )
            );

            gc.fillRect(
                    0,
                    0,
                    800,
                    600
            );

            gc.setFill(Color.WHITE);

            gc.setFont(
                    javafx.scene.text.Font.font(36)
            );

            gc.fillText(
                    "VOCÊ ESCAPOU!",
                    275,
                    280
            );

            gc.setFont(
                    javafx.scene.text.Font.font(18)
            );

            gc.fillText(
                    "Pressione R para jogar novamente",
                    260,
                    325
            );
        }

        if (gameOver) {

            gc.setFill(
                    Color.rgb(
                            0,
                            0,
                            0,
                            0.75
                    )
            );

            gc.fillRect(
                    0,
                    0,
                    800,
                    600
            );

            gc.setFill(Color.WHITE);

            gc.setFont(
                    javafx.scene.text.Font.font(36)
            );

            gc.fillText(
                    "VOCÊ FOI CAPTURADO!",
                    210,
                    280
            );

            gc.setFont(
                    javafx.scene.text.Font.font(18)
            );

            gc.fillText(
                    "Pressione R para tentar novamente",
                    255,
                    325
            );
        }
    }

    public static void main(String[] args) {
        launch();
    }
}