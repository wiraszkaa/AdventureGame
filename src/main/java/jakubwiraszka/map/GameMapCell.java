package jakubwiraszka.map;

import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.gamefiles.Position;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;

public class GameMapCell {
    private final GridPane cellGridPane = new GridPane();
    private final Position position;

    public GameMapCell(Location location) {
        this.position = location.getPosition();
        HashMap<String, Position> exits = location.getExits();
        cellGridPane.add(createPathImageView(), 1, 1);
        cellGridPane.add(createGrassImageView(), 0, 0);
        cellGridPane.add(createGrassImageView(), 2, 0);
        cellGridPane.add(createGrassImageView(), 0, 2);
        cellGridPane.add(createGrassImageView(), 2, 2);
        if (exits.get("N") != null) {
            cellGridPane.add(createPathImageView(), 1, 0);
        } else {
            cellGridPane.add(createGrassImageView(), 1, 0);
        }
        if (exits.get("E") != null) {
            cellGridPane.add(createPathImageView(), 2, 1);
        } else {
            cellGridPane.add(createGrassImageView(), 2, 1);
        }
        if (exits.get("W") != null) {
            cellGridPane.add(createPathImageView(), 0, 1);
        } else {
            cellGridPane.add(createGrassImageView(), 0, 1);
        }
        if (exits.get("S") != null) {
            cellGridPane.add(createPathImageView(), 1, 2);
        } else {
            cellGridPane.add(createGrassImageView(), 1, 2);
        }

    }

    public GameMapCell(Position position) {
        this.position = position;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ImageView imageView = new ImageView(new Image(GameMapBuilder.getImageUrl("Grass.png")));
                cellGridPane.add(imageView, i, j);
            }
        }
    }

    public void update(HashMap<String, Position> exits) {
        if(exits == null || exits.size() == 0) {
            clear();
        } else {
            GameMapBuilder.getImageView(cellGridPane, 1, 1).setImage(new Image(GameMapBuilder.getImageUrl("Path.png")));
            boolean isPath = false;
            if (exits.get("N") != null) {
                isPath = true;
            }
            changeNorth(isPath);
            isPath = false;
            if (exits.get("E") != null) {
                isPath = true;
            }
            changeEast(isPath);
            isPath = false;
            if (exits.get("W") != null) {
                isPath = true;
            }
            changeWest(isPath);
            isPath = false;
            if (exits.get("S") != null) {
                isPath = true;
            }
            changeSouth(isPath);
        }
    }

    public void changeNorth(boolean isPath) {
        if(isPath) {
            GameMapBuilder.getImageView(cellGridPane, 1, 0).setImage(new Image(GameMapBuilder.getImageUrl("Path.png")));
        } else {
            GameMapBuilder.getImageView(cellGridPane, 1, 0).setImage(new Image(GameMapBuilder.getImageUrl("Grass.png")));
        }
    }

    public void changeEast(boolean isPath) {
        if(isPath) {
            GameMapBuilder.getImageView(cellGridPane, 2, 1).setImage(new Image(GameMapBuilder.getImageUrl("Path.png")));
        } else {
            GameMapBuilder.getImageView(cellGridPane, 2, 1).setImage(new Image(GameMapBuilder.getImageUrl("Grass.png")));
        }
    }

    public void changeWest(boolean isPath) {
        if(isPath) {
            GameMapBuilder.getImageView(cellGridPane, 0, 1).setImage(new Image(GameMapBuilder.getImageUrl("Path.png")));
        } else {
            GameMapBuilder.getImageView(cellGridPane, 0, 1).setImage(new Image(GameMapBuilder.getImageUrl("Grass.png")));
        }
    }

    public void changeSouth(boolean isPath) {
        if(isPath) {
            GameMapBuilder.getImageView(cellGridPane, 1, 2).setImage(new Image(GameMapBuilder.getImageUrl("Path.png")));
        } else {
            GameMapBuilder.getImageView(cellGridPane, 1, 2).setImage(new Image(GameMapBuilder.getImageUrl("Grass.png")));
        }
    }

    public void clear() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                GameMapBuilder.getImageView(cellGridPane, i, j).setImage(new Image(GameMapBuilder.getImageUrl("Grass.png")));
            }
        }
    }

    private ImageView createPathImageView() {
        return new ImageView(new Image(GameMapBuilder.getImageUrl("Path.png")));
    }

    private ImageView createGrassImageView() {
        return new ImageView(new Image(GameMapBuilder.getImageUrl("Grass.png")));
    }

    public GridPane getGameMapCell() {
        return cellGridPane;
    }

    public Position getPosition() {
        return position;
    }
}
