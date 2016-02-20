package de.weighttrend.io;

import de.weighttrend.models.Trend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 03.04.14
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class WebserviceHelper {

    public static void update(Trend trend) {

        Map<String,Object> params = new LinkedHashMap();
        params.put("time", trend.getDatetime().getTimeInMillis());
        params.put("userID", "sascha");
        params.put("action", "update");
        params.put("facebookPostID", trend.getFacebookId());

        sendPost(params);
    }
    
    public static void insert(Trend trend){

        Map<String,Object> params = new LinkedHashMap();
        params.put("name", trend.getText());
        params.put("time", trend.getDatetime().getTimeInMillis());
        params.put("text", trend.getDetailText());
        params.put("type", trend.getTrend().name());
        params.put("weight", trend.getWeight());
        params.put("facebookPostID", trend.getFacebookId());
        params.put("twitterPostID", trend.getWeight());
        params.put("userID", trend.getWeight());
        params.put("action", "write");

        sendPost(params);
    }

    public static void delete(Trend trend){

        Map<String,Object> params = new LinkedHashMap();
        params.put("time", trend.getDatetime().getTimeInMillis());
        params.put("userID", trend.getWeight());
        params.put("action", "delete");

        sendPost(params);
    }
    
    private static void sendPost(final Map<String,Object> params){
        
        Thread sendThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    URL url = new URL("http://basti-fritzsche.de/httpdocs/weightTrend/db.php");

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        sendThread.start();
    }
}
