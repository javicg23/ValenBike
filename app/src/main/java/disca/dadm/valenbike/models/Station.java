package disca.dadm.valenbike.models;

public class Station extends disca.dadm.valenbike.database.Station{
    private String title;
    private boolean expanded;
    private boolean arrowDown;

    public Station(String title) {
        super();
        this.title = this.name;
        this.expanded = false;
        this.arrowDown = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isArrowDown() {
        return arrowDown;
    }

    public void setArrowDown(boolean arrowDown) {
        this.arrowDown = arrowDown;
    }
}
