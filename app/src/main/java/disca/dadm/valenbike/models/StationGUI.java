package disca.dadm.valenbike.models;

public class StationGUI extends Station {
    private boolean expanded;
    private boolean arrowDown;

    public StationGUI(int number, String contractName, String name, String address, Position position, boolean banking, boolean bonus, int bikeStands, int availableBikeStands, int availableBikes, String status, long lastUpdate) {
        super(number, contractName, name, address, position, banking, bonus, bikeStands, availableBikeStands, availableBikes, status, lastUpdate);
        this.expanded = false;
        this.arrowDown = false;
    }

    public StationGUI() {}


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
