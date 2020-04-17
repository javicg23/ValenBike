package disca.dadm.valenbike.models;

public class Station {
    private int number;
    private String contractName;
    private String name;
    private String address;
    private Position position;
    private boolean banking;
    private boolean bonus;
    private int bikeStands;
    private int availableBikeStands;
    private int availableBikes;
    private String status;
    private long lastUpdate;

    public Station(int number, String contractName, String name, String address, Position position, boolean banking, boolean bonus, int bikeStands, int availableBikeStands, int availableBikes, String status, long lastUpdate) {
        this.number = number;
        this.contractName = contractName;
        this.name = name;
        this.address = address;
        this.position = position;
        this.banking = banking;
        this.bonus = bonus;
        this.bikeStands = bikeStands;
        this.availableBikeStands = availableBikeStands;
        this.availableBikes = availableBikes;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public Station() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int value) {
        this.number = value;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String value) {
        this.contractName = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String value) {
        this.address = value;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position value) {
        this.position = value;
    }

    public boolean getBanking() {
        return banking;
    }

    public void setBanking(boolean value) {
        this.banking = value;
    }

    public boolean getBonus() {
        return bonus;
    }

    public void setBonus(boolean value) {
        this.bonus = value;
    }

    public int getBikeStands() {
        return bikeStands;
    }

    public void setBikeStands(int value) {
        this.bikeStands = value;
    }

    public int getAvailableBikeStands() {
        return availableBikeStands;
    }

    public void setAvailableBikeStands(int value) {
        this.availableBikeStands = value;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public void setAvailableBikes(int value) {
        this.availableBikes = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long value) {
        this.lastUpdate = value;
    }

    public boolean isActive() {
        return status.equals("OPEN");
    }


}


