package disca.dadm.valenbike.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "viaje")
public class Viaje {

    //Autoincremental primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_ID")
    private long id;

    @ColumnInfo(name = "tiempoTotal")
    private int tiempoTotal;

    @ColumnInfo(name = "costeTotal")
    private double costeTotal;

    @ColumnInfo(name = "fecha")
    private Date fecha;

    @ColumnInfo(name = "distancia")
    private double distancia;

    @ColumnInfo(name = "origen")
    private Estacion origen;

    @ColumnInfo(name = "destino")
    private Estacion destino;

    public Viaje(int tiempo, double coste, Date fecha, double dist, Estacion ori, Estacion dest) {
        this.tiempoTotal = tiempo;
        this.costeTotal = coste;
        this.fecha = fecha;
        this.distancia = dist;
        this.origen = ori;
        this.destino = dest;
    }

    public long getId() { return this.id; }
    public void setNombre(long id) { this.id = id; }

    public int getTiempoTotal() { return this.tiempoTotal; }
    public void setTiempoTotal(int tiempo) { this.tiempoTotal = tiempo; }

    public double getCosteTotal() { return this.costeTotal; }
    public void setCosteTotal(double coste) { this.costeTotal = coste; }

    public Date getFecha() { return this.fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getDistancia() { return this.distancia; }
    public void setDistancia(double dist) { this.distancia = dist; }

    public Estacion getOrigen() { return this.origen; }
    public void setOrigen(Estacion ori) { this.origen = ori; }

    public Estacion getDestino() { return this.destino; }
    public void setDestino(Estacion dest) { this.destino = dest; }
}
