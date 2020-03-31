package disca.dadm.valenbike.models;

public class History {
    private String date;
    private String time;
    private String origin;
    private String destination;
    private String distance;
    private String money;

    public History (String date, String time, String origin, String destination, String distance, String money) {
        this.date = date;
        this.time = time;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.money = money;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMoney() {
        return money;
    }
    public void setMoney(String money) {
        this.money = money;
    }
}

