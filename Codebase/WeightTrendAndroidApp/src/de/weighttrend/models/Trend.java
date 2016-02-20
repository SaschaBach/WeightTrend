package de.weighttrend.models;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Settings;
import de.weighttrend.activities.R;
import de.weighttrend.util.Constants;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 03.04.14
 * Time: 21:09
 * Pojo für die Trends
 */
public class Trend {

    public final static String TABLE_NAME = "Trend";

    public final static String COLUMN_ID = "ID";
    public final static String COLUMN_USER_ID = "USER_ID";
    public final static String COLUMN_FACEBOOK_ID = "FACEBOOK_ID";
    public final static String COLUMN_TWITTER_ID = "TWITTER_ID";
    public final static String COLUMN_TEXT = "TEXT";
    public final static String COLUMN_DETAIL_TEXT = "DETAIL_TEXT";
    public final static String COLUMN_TREND_TYPE = "TREND_TYPE";
    public final static String COLUMN_DATETIME = "DATETIME";
    public final static String COLUMN_WEIGHT = "WEIGHT";

    // das Attribute wird aktuell nicht benötigt
    @Column(columnName = COLUMN_ID, columnType = "INTEGER", additionalInformations = "PRIMARY KEY AUTOINCREMENT NOT NULL")
    private int id;

    @Column(columnName = COLUMN_USER_ID, columnType = "TEXT", additionalInformations = "")
    private String userId;

    @Column(columnName = COLUMN_FACEBOOK_ID, columnType = "TEXT", additionalInformations = "")
    private String facebookId;

    @Column(columnName = COLUMN_TWITTER_ID, columnType = "TEXT", additionalInformations = "")
    private int twitterId;

    @Column(columnName = COLUMN_TEXT, columnType = "TEXT", additionalInformations = "")
    private String text;

    @Column(columnName = COLUMN_DETAIL_TEXT, columnType = "TEXT", additionalInformations = "")
    private String detailText;

    @Column(columnName = COLUMN_TREND_TYPE, columnType = "TEXT", additionalInformations = "")
    private TrendType trend;

    @Column(columnName = COLUMN_DATETIME, columnType = "INTEGER", additionalInformations = "")
    private Calendar datetime;

    @Column(columnName = COLUMN_WEIGHT, columnType = "TEXT", additionalInformations = "")
    private String weight;

    /**
     * Konstruktor für die Activity
     *
     * @param activity
     */
    public Trend(Activity activity){

        // die Device Id wird zur UserId
        this.userId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

        this.datetime = Calendar.getInstance();
    }

    /**
     * Konstruktor für das DAO
     *
     * @param c Database Cursor
     */
    public Trend(Cursor c){

        this.id = c.getInt(c.getColumnIndex(COLUMN_ID));
        this.userId = c.getString(c.getColumnIndex(COLUMN_USER_ID));
        this.facebookId = c.getString(c.getColumnIndex(COLUMN_FACEBOOK_ID));
        this.twitterId = c.getInt(c.getColumnIndex(COLUMN_TWITTER_ID));
        this.text = c.getString(c.getColumnIndex(COLUMN_TEXT));
        this.detailText = c.getString(c.getColumnIndex(COLUMN_DETAIL_TEXT));
        this.weight = c.getString(c.getColumnIndex(COLUMN_WEIGHT));

        long timeInMillis = c.getLong(c.getColumnIndex(COLUMN_DATETIME));
        this.datetime = Calendar.getInstance();
        this.datetime.setTimeInMillis(timeInMillis);

        String trendName = c.getString(c.getColumnIndex(COLUMN_TREND_TYPE));
        this.trend = TrendType.valueOf(trendName);

    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public int getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(int twitterId) {
        this.twitterId = twitterId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public TrendType getTrend() {
        return trend;
    }

    public void setTrend(TrendType trend) {
        this.trend = trend;
    }

    public Calendar getDatetime() {
        return datetime;
    }

    public void setDatetime(Calendar datetime) {
        this.datetime = datetime;
    }

    public enum TrendType{

        Up(R.drawable.arrow_up_active_icon, Constants.TEXT_TREND_UP, "+"),
        Down(R.drawable.arrow_down_active_icon, Constants.TEXT_TREND_DOWN, "-");

        private int pictureResourceId;
        private String text;
        private String symbol;

        private TrendType(int pictureResourceId, String description, String symbol){
            this.pictureResourceId = pictureResourceId;
            this.text = description;
            this.symbol = symbol;
        }

        public int getPictureResourceId() {
            return pictureResourceId;
        }

        public String getText() {
            return text;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
