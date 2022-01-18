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
            setImage(1, 1, "Path.png");
            boolean isPath = exits.get("N") != null;
            changeNorth(isPath);
            isPath = exits.get("E") != null;
            changeEast(isPath);
            isPath = exits.get("W") != null;
            changeWest(isPath);
            isPath = exits.get("S") != null;
            changeSouth(isPath);
        }
    }

    public void changeNorth(boolean isPath) {
        if(isPath) {
            setImage(1, 0, "Path.png");
        } else {
            setImage(1, 0, "Grass.png");
        }
    }

    public void changeEast(boolean isPath) {
        if(isPath) {
            setImage(2, 1, "Path.png");
        } else {
            setImage(2, 1, "Grass.png");
        }
    }

    public void changeWest(boolean isPath) {
        if(isPath) {
            setImage(0, 1, "Path.png");
        } else {
            setImage(0, 1, "Grass.png");
        }
    }

    public void changeSouth(boolean isPath) {
        if(isPath) {
            setImage(1, 2, "Path.png");
        } else {
            setImage(1, 2, "Grass.png");
        }
    }

    public void clear() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                setImage(i, j, "Grass.png");
            }
        }
    }

    private ImageView createPathImageView() {
        return new ImageView(new Image(GameMapBuilder.getImageUrl("Path.png")));
    }

    private ImageView createGrassImageView() {
        return new ImageView(new Image(GameMapBuilder.getImageUrl("Grass.png")));
    }

    private void setImage(int x, int y, String source) {
        ImageView imageView = GameMapBuilder.getImageView(cellGridPane, x, y);
        if (imageView != null) {
            imageView.setImage(new Image(GameMapBuilder.getImageUrl(source)));
        }
    }

    public GridPane getGameMapCell() {
        return cellGridPane;
    }

    public Position getPosition() {
        return position;
    }
}
