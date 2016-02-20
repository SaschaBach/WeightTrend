package de.weighttrend.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import de.weighttrend.models.Settings;

/**
 * Datenzugriffsklasse für die Einstellungen.
 *
 * Die Einstellungen werden beim ersten Start der Anwendung mit Default Werten in die
 * Datenbank geschrieben. Anschließend erfolgen nur noch Updates auf diesen Datensatz.
 * *
 * Created by Sascha on 12.04.14.
 */
public class SettingsDAO extends DatabaseHelper {

    /**
     * Hält die Instanz der Klasse
     */
    private static SettingsDAO settingsDAO;

    /**
     * Konstruktor
     *
     * @param context aktueller Android Context
     */
    private SettingsDAO(Context context) {
        super(context);
    }

    public static SettingsDAO getInstance(Context context) {
        if (settingsDAO == null) {
            settingsDAO = new SettingsDAO(context);
        }

        return settingsDAO;
    }

    /**
     * Methode zum aktualisieren der Einstellungen
     *
     * @param settings veränderte Einstellungen
     */
    public void update(Settings settings) {

        try {
            // Update
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            String whereClause = Settings.COLUMN_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(settings.getId())};

            sqLiteDatabase.update(Settings.TABLE_NAME, createContentValues(settings), whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(SettingsDAO.class.getName(), "Fehler beim Lesen der Einstellungen" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Speichern der Einstellungen", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }
    }

    /**
     * Gibt die Einstellungen zurück
     *
     * @return Einstellungen
     */
    public Settings get() {

        Settings settings = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  * FROM " + Settings.TABLE_NAME;

            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {

                do {
                    settings = new Settings();

                    settings.setId(c.getInt(c.getColumnIndex(Settings.COLUMN_ID)));

                    int initWeight = c.getInt(c.getColumnIndex(Settings.COLUMN_IS_INIT_WEIGHT));
                    settings.setInitWeight((initWeight == 1));

                } while (c.moveToNext());
            }

            c.deactivate();
            c.close();

        } catch (Exception e) {
            Log.e(SettingsDAO.class.getName(), "Fehler beim Lesen der Einstellungen" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Lesen der Einstellungen", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }

        return settings;
    }


    private ContentValues createContentValues(Settings settings) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Settings.COLUMN_IS_INIT_WEIGHT, settings.isInitWeight() ? 1 : 0);

        return contentValues;
    }
}
