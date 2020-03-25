package disca.dadm.valenbike.database;

import androidx.room.TypeConverter;

import java.util.Date;

/*
    This class converts complex data types to types that are supported by Room.
 */
public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
