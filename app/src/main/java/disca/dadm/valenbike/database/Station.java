package disca.dadm.valenbike.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "station")
public class Station {

    @PrimaryKey
    @NonNull
    protected String name;

    protected String address;

    @ColumnInfo(name = "num_free_bikes")
    protected int numFreeBikes;

    @ColumnInfo(name = "num_free_gaps")
    protected int numFreeGaps;

    @ColumnInfo(name = "is_favourite")
    protected boolean isFavourite;

    protected boolean notify;

    public Station(String name, String address, int numBikes, int numGaps, boolean favourite, boolean notify) {
        this.name = name;
        this.address = address;
        this.numFreeBikes = numBikes;
        this.numFreeGaps = numGaps;
        this.isFavourite = favourite;
        this.notify = notify;
    }

    public Station() {}

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return this.address; }
    public void setAddress(String address) { this.address = address; }

    public int getNumFreeBikes() { return this.numFreeBikes; }
    public void setNumFreeBikes(int numBikes) { this.numFreeBikes = numBikes; }

    public int getNumFreeGaps() { return this.numFreeGaps; }
    public void setNumFreeGaps(int numGaps) { this.numFreeGaps = numGaps; }

    public boolean isFavourite() { return this.isFavourite; }
    public void setFavourite(boolean fav) { this.isFavourite = fav; }

    public boolean getNotify() { return this.notify; }
    public void setNotify(boolean notify) { this.notify = notify; }
}
