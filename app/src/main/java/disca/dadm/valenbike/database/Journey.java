package disca.dadm.valenbike.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.internal.$Gson$Preconditions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "journey",
        foreignKeys = {@ForeignKey(entity = StationDb.class,
                                   parentColumns = "number",
                                   childColumns = "origin_station"),
                       @ForeignKey(entity = StationDb.class,
                                   parentColumns = "number",
                                   childColumns = "destination_station")
        })

public class Journey {

    //Autoincremental primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_ID")
    private long id;

    private int totalTime;

    private double totalCost;

    private Date date;

    private double distance;

    @ColumnInfo(name = "origin_station") private int origin;

    @ColumnInfo(name = "destination_station") private int destination;

    public Journey(int time, double cost, Date date, double dist, int ori, int dest) {
        this.totalTime = time;
        this.totalCost = cost;
        this.date = date;
        this.distance = dist;
        this.origin = ori;
        this.destination = dest;
    }

    public Journey() {}

    public long getId() { return this.id; }
    public void setId(long id) { this.id = id; }

    public int getTotalTime() { return this.totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public double getTotalCost() { return this.totalCost; }
    public void setTotalCost(double cost) { this.totalCost = cost; }

    public Date getDate() { return this.date; }
    public void setDate(Date date) { this.date = date; }

    public double getDistance() { return this.distance; }
    public void setDistance(double dist) { this.distance = dist; }

    public int getOrigin() { return this.origin; }
    public void setOrigin(int ori) { this.origin = ori; }

    public int getDestination() { return this.destination; }
    public void setDestination(int dest) { this.destination = dest; }

    public static Journey[] populateData() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Journey[] prepopulate = new Journey[]{
                new Journey(12, 3.22, dateFormat.parse("12/04/2020"), 2300.0, 22, 89),
                new Journey(5, 2.22, dateFormat.parse("22/02/2020"), 800.0, 96, 258),
                new Journey(30, 8.15, dateFormat.parse("03/03/2020"), 6500.0, 257, 113),
                new Journey(22, 6.76, dateFormat.parse("22/04/2020"), 4500.0, 102, 114)
            };

            return prepopulate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
