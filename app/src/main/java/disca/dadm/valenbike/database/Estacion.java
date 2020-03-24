package disca.dadm.valenbike.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "estacion")
public class Estacion {

    @PrimaryKey
    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "direccion")
    private String direccion;

    @ColumnInfo(name = "numBicisDispo")
    private int numBicisDisponibles;

    @ColumnInfo(name = "numHuecosDispo")
    private int numHuecosDisponibles;

    @ColumnInfo(name = "favorita")
    private boolean esFavorita;

    @ColumnInfo(name = "notificar")
    private boolean notificar;

    public Estacion(String nombre, String dir, int numBicis, int numHuecos, boolean favorita, boolean notificar) {
        this.nombre = nombre;
        this.direccion = dir;
        this.numBicisDisponibles = numBicis;
        this.numHuecosDisponibles = numHuecos;
        this.esFavorita = favorita;
        this.notificar = notificar;
    }

    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return this.direccion; }
    public void setDireccion(String nombre) { this.direccion = direccion; }

    public int getNumBicisDisponibles() { return this.numBicisDisponibles; }
    public void setNumBicisDisponibles(int numBicis) { this.numBicisDisponibles = numBicis; }

    public int getNumHuecosDisponibles() { return this.numHuecosDisponibles; }
    public void setNumHuecosDisponibles(int numHuecos) { this.numHuecosDisponibles = numHuecos; }

    public boolean esFavorita() { return this.esFavorita; }
    public void setFavorita(boolean fav) { this.esFavorita = fav; }

    public boolean getNotificar() { return this.notificar; }
    public void setNotificar(boolean notificar) { this.notificar = notificar; }
}
