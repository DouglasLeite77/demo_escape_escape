package com.example.demo_escape_escape.rendering;

import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.world.TileMap;
import com.example.demo_escape_escape.world.TileType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class Renderer {

    private final GameAssets assets = new GameAssets();

    public void renderMap(GraphicsContext gc, TileMap tileMap) {

        gc.setImageSmoothing(false);

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(); column++) {

                TileType tile = tileMap.getTile(row, column);

                double x = column * TileMap.TILE_SIZE;
                double y = row * TileMap.TILE_SIZE;

                if (tile == TileType.WALL) {

                    gc.drawImage(
                            assets.getWall(),
                            x,
                            y,
                            TileMap.TILE_SIZE,
                            TileMap.TILE_SIZE
                    );

                } else {

                    gc.drawImage(
                            assets.getFloor(),
                            x,
                            y,
                            TileMap.TILE_SIZE,
                            TileMap.TILE_SIZE
                    );

                    if (tile == TileType.DEBRIS) {

                        gc.drawImage(
                                assets.getDebris(),
                                x,
                                y,
                                TileMap.TILE_SIZE,
                                TileMap.TILE_SIZE
                        );
                    }
                }
            }
        }
    }

    public void renderPath(GraphicsContext gc, List<GridNode> path) {

        gc.setFill(Color.YELLOW);

        for (GridNode node : path) {

            double x = node.getColumn() * TileMap.TILE_SIZE;
            double y = node.getRow() * TileMap.TILE_SIZE;

            gc.fillRect(
                    x + 10,
                    y + 10,
                    TileMap.TILE_SIZE - 20,
                    TileMap.TILE_SIZE - 20
            );
        }
    }


}