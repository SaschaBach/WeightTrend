package de.weighttrend.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import de.weighttrend.activities.R;
import de.weighttrend.activities.ShowTrendActivity;
import de.weighttrend.activities.TrendActivity;
import de.weighttrend.activities.WeightChooserActivity;
import de.weighttrend.controls.TrendListViewAdapter;
import de.weighttrend.io.FacebookHelper;
import de.weighttrend.io.TrendDAO;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Commons;
import de.weighttrend.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sascha on 13.04.14.
 *
 * Fragment zur Main Activity.
 * Das Fragment wird benötigt um die Facebook Session zu erstellen.
 */
public class MainFragment extends Fragment {

    private UiLifecycleHelper uiHelper;

    TextView weightTextView;
    RelativeLayout weightLayout;
    ListView trendListView;
    LinearLayout processTrendLinearLayout;
    LinearLayout processWeightChooserLinearLayout;
    ImageView weightTrendImageView;
    TextView averageWeightTrendTextView;
    TextView lastWeightTrendTextView;
    TrendListViewAdapter listViewAdapter;
    ImageButton helpImageButton;

    List<Trend> trends = new ArrayList<Trend>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main, container, false);

        // Hack: um den glow effekt in der ListView zu umgehen. In API Version 8 nur so möglich
        int glowDrawableId = this.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        if (glowDrawableId > 0) {
            Drawable androidGlow = this.getResources().getDrawable(glowDrawableId);
            androidGlow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        int edgeDrawableId = this.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        if (edgeDrawableId > 0) {
            Drawable androidEdge = this.getResources().getDrawable(edgeDrawableId);
            androidEdge.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        weightTextView = (TextView) view.findViewById(R.id.weightTextView);
        weightLayout = (RelativeLayout) view.findViewById(R.id.weightLayout);
        trendListView = (ListView) view.findViewById(R.id.trendListView);
        processTrendLinearLayout = (LinearLayout) view.findViewById(R.id.processTrendLinearLayout);
        processWeightChooserLinearLayout = (LinearLayout) view.findViewById(R.id.processWeightChooserLinearLayout);
        weightTrendImageView = (ImageView) view.findViewById(R.id.weightTrendImageView);
        averageWeightTrendTextView = (TextView) view.findViewById(R.id.averageWeightTrendTextView);
        lastWeightTrendTextView = (TextView) view.findViewById(R.id.lastWeightTrendTextView);
        helpImageButton = (ImageButton) view.findViewById(R.id.helpImageButton);

        // Init Controls
        final GestureDetector gestureDetector = new GestureDetector(new GestureListener(getActivity()));
        weightLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        processTrendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processTrend();
            }
        });

        processWeightChooserLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processWeightChooser();
            }
        });

        listViewAdapter = new TrendListViewAdapter(getActivity(),
                R.layout.trend_list_item,
                trends,
                TrendDAO.getInstance(getActivity()),
                new TrendActivity());

        trendListView.setAdapter(listViewAdapter);

        helpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }
        });

        registerForContextMenu(trendListView);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
        listViewAdapter.updateList(trends);

        // For scenarios where the weight_chooser activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            Log.d(MainFragment.class.getName(), "Facebook Session aktiv.");
            onSessionStateChange(session.getState());
        }

        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Trend listViewItem = listViewAdapter.getItem((int) (info.id));

        switch (item.getItemId()) {

            case R.id.openItem:
                openDetailView(listViewItem.getId());

                return true;
            case R.id.deleteItem:
                deleteItem(listViewItem.getId());

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(state);
        }
    };

    private void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            Log.d(MainFragment.class.getName(), "Facebook Session ist geöffnet");
        } else if (state.isClosed()) {
            Log.d(MainFragment.class.getName(), "Facebook Session ist geschlossen");
        }
    }

    private void initData() {
        Trend lastTrend = TrendDAO.getInstance(getActivity()).getLastTrendWithWeight();
        trends = TrendDAO.getInstance(getActivity()).getAll();

        if (lastTrend != null) {
            weightTextView.setText(lastTrend.getWeight());
            weightTrendImageView.setImageResource(getImageResourceForCalculateWeightTrend(lastTrend, trends));
            lastWeightTrendTextView.setText(String.valueOf(getLastWeightTrend(trends)));
            averageWeightTrendTextView.setText(String.valueOf(getAverageWeightTrend(trends)));
        } else {
            weightTextView.setText(Constants.INIT_WEIGHT);
            weightTrendImageView.setImageResource(R.drawable.arrow_orange_icon);
            lastWeightTrendTextView.setText("0");
            averageWeightTrendTextView.setText("0");
        }
    }


    private void deleteItem(long id) {

        // Prüfe Facebook
        Trend trend = TrendDAO.getInstance(getActivity()).getById(id);
        if (trend.getFacebookId() != null) {
            FacebookHelper.deletePost(Session.getActiveSession(), trend);
        }

        TrendDAO.getInstance(getActivity()).delete(id);

        initData();

        listViewAdapter.updateList(trends);
    }

    private void openDetailView(int trendId) {
        Intent intent = new Intent(getActivity(), ShowTrendActivity.class);
        intent.putExtra(Constants.CHOSEN_TREND, trendId);
        startActivity(intent);
    }

    private void processTrend() {
        Intent intent = new Intent(getActivity(), TrendActivity.class);
        intent.putExtra(Constants.CURRENT_TREND, Trend.TrendType.Down.name());
        startActivity(intent);
    }

    private void processWeightChooser() {
        Intent intent = new Intent(getActivity(), WeightChooserActivity.class);
        intent.putExtra(Constants.CURRENT_WEIGHT, weightTextView.getText());
        startActivity(intent);
    }

    private int getImageResourceForCalculateWeightTrend(Trend lastTrend, List<Trend> allTrends) {
        int counter = 0;

        for (Trend trend : allTrends) {
            if (trend.getTrend() == Trend.TrendType.Down) {
                --counter;
            } else {
                ++counter;
            }

            if (trend.getId() == lastTrend.getId()) {
                break;
            }
        }

        if (counter <= 0) {
            return R.drawable.arrow_down_orange_icon;
        }

        return R.drawable.arrow_up_orange_icon;
    }

    private double getAverageWeightTrend(List<Trend> allTrends) {
        double lastValue = 0;
        double average = 0;
        // Starte mit -1 da wir nur die shifts und nicht die Werte zählen
        double countedTrendWithWeight = -1;

        for (Trend trend : allTrends) {
            if (trend.getWeight() != null) {
                countedTrendWithWeight++;
                double currentValue = Double.parseDouble(trend.getWeight());

                if (lastValue == 0) {
                    lastValue = currentValue;
                    continue;
                }

                average += lastValue - currentValue;

                lastValue = currentValue;
            }
        }

        average = average / countedTrendWithWeight;
        average = Math.round(average * 10) / 10.0;
        return average;
    }

    private double getLastWeightTrend(List<Trend> allTrends) {
        double lastValue = 0;
        double average = 0;

        for (Trend trend : allTrends) {
            if (trend.getWeight() != null) {
                double currentValue = Double.parseDouble(trend.getWeight());

                if (lastValue == 0) {
                    lastValue = currentValue;
                    continue;
                }

                average = lastValue - currentValue;
                average = Math.round(average * 10) / 10.0;

                return average;
            }
        }

        return average;
    }

    private void openHelp() {

        boolean playFromSDCard = true;

        File f = new File(Environment.getExternalStorageDirectory(), Constants.NAME_HELP_VIDEO);

        if(!f.exists()){
            playFromSDCard = Commons.installHelpVideo(getActivity());
        }

        if(playFromSDCard){

            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

            dialog.setContentView(R.layout.help);

            VideoView videoView = (VideoView) dialog.findViewById(R.id.videoView);
            videoView.setVideoURI(Uri.fromFile(f));
            videoView.requestFocus();
            videoView.start();
            dialog.show();

        } else {

            // Wenn das Video nicht auf der SD Karte installiert werden kann,
            // wird es online geöffnet
            String link = "http://youtu.be/jm4s4VvuKlI";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            getActivity().startActivity(intent);

        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        Activity activity;

        public GestureListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (velocityY < -150 || velocityY > 150) {
                processTrend();
            }

            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            processWeightChooser();

            return false;
        }
    }
}
