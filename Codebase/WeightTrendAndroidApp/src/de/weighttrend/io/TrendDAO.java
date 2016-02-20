package de.weighttrend.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import de.weighttrend.models.Trend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 05.04.14
 * Time: 08:41
 * DataAccess Objekt für die Trends
 */
public class TrendDAO extends DatabaseHelper {

    /**
     * Singleton Parameter
     */
    private static TrendDAO intervalDAO;

    /**
     * Konstruktor
     *
     * @param context aktueller Android Context
     */
    public TrendDAO(Context context) {
        super(context);
    }


    /**
     * Liefert die Singleton Instanz der Klasse
     *
     * @param context Context
     * @return Instanz der Klasse
     */
    public static TrendDAO getInstance(Context context) {
        if (intervalDAO == null) {
            intervalDAO = new TrendDAO(context);
        }

        return intervalDAO;
    }

    /**
     * Die Methode speichert ein Pojo
     *
     * @param trend Pojo
     */
    public void save(Trend trend) {

        try {

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.insert(Trend.TABLE_NAME, null, createContentValues(trend));

        } catch (Exception e) {

            Log.e(TrendDAO.class.getName(), "Fehler beim Speichern" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Speichern", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        } finally {
            closeDB();
        }
    }

    /**
     * Gibt alle in der Datenbank vorhandenen Trends zurück
     *
     * @return Liste mit Trends
     */
    public List<Trend> getAll() {

        List<Trend> responseList = new ArrayList<Trend>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  * FROM " + Trend.TABLE_NAME + " ORDER BY " + Trend.COLUMN_DATETIME + " DESC";

            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {

                do {
                    responseList.add(new Trend(c));

                } while (c.moveToNext());
            }

            c.close();

        } catch (Exception e) {
            Log.e(TrendDAO.class.getName(), "Fehler beim Lesen" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Lesen", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }

        return responseList;
    }

    /**
     * Gibt den aktuellen Trend zurück
     *
     * @return Liste mit Trends
     */
    public Trend getLastTrendWithWeight() {

        List<Trend> responseList = new ArrayList<Trend>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  * FROM " + Trend.TABLE_NAME + " WHERE " +
                     Trend.COLUMN_DATETIME + " = (" +
                    "SELECT  MAX(" + Trend.COLUMN_DATETIME + ") FROM " + Trend.TABLE_NAME +
                    " WHERE " + Trend.COLUMN_WEIGHT + " IS NOT NULL)";

            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {

                do {
                    responseList.add(new Trend(c));

                } while (c.moveToNext());
            }

            c.close();

        } catch (Exception e) {
            Log.e(TrendDAO.class.getName(), "Fehler beim Lesen des letzten Datensatzes" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Lesen des letzten Datensatzes", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }

        if(responseList.size()>0){
            return responseList.get(0);
        }

        return null;
    }

    /**
     * Gibt einen Trend zurück
     *
     * @return Liste mit Trends
     */
    public Trend getById(long id) {

        List<Trend> responseList = new ArrayList<Trend>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "SELECT  * FROM " + Trend.TABLE_NAME + " WHERE " + Trend.COLUMN_ID + " = " + id;

            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {

                do {
                    responseList.add(new Trend(c));

                } while (c.moveToNext());
            }

            c.deactivate();
            c.close();

        } catch (Exception e) {
            Log.e(TrendDAO.class.getName(), "Fehler beim Lesen eins Datensatzes" + e.getMessage());
            Toast.makeText(this.context, "Fehler beim Lesen eins Datensatzes", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }

        if(responseList.size()>0){
            return responseList.get(0);
        }

        return null;
    }


    /**
     * Löscht den übergebenen Trend in der Datenbank
     *
     * @param id Id des zu löschenden Objektes
     */
    public void delete(long id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String whereClause = Trend.COLUMN_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(id)};

            db.delete(Trend.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e(TrendDAO.class.getName(), "Fehler beim Löschen" + e.getMessage());

            Toast.makeText(this.context, "Fehler beim Löschen", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            closeDB();
        }
    }

    private ContentValues createContentValues(Trend trend) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Trend.COLUMN_USER_ID, trend.getUserId());
        contentValues.put(Trend.COLUMN_FACEBOOK_ID, trend.getFacebookId());
        contentValues.put(Trend.COLUMN_TWITTER_ID, trend.getTwitterId());
        contentValues.put(Trend.COLUMN_TEXT, trend.getText());
        contentValues.put(Trend.COLUMN_DETAIL_TEXT, trend.getDetailText());
        contentValues.put(Trend.COLUMN_TREND_TYPE, trend.getTrend().name());
        contentValues.put(Trend.COLUMN_DATETIME, trend.getDatetime().getTimeInMillis());
        contentValues.put(Trend.COLUMN_WEIGHT, trend.getWeight());

        return contentValues;
    }
}
