package de.weighttrend.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.facebook.Session;
import de.weighttrend.controls.FacebookCommentListViewAdapter;
import de.weighttrend.io.FacebookHelper;
import de.weighttrend.io.TrendDAO;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Commons;
import de.weighttrend.util.Constants;

public class ShowTrendActivity extends FragmentActivity {

    private TextView datetimeTextView;
    private RelativeLayout mainLayout;
    private RelativeLayout actionBar;
    private TextView detailText;
    private TextView headerTextView;
    private ImageView trendImageView;
    private ListView facebookCommentsListView;
    private ImageView facebookImageView;
    private LinearLayout trendLinearLayout;
    private TextView textTextView;
    private TextView likesCountTextView;
    private TextView likeNamesTextView;

    private Trend trend;

     /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_trend);

        trendImageView = (ImageView) findViewById(R.id.trendImageView);
        headerTextView = (TextView) findViewById(R.id.headerTextView);
        detailText = (TextView) findViewById(R.id.detailText);
        actionBar = (RelativeLayout) findViewById(R.id.actionBar);
        mainLayout = (RelativeLayout) findViewById(R.id.showTrendMainLayout);
        datetimeTextView = (TextView) findViewById(R.id.datetimeTextView);
        facebookCommentsListView = (ListView) findViewById(R.id.facebookCommentsListView);
        facebookImageView = (ImageView) findViewById(R.id.facebookImageView);
        trendLinearLayout = (LinearLayout) findViewById(R.id.trendLinearLayout);
        likesCountTextView = (TextView) findViewById(R.id.likesCountTextView);
        likeNamesTextView = (TextView) findViewById(R.id.likeNamesTextView);
        textTextView = (TextView) findViewById(R.id.textTextView);

        int chosenTrendId = getIntent().getIntExtra(Constants.CHOSEN_TREND,0);

        trend = TrendDAO.getInstance(this).getById(chosenTrendId);

        int colorId = R.color.purple_background;

        if(trend.getWeight() != null){
            // In diesem Fall soll eine Gewichtsveränderung angezeigt werden.
            // die Default Background Einstellungen sind für den Trend
            mainLayout.setBackgroundResource(R.color.orange_background);

            trendLinearLayout.setVisibility(View.GONE);
            textTextView.setVisibility(View.VISIBLE);

            // Hack um die Padding zu behalten, dieser verliert man, wenn ein neuer Background gesetzt wird
            int pL = actionBar.getPaddingLeft();
            int pT = actionBar.getPaddingTop();
            int pR = actionBar.getPaddingRight();
            int pB = actionBar.getPaddingBottom();

            actionBar.setBackgroundResource(R.drawable.actionbar_background_orange);
            facebookCommentsListView.setBackgroundResource(R.color.orange_background);
            actionBar.setPadding(pL, pT, pR, pB);

            headerTextView.setText("Gewicht");

            textTextView.setText(trend.getText());

            colorId = R.color.orange_background;
        }

        // Init Controls
        datetimeTextView.setText(Commons.getFormattedDatetime(trend.getDatetime()));
        detailText.setText(trend.getDetailText());
        trendImageView.setImageResource(trend.getTrend().getPictureResourceId());

        FacebookCommentListViewAdapter adapter = new FacebookCommentListViewAdapter(this,
                R.layout.facebook_comment_list_item, colorId);

        facebookCommentsListView.setAdapter(adapter);

        if(trend.getFacebookId() != null){
            facebookImageView.setImageResource(R.drawable.facebook_button_enable);

            FacebookHelper.writeCommentsWithDetailsToPost(Session.getActiveSession(), trend.getFacebookId(), adapter);
            FacebookHelper.writeLikesCountAndLikeNamesToPost(Session.getActiveSession(), trend.getFacebookId(),
                    likesCountTextView, likeNamesTextView);
        }

        // Hack: um den glow effekt in der ListView zu umgehen. In API Version 8 nur so möglich
        int glowDrawableId = this.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        if(glowDrawableId > 0){
            Drawable androidGlow = this.getResources().getDrawable(glowDrawableId);
            androidGlow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        int edgeDrawableId = this.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        if(edgeDrawableId > 0){
            Drawable androidEdge = this.getResources().getDrawable(edgeDrawableId);
            androidEdge.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
