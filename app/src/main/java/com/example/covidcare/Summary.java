package com.example.covidcare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "summary _table")
public class Summary {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int unknown;
    private int infected;
    private int treated;

    public Summary(int unknown, int infected, int treated) {
        this.unknown = unknown;
        this.infected = infected;
        this.treated = treated;
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
}
