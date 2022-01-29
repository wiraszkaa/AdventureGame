package jakubwiraszka.map;

import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.gamefiles.LocationContent;
import jakubwiraszka.gamefiles.Position;
import jakubwiraszka.gamefiles.World;
import jakubwiraszka.observable.Listener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;


public class GameMapBuilder implements Listener {
    private final GridPane gameMapGridPane;
    private final GridPane contentMapGridPane;
    private final GridPane playerMapGridPane;
    private final World world;
    private final ArrayList<GameMapCell> gameMapCells;
    private Position playerPosition;

    public final static String ICONS_LOC = "src\\main\\resources\\graphics\\";

    public GameMapBuilder(World world) {
        this.world = world;
        this.gameMapCells = new ArrayList<>();
        gameMapGridPane = new GridPane();
        gameMapGridPane.setAlignment(Pos.CENTER);
        contentMapGridPane = new GridPane();
        contentMapGridPane.setAlignment(Pos.CENTER);
        playerMapGridPane = new GridPane();
        playerMapGridPane.setAlignment(Pos.CENTER);
        playerPosition = world.getHero().getPosition();
    }

    public static String getImageUrl(String name) {
        return new File(ICONS_LOC + name).toURI().toString();
    }

    private void createMap(boolean readVisited) {
        for(int i = 0; i <= world.getWidth(); i++) {
            for(int j = 0; j <= world.getHeight(); j++) {
                Location location = world.findLocation(new Position(i, j), world.getLocations());
                ImageView contentImageView = new ImageView();
                contentImageView.setImage(new Image(getImageUrl("Nothing.png")));
                contentImageView.setFitHeight(81);
                contentImageView.setPreserveRatio(true);
                contentMapGridPane.add(contentImageView, i, j);
                ImageView playerImageView = new ImageView();
                playerImageView.setImage(new Image(getImageUrl("Nothing.png")));
                playerImageView.setFitHeight(81);
                playerImageView.setPreserveRatio(true);
                playerMapGridPane.add(playerImageView, i, j);
                GameMapCell gameMapCell = new GameMapCell(new Position(i, j));
                if(location != null) {
                    modifyContentCell(location, getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()), readVisited);
                    if(location.isVisited() || !readVisited) {
                        gameMapCell = new GameMapCell(location);
                    }
                }
                gameMapGridPane.add(gameMapCell.getGameMapCell(), i, j);
                gameMapCells.add(gameMapCell);
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

    public static void modifyContentCell(Location location, ImageView imageView, boolean readVisited) {
        if (location.isVisited() || !readVisited) {
            if (location.getContent() != null) {
                if (location.getContent().isEnemy()) {
                    imageView.setImage(new Image(getImageUrl("Enemy.png")));
                } else if (location.getContent().isTreasure()) {
                    imageView.setImage(new Image(getImageUrl("Chest.png")));
                } else if (location.getContent().isItem()) {
                    imageView.setImage(new Image(getImageUrl("Loot.png")));
                } else {
                    imageView.setImage(new Image(getImageUrl("Portal.png")));
                }
            } else {
                imageView.setImage(new Image(getImageUrl("Nothing.png")));
            }
        }
        imageView.setFitHeight(81);
        imageView.setPreserveRatio(true);
    }

    private void handleNearLocations(Location location) {
        updateGameMapCell(location);
        for(Location i: world.getNearLocations(location)) {
            updateGameMapCell(i);
        }
    }

    private void updateGameMapCell(Location location) {
        if(location != null) {
            GameMapCell gameMapCell = findGameMapCell(location.getPosition());
            if (gameMapCell != null) {
                gameMapCell.update(location.getExits());
            }
        }
    }

    public ArrayList<GridPane> createGameMap(boolean readVisited) {
        createMap(readVisited);
        if(!readVisited) {
            world.getLocations().addListener((ListChangeListener<Location>) change -> {
                while (change.next()) {
                    for(Location i: change.getAddedSubList()) {
                        handleNearLocations(i);
                    }
                    for(Location i: change.getRemoved()) {
                        handleNearLocations(i);
                    }
                }
            });
        } else {
            world.getVisitedLocations().addListener((ListChangeListener<Position>) change -> {
                while(change.next()) {
                    for (Position visitedPosition : change.getAddedSubList()) {
                        Location location = world.findLocation(visitedPosition, world.getLocations());
                        updateGameMapCell(location);
                        if(location.getContent() != null) {
                            modifyContentCell(location, getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()), true);
                        }
                    }
                }
            });
        }
        contentListChangeListener(world.getEnemies());
        contentListChangeListener(world.getTreasures());
        ArrayList<GridPane> gridPanes = new ArrayList<>();
        gridPanes.add(gameMapGridPane);
        gridPanes.add(contentMapGridPane);
        gridPanes.add(playerMapGridPane);
        return gridPanes;
    }

    private void contentListChangeListener(ObservableList<? extends LocationContent> list) {
        list.addListener((ListChangeListener<LocationContent>) change -> {
            while(change.next()) {
                for(LocationContent content: change.getRemoved()) {
                    Location location = world.findLocationByContent(content.getId());
                    ImageView imageView = getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY());
                    if(imageView != null) {
                        imageView.setImage(new Image(getImageUrl("Nothing.png")));
                    }
                }
            }
        });
    }

    private GameMapCell findGameMapCell(Position position) {
        for(GameMapCell i: gameMapCells) {
            if(i.getPosition().equals(position)) {
                return i;
            }
        }
        return null;
    }

    public void setPlayerPosition(Position playerPosition) {
        ImageView imageView = getImageView(playerMapGridPane, this.playerPosition.getX(), this.playerPosition.getY());
        if(imageView != null) {
            imageView.setImage(new Image(getImageUrl("Nothing.png")));
        }
        this.playerPosition = playerPosition;
        ImageView imageView1 = getImageView(playerMapGridPane, this.playerPosition.getX(), this.playerPosition.getY());
        if(imageView1 != null) {
            imageView1.setImage(new Image(getImageUrl("Player.png")));
        }
    }

    @Override
    public void update(double value1, double value2, int value3, int value4, Location location) {
        ImageView imageView = getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY());
        if(location.getContent() != null) {
            if(imageView != null) {
                imageView.setImage(new Image(getImageUrl("Question.png")));
            }
        } else {
            if(imageView != null) {
                imageView.setImage(new Image(getImageUrl("Nothing.png")));
            }
        }
    }
}
