package jakubwiraszka;

import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.gamefiles.World;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class CreateMap {
    public final static String ICONS_LOC = "D:\\Projekty\\Java\\AdventureFX\\resources\\";

    public static void createMapTemplate(World world, ArrayList<GridPane> gridPanes) {
        for(int i = 0; i <= world.getWidth(); i++) {
            for(int j = 0; j <= world.getHeight(); j++) {
                for(GridPane gridPane: gridPanes) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(ICONS_LOC + "Nothing.png"));
                    imageView.setFitHeight(81);
                    imageView.setPreserveRatio(true);
                    gridPane.add(imageView, i, j);
                }
            }
        }
    }

    public static ImageView getImageView(GridPane gridPane, int x, int y) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                return (ImageView) node;
            }
        }
        return null;
    }

    public static void createMap(World world, GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, boolean readVisited) {
        ArrayList<GridPane> gridPanes = new ArrayList<>();
        gridPanes.add(gameMapGridPane);
        gridPanes.add(contentMapGridPane);
        gridPanes.add(playerMapGridPane);
        createMapTemplate(world, gridPanes);
        for (Location location : world.getLocations()) {
            modifyMapCell(location, getImageView(gameMapGridPane, location.getPosition().getX(), location.getPosition().getY()), readVisited);
            modifyContentCell(location, getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()), readVisited);
        }
    }

    public static void modifyMapCell(Location location, ImageView imageView, boolean readVisited) {
        if (location.isVisited() || !readVisited) {
            boolean north = false;
            boolean south = false;
            boolean east = false;
            boolean west = false;
            if (location.getExits().get("N") != null) {
                north = true;
            }
            if (location.getExits().get("S") != null) {
                south = true;
            }
            if (location.getExits().get("E") != null) {
                east = true;
            }
            if (location.getExits().get("W") != null) {
                west = true;
            }
            if (north && south && east && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road4.png"));
            } else if (north && south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(90);
            } else if (north && south && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(-90);
            } else if (west && south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(180);
            } else if (north && west && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
            } else if (north && south) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.1.png"));
            } else if (east && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.1.png"));
                imageView.setRotate(90);
            } else if (north && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
            } else if (south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(90);
            } else if (south && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(180);
            } else if (north && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(-90);
            } else if (north) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
            } else if (east) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(90);
            } else if (south) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(180);
            } else {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(-90);
            }
        } else {
            imageView.setImage(new Image(ICONS_LOC + "Unknown.png"));
        }
        imageView.setFitHeight(81);
        imageView.setPreserveRatio(true);
    }

    public static void modifyContentCell(Location location, ImageView imageView, boolean readVisited) {
        if (location.isVisited() || !readVisited) {
            if (location.getContent() != null) {
                if (location.getContent().isEnemy()) {
                    imageView.setImage(new Image(ICONS_LOC + "Enemy.png"));
                } else if (location.getContent().isTreasure()) {
                    imageView.setImage(new Image(ICONS_LOC + "Chest.png"));
                } else {
                    imageView.setImage(new Image(ICONS_LOC + "Portal.png"));
                }
            }
        }
        imageView.setFitHeight(81);
        imageView.setPreserveRatio(true);
    }
}
