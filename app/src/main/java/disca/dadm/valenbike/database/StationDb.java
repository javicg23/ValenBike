package disca.dadm.valenbike.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "station")
public class StationDb {

    @PrimaryKey
    @NonNull
    protected int number;


    @ColumnInfo(name = "is_favourite")
    protected boolean isFavourite;
    @ColumnInfo(name = "notify_bikes")
    protected boolean notifyBikes;


    public StationDb(int number, boolean favourite, boolean notifyBikes) {
        this.number = number;
        this.isFavourite = favourite;
        this.notifyBikes = notifyBikes;
    }

    public StationDb() {}

    public int getNumber() { return this.number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isFavourite() { return this.isFavourite; }
    public void setFavourite(boolean fav) { this.isFavourite = fav; }

    public boolean getNotifyBikes() { return this.notifyBikes; }
    public void setNotifyBikes(boolean notifyBikes) { this.notifyBikes = notifyBikes; }
}
