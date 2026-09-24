package com.example.demo_escape_escape;

import com.example.demo_escape_escape.rendering.Renderer;
import com.example.demo_escape_escape.world.TileMap;
import com.example.demo_escape_escape.entity.Agent;
import com.example.demo_escape_escape.entity.Guard;
import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.ai.Noise;
import com.example.demo_escape_escape.rendering.GameAssets;
import com.example.demo_escape_escape.entity.GuardType;

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

    private double jogadorXAntesEsconder;
    private double jogadorYAntesEsconder;

    private static final double VELOCIDADE = 200;
    private static final double TAMANHO_JOGADOR = 50;
    private static final double TAMANHO_HITBOX = 30;

    private final TileMap tileMap = new TileMap();
    private final Renderer renderer = new Renderer();
    private final GameAssets assets = new GameAssets();

    private final Agent agent = new Agent(2, 2);
    private final Guard patrolGuard =
            new Guard(
                    GuardType.PATRULHEIRO,
                    2,
                    20,
                    new int[][]{
                            {2, 20},
                            {6, 22},
                            {6, 10},
                            {3, 12}
                    }
            );

    private final Guard investigatorGuard =
            new Guard(
                    GuardType.INVESTIGADOR,
                    9,
                    16,
                    new int[][]{
                            {9, 16},
                            {8, 14},
                            {11, 18}
                    }
            );

    private final Guard securityGuard =
            new Guard(
                    GuardType.SEGURANCA,
                    14,
                    21,
                    new int[][]{
                            {14, 21},
                            {13, 23},
                            {16, 20},
                            {13, 21}
                    }
            );

    private final List<Guard> guards =
            java.util.Arrays.asList(
                    patrolGuard,
                    investigatorGuard,
                    securityGuard
            );

    private final Noise noise = new Noise();

    private int goalRow = 16;
    private int goalColumn = 23;

    private static final int CARD_ROW = 9;
    private static final int CARD_COLUMN = 5;

    private static final int DOOR_ROW = 16;
    private static final int DOOR_COLUMN = 22;

    private static final int HIDE_1_ROW = 3;
    private static final int HIDE_1_COLUMN = 8;

    private static final int HIDE_2_ROW = 11;
    private static final int HIDE_2_COLUMN = 18;

    private static final int EXIT_ROW = 16;
    private static final int EXIT_COLUMN = 23;

    private boolean doorUnlocked = false;
    private boolean hasAccessCard = false;
    private boolean playerHidden = false;

    private List<GridNode> path;

    private final Set<KeyCode> teclasAtivas = new HashSet<>();
    private boolean debugMode = false;

    private boolean gameWon = false;
    private boolean gameOver = false;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        agent.setDestination(
                goalRow,
                goalColumn
        );

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

            if (event.getCode() == KeyCode.E) {

                if (playerHidden) {

                    sairDoEsconderijo();

                } else if (jogadorPertoDoTile(
                        HIDE_1_ROW,
                        HIDE_1_COLUMN
                )) {

                    entrarNoEsconderijo(
                            HIDE_1_ROW,
                            HIDE_1_COLUMN
                    );

                } else if (jogadorPertoDoTile(
                        HIDE_2_ROW,
                        HIDE_2_COLUMN
                )) {

                    entrarNoEsconderijo(
                            HIDE_2_ROW,
                            HIDE_2_COLUMN
                    );

                } else if (!hasAccessCard
                        && jogadorPertoDoTile(
                        CARD_ROW,
                        CARD_COLUMN
                )) {

                    hasAccessCard = true;

                } else if (hasAccessCard
                        && !doorUnlocked
                        && jogadorPertoDoTile(
                        DOOR_ROW,
                        DOOR_COLUMN
                )) {

                    doorUnlocked = true;
                }
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

        final long[] tempoAnterior = {
                System.nanoTime()
        };

        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long tempoAtual) {

                double deltaTime =
                        (tempoAtual - tempoAnterior[0])
                                / 1_000_000_000.0;

                tempoAnterior[0] = tempoAtual;

                atualizarLogica(deltaTime);

                renderizar(
                        gc,
                        deltaTime
                );
            }
        };

        stage.setTitle(
                "Escape Escape - A* Pathfinding"
        );

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

        hasAccessCard = false;
        doorUnlocked = false;
        playerHidden = false;

        noise.clear();

        for (Guard guard : guards) {
            guard.reset();
        }
    }

    private void atualizarLogica(
            double deltaTime
    ) {

        if (gameWon || gameOver) {
            return;
        }

        double jogadorXAnterior = jogadorX;
        double jogadorYAnterior = jogadorY;

        noise.clear();

        if (!playerHidden) {

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
        }

        agent.update(
                deltaTime,
                tileMap
        );

        path = agent.getPath();

        for (Guard guard : guards) {

            guard.update(
                    deltaTime,
                    tileMap,
                    jogadorX,
                    jogadorY,
                    TAMANHO_JOGADOR,
                    playerHidden,
                    noise
            );
        }

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

        if (!playerHidden
                && playerRow == EXIT_ROW
                && playerColumn == EXIT_COLUMN
                && doorUnlocked) {

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

        double hitboxX =
                novoX + margem;

        double hitboxY =
                novoY + margem;

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

        boolean mapaLivre =
                tileMap.isWalkable(
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

        if (!mapaLivre) {
            return false;
        }

        if (!doorUnlocked) {

            boolean encostandoNaPorta =
                    (linhaSuperior == DOOR_ROW
                            && colunaEsquerda == DOOR_COLUMN)
                            || (linhaSuperior == DOOR_ROW
                            && colunaDireita == DOOR_COLUMN)
                            || (linhaInferior == DOOR_ROW
                            && colunaEsquerda == DOOR_COLUMN)
                            || (linhaInferior == DOOR_ROW
                            && colunaDireita == DOOR_COLUMN);

            if (encostandoNaPorta) {
                return false;
            }
        }

        return true;
    }

    private boolean guardaCapturouJogador() {

        if (playerHidden) {
            return false;
        }

        double margemPlayer =
                (TAMANHO_JOGADOR
                        - TAMANHO_HITBOX) / 2;

        double playerX =
                jogadorX + margemPlayer;

        double playerY =
                jogadorY + margemPlayer;

        double hitboxGuarda = 24;

        for (Guard guard : guards) {

            double margemGuarda =
                    (guard.getSize()
                            - hitboxGuarda) / 2;

            double guardX =
                    guard.getX()
                            + margemGuarda;

            double guardY =
                    guard.getY()
                            + margemGuarda;

            boolean capturado =
                    playerX
                            < guardX + hitboxGuarda
                            && playerX + TAMANHO_HITBOX
                            > guardX
                            && playerY
                            < guardY + hitboxGuarda
                            && playerY + TAMANHO_HITBOX
                            > guardY;

            if (capturado) {
                return true;
            }
        }

        return false;
    }

    private boolean jogadorPertoDoTile(
            int row,
            int column
    ) {

        double playerCenterX =
                jogadorX + TAMANHO_JOGADOR / 2;

        double playerCenterY =
                jogadorY + TAMANHO_JOGADOR / 2;

        double tileCenterX =
                column * TileMap.TILE_SIZE
                        + TileMap.TILE_SIZE / 2.0;

        double tileCenterY =
                row * TileMap.TILE_SIZE
                        + TileMap.TILE_SIZE / 2.0;

        double deltaX =
                playerCenterX - tileCenterX;

        double deltaY =
                playerCenterY - tileCenterY;

        double distance =
                Math.sqrt(
                        deltaX * deltaX
                                + deltaY * deltaY
                );

        return distance <= 48;
    }

    private void entrarNoEsconderijo(
            int row,
            int column
    ) {

        jogadorXAntesEsconder =
                jogadorX;

        jogadorYAntesEsconder =
                jogadorY;

        jogadorX =
                column * TileMap.TILE_SIZE
                        + (TileMap.TILE_SIZE
                        - TAMANHO_JOGADOR) / 2;

        jogadorY =
                row * TileMap.TILE_SIZE
                        + (TileMap.TILE_SIZE
                        - TAMANHO_JOGADOR) / 2;

        playerHidden = true;

        noise.clear();
    }

    private void sairDoEsconderijo() {

        jogadorX =
                jogadorXAntesEsconder;

        jogadorY =
                jogadorYAntesEsconder;

        playerHidden = false;

        noise.clear();
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

        renderizarEsconderijo(
                gc,
                HIDE_1_ROW,
                HIDE_1_COLUMN
        );

        renderizarEsconderijo(
                gc,
                HIDE_2_ROW,
                HIDE_2_COLUMN
        );

        if (!doorUnlocked) {

            double doorX =
                    DOOR_COLUMN * TileMap.TILE_SIZE;

            double doorY =
                    DOOR_ROW * TileMap.TILE_SIZE;

            gc.setFill(Color.DARKRED);

            gc.fillRect(
                    doorX,
                    doorY,
                    TileMap.TILE_SIZE,
                    TileMap.TILE_SIZE
            );
        }

        if (!hasAccessCard) {

            double cardX =
                    CARD_COLUMN * TileMap.TILE_SIZE;

            double cardY =
                    CARD_ROW * TileMap.TILE_SIZE;

            gc.setFill(Color.GOLD);

            gc.fillRect(
                    cardX + 8,
                    cardY + 11,
                    16,
                    10
            );
        }

        if (!hasAccessCard
                && jogadorPertoDoTile(
                CARD_ROW,
                CARD_COLUMN
        )) {

            gc.setFill(Color.WHITE);

            gc.setFont(
                    javafx.scene.text.Font.font(14)
            );

            gc.fillText(
                    "E - Pegar cartão",
                    jogadorX - 25,
                    jogadorY - 10
            );
        }

        if (!doorUnlocked
                && jogadorPertoDoTile(
                DOOR_ROW,
                DOOR_COLUMN
        )) {

            gc.setFill(Color.WHITE);

            gc.setFont(
                    javafx.scene.text.Font.font(14)
            );

            if (hasAccessCard) {

                gc.fillText(
                        "E - Desbloquear porta",
                        jogadorX - 45,
                        jogadorY - 10
                );

            } else {

                gc.fillText(
                        "Porta trancada - cartão necessário",
                        jogadorX - 70,
                        jogadorY - 10
                );
            }
        }

        double exitX =
                EXIT_COLUMN * TileMap.TILE_SIZE;

        double exitY =
                EXIT_ROW * TileMap.TILE_SIZE;

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

        if (!playerHidden) {

            gc.drawImage(
                    assets.getPlayer(),
                    jogadorX,
                    jogadorY,
                    TAMANHO_JOGADOR,
                    TAMANHO_JOGADOR
            );
        }

        for (Guard guard : guards) {

            gc.drawImage(
                    assets.getGuard(),
                    guard.getX(),
                    guard.getY(),
                    guard.getSize(),
                    guard.getSize()
            );
        }

        renderizarInteracaoEsconderijo(
                gc
        );

        if (debugMode) {

            gc.setFill(Color.LIME);

            int fps;

            if (deltaTime > 0) {

                fps =
                        (int) (
                                1 / deltaTime
                        );

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

            int debugY = 100;

            for (Guard guard : guards) {

                gc.fillText(
                        guard.getType().getDisplayName()
                                + ": "
                                + guard.getState(),
                        10,
                        debugY
                );

                debugY += 20;
            }
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

    private void renderizarEsconderijo(
            GraphicsContext gc,
            int row,
            int column
    ) {

        double x =
                column * TileMap.TILE_SIZE;

        double y =
                row * TileMap.TILE_SIZE;

        gc.setFill(
                Color.DARKSLATEBLUE
        );

        gc.fillRect(
                x + 2,
                y + 1,
                TileMap.TILE_SIZE - 4,
                TileMap.TILE_SIZE - 2
        );

        gc.setStroke(
                Color.LIGHTSLATEGRAY
        );

        gc.strokeRect(
                x + 2,
                y + 1,
                TileMap.TILE_SIZE - 4,
                TileMap.TILE_SIZE - 2
        );

        gc.setFill(
                Color.GRAY
        );

        gc.fillOval(
                x + 22,
                y + 15,
                3,
                3
        );
    }

    private void renderizarInteracaoEsconderijo(
            GraphicsContext gc
    ) {

        gc.setFill(Color.WHITE);

        gc.setFont(
                javafx.scene.text.Font.font(14)
        );

        if (playerHidden) {

            gc.fillText(
                    "ESCONDIDO - E para sair",
                    jogadorX - 55,
                    jogadorY - 10
            );

            return;
        }

        if (jogadorPertoDoTile(
                HIDE_1_ROW,
                HIDE_1_COLUMN
        )
                || jogadorPertoDoTile(
                HIDE_2_ROW,
                HIDE_2_COLUMN
        )) {

            gc.fillText(
                    "E - Esconder",
                    jogadorX - 20,
                    jogadorY - 10
            );
        }
    }

    public static void main(String[] args) {
        launch();
    }
}