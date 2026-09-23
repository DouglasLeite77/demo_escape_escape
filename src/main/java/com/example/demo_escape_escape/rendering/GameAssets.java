package com.example.demo_escape_escape.rendering;

import javafx.scene.image.Image;

public class GameAssets {

    private final Image floor;
    private final Image wall;
    private final Image debris;
    private final Image player;
    private final Image guard;
    private final Image exit;

    public GameAssets() {
        floor = load("/assets/tiles/floor.png");
        wall = load("/assets/tiles/wall.png");
        debris = load("/assets/tiles/debris.png");
        exit = load("/assets/tiles/exit.png");

        player = load("/assets/characters/player.png");
        guard = load("/assets/characters/guard.png");
    }

    private Image load(String path) {
        return new Image(
                getClass()
                        .getResource(path)
                        .toExternalForm()
        );
    }

    public Image getFloor() {
        return floor;
    }

    public Image getWall() {
        return wall;
    }

    public Image getDebris() {
        return debris;
    }

    public Image getPlayer() {
        return player;
    }

    public Image getGuard() {
        return guard;
    }

    public Image getExit() {
        return exit;
    }
}