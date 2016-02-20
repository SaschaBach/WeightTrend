package de.weighttrend.models;

/**
 *
 * Diese Entität hält die Einstellungen der App
 *
 * Created by Sascha on 12.04.14.
 */
public class Settings {

    public final static String TABLE_NAME = "Settings";
    public final static String COLUMN_ID = "ID";
    public final static String COLUMN_IS_INIT_WEIGHT = "COLUMN_IS_INIT_WEIGHT";

    /**
     * Hält die Id
     */
    @Column(columnName = COLUMN_ID, columnType = "INTEGER", additionalInformations = "PRIMARY KEY AUTOINCREMENT NOT NULL")
    private int id;

    /**
     * Gibt an, ob das initiale Gewicht angegeben wurde.
     */
    @Column(columnName = COLUMN_IS_INIT_WEIGHT, columnType = "INTEGER", additionalInformations = "")
    private boolean isInitWeight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInitWeight() {
        return isInitWeight;
    }

    public void setInitWeight(boolean isInitWeight) {
        this.isInitWeight = isInitWeight;
    }
}