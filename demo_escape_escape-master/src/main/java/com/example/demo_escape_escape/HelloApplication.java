package com.example.demo_escape_escape;

import com.example.demo_escape_escape.rendering.Renderer;
import com.example.demo_escape_escape.world.TileMap;
import com.example.demo_escape_escape.entity.Agent;
import com.example.demo_escape_escape.ai.pathfinding.GridNode;

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
    private static final double TAMANHO_JOGADOR = 28;

    private final TileMap tileMap = new TileMap();
    private final Renderer renderer = new Renderer();
    private final Agent agent = new Agent(2, 2);

    private int goalRow = 16;
    private int goalColumn = 23;

    private List<GridNode> path;

    private final Set<KeyCode> teclasAtivas = new HashSet<>();

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        // Permite que o Canvas receba comandos do teclado
        canvas.setFocusTraversable(true);

        // Define o destino inicial
        agent.setDestination(goalRow, goalColumn);

        path = agent.getPath();

        // =========================
        // TECLADO
        // =========================

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            teclasAtivas.add(event.getCode());

            // =========================
            // DESTINO 1
            // =========================

            if (event.getCode() == KeyCode.DIGIT1
                    || event.getCode() == KeyCode.NUMPAD1) {

                goalRow = 2;
                goalColumn = 20;

                agent.setDestination(
                        goalRow,
                        goalColumn
                );
            }

            // =========================
            // DESTINO 2
            // =========================

            if (event.getCode() == KeyCode.DIGIT2
                    || event.getCode() == KeyCode.NUMPAD2) {

                goalRow = 14;
                goalColumn = 23;

                agent.setDestination(
                        goalRow,
                        goalColumn
                );
            }

            // =========================
            // DESTINO 3
            // =========================

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

        // =========================
        // LOOP DO JOGO
        // =========================

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

        // Força o foco no Canvas depois que a janela abrir
        canvas.requestFocus();

        gameLoop.start();
    }

    // =========================
    // ATUALIZAÇÃO DA LÓGICA
    // =========================

    private void atualizarLogica(double deltaTime) {

        double deslocamento =
                VELOCIDADE * deltaTime;

        // =========================
        // MOVIMENTO DO JOGADOR
        // =========================

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

        // =========================
        // ATUALIZA O AGENTE
        // =========================

        /*
         * O Agent verifica se o destino mudou.
         *
         * Quando o destino muda,
         * o A* calcula automaticamente
         * uma nova rota.
         */

        agent.update(
                deltaTime,
                tileMap
        );

        // Atualiza o caminho exibido na tela
        path = agent.getPath();
    }

    // =========================
    // VERIFICA COLISÃO DO JOGADOR
    // =========================

    private boolean podeMoverPara(
            double novoX,
            double novoY
    ) {

        int colunaEsquerda =
                (int) Math.floor(
                        novoX / TileMap.TILE_SIZE
                );

        int colunaDireita =
                (int) Math.floor(
                        (novoX + TAMANHO_JOGADOR - 1)
                                / TileMap.TILE_SIZE
                );

        int linhaSuperior =
                (int) Math.floor(
                        novoY / TileMap.TILE_SIZE
                );

        int linhaInferior =
                (int) Math.floor(
                        (novoY + TAMANHO_JOGADOR - 1)
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

    // =========================
    // DESENHA O JOGO
    // =========================

    private void renderizar(
            GraphicsContext gc,
            double deltaTime
    ) {

        // =========================
        // FUNDO
        // =========================

        gc.setFill(
                Color.web("#1e1e1e")
        );

        gc.fillRect(
                0,
                0,
                800,
                600
        );

        // =========================
        // MAPA
        // =========================

        renderer.renderMap(
                gc,
                tileMap
        );

        // =========================
        // CAMINHO A*
        // =========================

        renderer.renderPath(
                gc,
                path
        );

        // =========================
        // JOGADOR
        // =========================

        gc.setFill(Color.ORANGE);

        gc.fillRect(
                jogadorX,
                jogadorY,
                TAMANHO_JOGADOR,
                TAMANHO_JOGADOR
        );

        // =========================
        // INFORMAÇÕES
        // =========================

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

        // =========================
        // AGENTE
        // =========================

        gc.setFill(Color.CYAN);

        gc.fillRect(
                agent.getX(),
                agent.getY(),
                agent.getSize(),
                agent.getSize()
        );
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {
        launch();
    }
}