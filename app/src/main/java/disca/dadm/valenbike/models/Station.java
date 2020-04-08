package disca.dadm.valenbike.models;

public class Station {
    private long number;
    private String contractName;
    private String name;
    private String address;
    private Position position;
    private boolean banking;
    private boolean bonus;
    private long bikeStands;
    private long availableBikeStands;
    private long availableBikes;
    private String status;
    private long lastUpdate;

    public Station(long number, String contractName, String name, String address, Position position, boolean banking, boolean bonus, long bikeStands, long availableBikeStands, long availableBikes, String status, long lastUpdate) {
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long value) {
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

    public long getBikeStands() {
        return bikeStands;
    }

    public void setBikeStands(long value) {
        this.bikeStands = value;
    }

    public long getAvailableBikeStands() {
        return availableBikeStands;
    }

    public void setAvailableBikeStands(long value) {
        this.availableBikeStands = value;
    }

    public long getAvailableBikes() {
        return availableBikes;
    }

    public void setAvailableBikes(long value) {
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


    public class Position {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double value) {
            this.lat = value;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double value) {
            this.lng = value;
        }
    }
}