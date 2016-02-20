package de.weighttrend.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.weighttrend.models.Column;
import de.weighttrend.models.Settings;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Commons;

import java.lang.reflect.Field;

/**
 * Basisklasse für alle DAOs. Die Klasse bietet Hilfsfunktionen und
 * kümmert sich um das Erzeugen bzw. Erneuern der Datenbank.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    protected Context context;

    // Database Version
    private static final int DATABASE_VERSION = 17;

    // Database Name
    private static final String DATABASE_NAME = "WeightTrend";

    /**
     * Konstruktor
     *
     * @param context aktueller Android Context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }

    /**
     * Die Methode wird beim initialen Anlegen der Datenbank automatisch aufgerufen und
     * erzeugt alle Tabellen.
     *
     * @param sqLiteDatabase Zugriffsklasse für die Datenbank
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getCreateTableForPojo(Trend.class, Trend.TABLE_NAME));
        sqLiteDatabase.execSQL(getCreateTableForPojo(Settings.class, Settings.TABLE_NAME));

        insertDefaultSetting(sqLiteDatabase);
    }

    /**
     * Die Methode wird bei einer Erneuerung der Datenbank automatisch aufgerufen.
     * Eine Erneuerung kann über das Setzen einer neuen Version angetriggert werden.
     * <p/>
     * Die Methode löscht alle bestehenden Tabellen und legt diese neu an.
     *
     * @param sqLiteDatabase Zugriffsklasse für die Datenbank
     * @param i              alte Datenbank Version
     * @param i2             neue Datenbank Version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Trend.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE_NAME);

        sqLiteDatabase.execSQL(getCreateTableForPojo(Trend.class, Trend.TABLE_NAME));
        sqLiteDatabase.execSQL(getCreateTableForPojo(Settings.class, Settings.TABLE_NAME));

        insertDefaultSetting(sqLiteDatabase);
    }

    /**
     * Die Methode schließt eine Datenbank Verbindung.
     */
    protected void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();

        db = this.getWritableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Diese Methode erzeugt ein CREATE TABLE Statement
     * für eine übergebene Klasse.
     *
     * @param c Klasse für die eine Tabelle erzeugt werden soll.
     * @return CREATE TABLE Statement
     */
    private String getCreateTableForPojo(Class c, String tableName) {
        String query = "CREATE TABLE " + tableName + "(";

        for (Field field : c.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);

            if (column != null) {
                query = query + column.columnName() + " "
                        + column.columnType() + " "
                        + column.additionalInformations()
                        + ",";

            }
        }

        query = query.substring(0, query.length() - 1) + ")";

        return query;
    }

    /**
     * Diese Methode speichert die Default Einstellungen nach dem Erzeugen der Datenbank
     */
    private void insertDefaultSetting(SQLiteDatabase sqLiteDatabase) {
        try {
            ContentValues contentValues = new ContentValues();

            // Default Werte
            contentValues.put(Settings.COLUMN_IS_INIT_WEIGHT, 1);

            sqLiteDatabase.insert(Settings.TABLE_NAME, null, contentValues);

        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Fehler beim Anlegen der Default Einstellungen " + e.getMessage());
            e.printStackTrace();
        }

        Commons.installHelpVideo(context);
    }
}
