package de.weighttrend.models;

import java.util.Calendar;

/**
 * Created by Sascha on 13.04.14.
 *
 * Modelklasse für die ausgelesenen Facebook Kommentare zum Trend.
 */
public class FacebookComment {

    String facebookId;
    String text;
    String userName;
    Calendar date;
    int commentCount;
    int likesCount;

    public FacebookComment(String facebookId, String text, String userName, Calendar date, int commentCount, int likesCount) {
        this.text = text;
        this.userName = userName;
        this.date = date;
        this.commentCount = commentCount;
        this.likesCount = likesCount;
        this.facebookId = facebookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
