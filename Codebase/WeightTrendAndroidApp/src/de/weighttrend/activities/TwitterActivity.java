package de.weighttrend.activities;

import android.app.Activity;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 03.04.14
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class TwitterActivity extends Activity {

    /*private String consumerKey="U68MHs8ttq7yc7XwjHcYA";
    private String consumerSecret="JIzuJDI3PYZmY5juW5rdKXAfBZusYJuWfkQz2sTzQc";
    private RequestToken rToken;
    public String TAG="inApplication";
    public String CALLBACK_URL="Twitter";
    public Twitter twitter;
    private Button loginButton;

    *//** Called when the activity is first created. *//*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weight_chooser);
        this.loginButton=(Button) this.findViewById(R.id.saveButton);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginButtonClick();
                } catch (ClientProtocolException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    public void loginButtonClick() throws ClientProtocolException, IOException, JSONException{
        twitter = new TwitterFactory().getInstance();
        try
        {
            twitter.setOAuthConsumer(consumerKey, consumerSecret);
            String callbackURL = "Twitter://host";
//AccessToken at = new AccessToken(accessToken, accessSecret);
//twitter.setOAuthAccessToken(at);
            rToken= twitter.getOAuthRequestToken(callbackURL);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rToken.getAuthenticationURL())));
        }
        catch(IllegalStateException e)
        {
// access token is already available, or consumer key/secret is not set.
            if(!twitter.getAuthorization().isEnabled()){
                System.out.println("OAuth consumer key/secret is not set.");
                System.exit(-1);
            }
            e.printStackTrace();
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "Network Host not responding", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "New Intent Arrived");
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) { // If the user has just logged in
            String oauthVerifier = uri.getQueryParameter("oauth_verifier");
            Log.i(TAG, oauthVerifier);
            AccessToken at=null;
            try {
                at = twitter.getOAuthAccessToken(rToken, oauthVerifier);
            } catch (TwitterException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            twitter.setOAuthAccessToken(at);
*//*try {
twitter.updateStatus("Testing my first ever android app!");
} catch (TwitterException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}*//*
//authoriseNewUser(oauthVerifier);
            Intent i = new Intent(this,Activity.class);
            i.putExtra("twitter", twitter);
            startActivity(i);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Arrived at onResume");
    }
*/

}