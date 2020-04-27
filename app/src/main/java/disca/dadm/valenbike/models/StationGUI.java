package disca.dadm.valenbike.models;

public class StationGUI extends Station {
    private boolean expanded;
    private boolean arrowDown;
    private boolean reminderCheck = false;
    private boolean favouriteCheck = false;

    public StationGUI(int number, String contractName, String name, String address, Position position, boolean banking, boolean bonus, int bikeStands, int availableBikeStands, int availableBikes, String status, long lastUpdate) {
        super(number, contractName, name, address, position, banking, bonus, bikeStands, availableBikeStands, availableBikes, status, lastUpdate);
        this.expanded = false;
        this.arrowDown = false;
    }

    public StationGUI() {
    }

    public StationGUI(int number, int favourite, int reminder) {
        super.setNumber(number);
        this.favouriteCheck = favourite == 1;
        this.reminderCheck = reminder == 1;
        this.expanded = false;
        this.arrowDown = false;
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

    public boolean isReminderCheck() {
        return reminderCheck;
    }

    public void setReminderCheck(boolean reminderCheck) {
        this.reminderCheck = reminderCheck;
    }

    public boolean isFavouriteCheck() {
        return favouriteCheck;
    }

    public void setFavouriteCheck(boolean favouriteCheck) {
        this.favouriteCheck = favouriteCheck;
    }
}
