package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "summary _table")
public class Summary {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int unknown;
    private int infected;
    private int treated;
    private  int healthy;

    public Summary(int unknown, int infected, int treated, int healthy) {
        this.unknown = unknown;
        this.infected = infected;
        this.treated = treated;
        this.healthy = healthy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUnknown() {
        return unknown;
    }

    public int getInfected() {
        return infected;
    }

    public int getTreated() {
        return treated;
    }

    public int getHealthy() {
        return healthy;
    }
}
