package de.weighttrend.io;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import de.weighttrend.controls.FacebookCommentListViewAdapter;
import de.weighttrend.models.FacebookComment;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 05.04.14
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
public class FacebookHelper {

    public static void sendToWeightTrendFacebookGroup(Session session, Trend trend){

        if(session != null && session.isOpened()){

            // erstelle den typenspezifischen Prefix
            String prefix;

            if(trend.getWeight() == null){
                prefix = Constants.FB_POST_TREND_PREFIX;
            } else {
                prefix = Constants.FB_POST_WEIGHT_PREFIX;
            }

            String text = Constants.FB_POST_PREFIX + prefix + trend.getText() + ": " + trend.getDetailText();

            JSONObject json = new JSONObject();
            try {

                json.put("message", text);
                json.put("link", Constants.FB_POST_LINK);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(FacebookHelper.class.getName(), e.getMessage());
            }

            Request req = Request.newPostRequest(session, "weightTrend/feed", GraphObject.Factory.create(json), null);
            Response response = Request.executeAndWait(req);

            if (response.getError() != null) {
                Log.e(FacebookHelper.class.getName(), "Fehler beim Absenden des Post");
            } else {
                trend.setFacebookId(response.getGraphObject().getProperty("id").toString());

                Log.d(FacebookHelper.class.getName(), "Post erfolgreich gesendet");
            }
        }
    }

    public static Trend deletePost(Session session, Trend trend){

        if(session != null && session.isOpened()){

            Request req = Request.newDeleteObjectRequest(session, trend.getFacebookId(), new Request.Callback() {
                @Override
                public void onCompleted(Response response) {

                    if (response.getError() != null) {
                        Log.e(FacebookHelper.class.getName(), "Fehler beim Löschen des Post");
                    } else {
                        Log.d(FacebookHelper.class.getName(), "Post erfolgreich gelöscht");
                    }
                }
            });

            Request.executeBatchAsync(req);

            return trend;
        }

        return null;
    }

    public static void writeCommentsWithDetailsToPost(Session session, final String facebookId, final FacebookCommentListViewAdapter adapter){

        if(session != null && session.isOpened()){

            Bundle parameters = new Bundle();
            parameters.putString("fields", "comments.fields(like_count,comment_count,from,message,created_time)");

            // Erstelle die Anfrage Id - irgendwie ging es mal nur so:
            // String requestId = facebookId.substring(facebookId.indexOf("_") + 1,facebookId.length());

            Request req = new Request(session, facebookId, parameters, HttpMethod.GET, new Request.Callback() {

                @Override
                public void onCompleted(Response response) {

                    if (response.getError() != null) {
                        Log.e(FacebookHelper.class.getName(), "Fehler beim auslesen der Kommentare");
                    } else {
                        Log.d(FacebookHelper.class.getName(), "Kommentare und Likes erfolgreich ausgelesen");

                        List<FacebookComment> result = new ArrayList<FacebookComment>();

                        JSONObject data = response.getGraphObject().getInnerJSONObject();

                        try {
                            JSONArray comments = data.getJSONObject(Constants.FB_JSON_RESPONSE_COMMENTS)
                                    .getJSONArray(Constants.FB_JSON_RESPONSE_INNER_DATA);

                            for(int i = 0; i < comments.length(); i++){

                                // 2014-04-13T13:39:56+0000
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
                                Calendar date = Calendar.getInstance();
                                date.setTime(dateFormat.parse(comments.getJSONObject(i).getString(Constants.FB_JSON_RESPONSE_DATE)));

                                result.add(new FacebookComment(facebookId,
                                        comments.getJSONObject(i).getString(Constants.FB_JSON_RESPONSE_MESSAGE),
                                        comments.getJSONObject(i).getJSONObject(Constants.FB_JSON_RESPONSE_FROM)
                                                .getString(Constants.FB_JSON_RESPONSE_NAME),
                                        date,
                                        comments.getJSONObject(i).getInt(Constants.FB_JSON_RESPONSE_COMMENT_COUNT),
                                        comments.getJSONObject(i).getInt(Constants.FB_JSON_RESPONSE_LIKE_COUNT)
                                        ));
                            }

                            adapter.updateList(result);

                        } catch (JSONException e) {
                            Log.d(FacebookHelper.class.getName(), "Fehler beim auslesen des Facebook Response");
                            e.printStackTrace();
                        }

                        catch (ParseException e) {
                            Log.d(FacebookHelper.class.getName(), "Fehler beim auslesen des Facebook Response");
                            e.printStackTrace();
                        }
                    }
                }
            });

            Request.executeBatchAsync(req);
        }
    }


    public static void writeCommentsAndLikesCountToPost(Session session, String facebookId,
                                                        final TextView commentCountTextView,
                                                        final TextView likeCountTextView) {

        if (session != null && session.isOpened()) {

            Bundle parameters = new Bundle();
            parameters.putString("fields", "comments,likes");

            // Erstelle die Anfrage Id - irgendwie ging es mal nur so:
            // String requestId = facebookId.substring(facebookId.indexOf("_") + 1,facebookId.length());

            Request req = new Request(session, facebookId, parameters, HttpMethod.GET, new Request.Callback() {

                @Override
                public void onCompleted(Response response) {

                    if (response.getError() != null) {
                        Log.e(FacebookHelper.class.getName(), "Fehler beim auslesen der Kommentare und Likes");
                    } else {
                        Log.d(FacebookHelper.class.getName(), "Kommentare und Likes erfolgreich ausgelesen");
                        JSONObject data = response.getGraphObject().getInnerJSONObject();

                        try {
                            JSONArray comments = data.getJSONObject(Constants.FB_JSON_RESPONSE_COMMENTS)
                                    .getJSONArray(Constants.FB_JSON_RESPONSE_INNER_DATA);

                            int commentCount = comments.length();

                            commentCountTextView.setText(String.valueOf(commentCount));

                        } catch (JSONException e) {
                            Log.d(FacebookHelper.class.getName(), "Fehler beim auslesen des Facebook Response");
                            e.printStackTrace();
                        }

                        try {
                            JSONArray likes = data.getJSONObject(Constants.FB_JSON_RESPONSE_LIKES)
                                    .getJSONArray(Constants.FB_JSON_RESPONSE_INNER_DATA);

                            int likesCount = likes.length();

                            likeCountTextView.setText(String.valueOf(likesCount));

                        } catch (JSONException e) {
                            Log.d(FacebookHelper.class.getName(), "Fehler beim auslesen des Facebook Response");
                            e.printStackTrace();
                        }
                    }
                }
            });

            Request.executeBatchAsync(req);
        }
    }

    public static Intent createIntentToOpenPost(Context context, String id){

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://post/"+id));
        } catch (Exception e) {
            Log.d(FacebookHelper.class.getSimpleName(), "Facebook App isnt installed. Open WeightTrend Page via Browser");
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/weightTrend"));
        }
    }

    public static void writeLikesCountAndLikeNamesToPost(Session session, String facebookId,
                                                        final TextView likesCountTextView,
                                                        final TextView likeNamesTextView){

        if(session != null && session.isOpened()){

            Bundle parameters = new Bundle();
            parameters.putString("fields", "likes");

            // Erstelle die Anfrage Id - irgendwie ging es mal nur so:
            // String requestId = facebookId.substring(facebookId.indexOf("_") + 1,facebookId.length());

            Request req = new Request(session, facebookId, parameters, HttpMethod.GET, new Request.Callback() {

                @Override
                public void onCompleted(Response response) {

                    if (response.getError() != null) {
                        Log.e(FacebookHelper.class.getName(), "Fehler beim auslesen der Kommentare und Likes");
                    } else {
                        Log.d(FacebookHelper.class.getName(), "Kommentare und Likes erfolgreich ausgelesen");
                        JSONObject data = response.getGraphObject().getInnerJSONObject();

                        try {
                            JSONArray likes = data.getJSONObject(Constants.FB_JSON_RESPONSE_LIKES)
                                    .getJSONArray(Constants.FB_JSON_RESPONSE_INNER_DATA);

                            StringBuilder stringBuilder = new StringBuilder();

                            for(int i = 0; i < likes.length(); i++){
                                stringBuilder.append(likes.getJSONObject(i).getString(Constants.FB_JSON_RESPONSE_NAME));

                                if(i < likes.length() - 1){
                                    stringBuilder.append(", ");
                                }
                            }

                            likesCountTextView.setText(String.valueOf(likes.length()));
                            likeNamesTextView.setText(String.valueOf(stringBuilder.toString()));

                        } catch (JSONException e) {
                            Log.d(FacebookHelper.class.getName(), "Fehler beim auslesen des Facebook Response");
                            e.printStackTrace();
                        }
                    }
                }
            });

            Request.executeBatchAsync(req);
        }
    }
}
