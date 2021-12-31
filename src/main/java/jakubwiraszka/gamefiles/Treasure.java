package jakubwiraszka.gamefiles;

public class Treasure implements LocationContent {
    private String id;

    private final String name;
    private final Content content;
    private Position position;

    public Treasure(String name, Content content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public Content getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public String save() {
        return (getName() + "\n"
                + getContent().getStatistic() + "\n"
                + getContent().getValue());
    }

    public static class Content {
        private String statistic;
        private int value;

        public Content(String statistic, int value) {
            this.statistic = statistic;
            this.value = value;
        }

        public String getStatistic() {
            return statistic;
        }

        public int getValue() {
            return value;
        }

        public void setStatistic(String statistic) {
            this.statistic = statistic;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Override
    public boolean isEnemy() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public String toString() {
        return (getId() + ": " + getContent().getStatistic() + " " + getContent().getValue());
    }
}

