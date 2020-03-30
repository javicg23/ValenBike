package disca.dadm.valenbike.database;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journey",
        foreignKeys = {@ForeignKey(entity = Station.class,
                                   parentColumns = "name",
                                   childColumns = "origin_name"),
                       @ForeignKey(entity = Station.class,
                                   parentColumns = "name",
                                   childColumns = "destination_name")
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

    @ColumnInfo(name = "origin_name") private String origin;

    @ColumnInfo(name = "destination_name") private String destination;

    public Journey(int time, double cost, Date date, double dist, String ori, String dest) {
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

    public String getOrigin() { return this.origin; }
    public void setOrigin(String ori) { this.origin = ori; }

    public String getDestination() { return this.destination; }
    public void setDestination(String dest) { this.destination = dest; }
}