package disca.dadm.valenbike.models;

public class Information {
    private String title;
    private String body;
    private boolean expanded;
    private boolean arrowDown;

    public Information(String title, String body) {
        this.title = title;
        this.body = body;
        this.expanded = false;
        this.arrowDown = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
