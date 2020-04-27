package disca.dadm.valenbike.models;

import java.util.Comparator;

import static disca.dadm.valenbike.utils.Tools.cleanString;

public class Station {
    private int number;
    private String contract_name;
    private String name;
    private String address;
    private Position position;
    private boolean banking;
    private boolean bonus;
    private int bike_stands;
    private int available_bike_stands;
    private int available_bikes;
    private String status;
    private long last_update;

    public Station(int number, String contractName, String name, String address, Position position, boolean banking, boolean bonus, int bikeStands, int availableBikeStands, int availableBikes, String status, long lastUpdate) {
        this.number = number;
        this.contract_name = contractName;
        this.name = name;
        this.address = address;
        this.position = position;
        this.banking = banking;
        this.bonus = bonus;
        this.bike_stands = bikeStands;
        this.available_bike_stands = availableBikeStands;
        this.available_bikes = availableBikes;
        this.status = status;
        this.last_update = lastUpdate;
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
        return contract_name;
    }

    public void setContractName(String value) {
        this.contract_name = value;
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
        return bike_stands;
    }

    public void setBikeStands(int value) {
        this.bike_stands = value;
    }

    public int getAvailableBikeStands() {
        return available_bike_stands;
    }

    public void setAvailableBikeStands(int value) {
        this.available_bike_stands = value;
    }

    public int getAvailableBikes() {
        return available_bikes;
    }

    public void setAvailableBikes(int value) {
        this.available_bikes = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public long getLastUpdate() {
        return last_update;
    }

    public void setLastUpdate(long value) {
        this.last_update = value;
    }

    public boolean isActive() {
        return status.equals("OPEN");
    }

    public static Comparator<Station> numerically = new Comparator<Station>() {
        @Override
        public int compare(Station station1, Station station2) {
            return station1.getNumber() - station2.getNumber();
        }
    };

    public static Comparator<Station> alphabetically = new Comparator<Station>() {
        @Override
        public int compare(Station station1, Station station2) {
            String station1Clean = cleanString(station1.getAddress());
            String station2Clean = cleanString(station2.getAddress());
            return station1Clean.toLowerCase().compareTo(station2Clean.toLowerCase());
        }
    };


}


