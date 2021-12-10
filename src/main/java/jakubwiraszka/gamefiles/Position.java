package jakubwiraszka.gamefiles;

public class Position implements Comparable<Position> {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return ("[" + getX() + ";" + getY() + "]");
    }

    @Override
    public int compareTo(Position position) {
        if (getX() == position.getX() && getY() == position.getY()) {
            return 0;
        } else if ((getX() > position.getX() && getY() >= position.getY()) || (getX() >= position.getX() && getY() > position.getY())) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Position secondPosition = null;
        try {
            secondPosition = (Position) obj;
        } catch (ClassCastException e) {
            System.out.println("Can't compare objects");
        }
        if(secondPosition != null) {
            return getX() == secondPosition.getX() && getY() == secondPosition.getY();
        }
        return false;
    }
}
