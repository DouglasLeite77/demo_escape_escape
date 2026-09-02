    package com.example.demo_escape_escape.rendering;

    import com.example.demo_escape_escape.ai.pathfinding.GridNode;
    import com.example.demo_escape_escape.world.TileMap;
    import com.example.demo_escape_escape.world.TileType;

    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.paint.Color;

    import java.util.List;

    public class Renderer {

        public void renderMap(GraphicsContext gc, TileMap tileMap) {

            for (int row = 0; row < tileMap.getRows(); row++) {

                for (int column = 0; column < tileMap.getColumns(); column++) {

                    TileType tile = tileMap.getTile(row, column);

                    switch (tile) {

                        case FLOOR:
                            gc.setFill(Color.web("#4A4A4A"));
                            break;

                        case WALL:
                            gc.setFill(Color.web("#20242A"));
                            break;

                        case DEBRIS:
                            gc.setFill(Color.web("#795548"));
                            break;
                    }

                    double x = column * TileMap.TILE_SIZE;
                    double y = row * TileMap.TILE_SIZE;

                    gc.fillRect(
                            x,
                            y,
                            TileMap.TILE_SIZE,
                            TileMap.TILE_SIZE
                    );

                    gc.setStroke(Color.web("#30343A"));

                    gc.strokeRect(
                            x,
                            y,
                            TileMap.TILE_SIZE,
                            TileMap.TILE_SIZE
                    );
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