package jakubwiraszka.gamefiles;

public class ItemContent {
    private String statistic;
    private int value;

    public ItemContent(String statistic, int value) {
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
