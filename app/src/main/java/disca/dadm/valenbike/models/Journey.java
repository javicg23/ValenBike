package disca.dadm.valenbike.models;

public class Journey {
    private int id;
    private String origin;
    private String destiny;
    private double distance;
    private double price;
    private int duration;
    private String date;

    public Journey() {

    }

    public Journey(int id, String origin, String destiny, double distance, double price, int duration, String date) {
        this.id = id;
        this.origin = origin;
        this.destiny = destiny;
        this.distance = distance;
        this.price = price;
        this.duration = duration;
        this.date = date;
    }

    public Journey(String origin, String destiny, double distance, double price, int duration, String date) {
        this.origin = origin;
        this.destiny = destiny;
        this.distance = distance;
        this.price = price;
        this.duration = duration;
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
