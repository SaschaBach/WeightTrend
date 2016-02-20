package de.weighttrend.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import de.weighttrend.activities.MainActivity;
import de.weighttrend.activities.R;
import de.weighttrend.io.FacebookHelper;
import de.weighttrend.io.TrendDAO;
import de.weighttrend.io.WebserviceHelper;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Commons;
import de.weighttrend.util.Constants;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 02.04.14
 * Time: 22:53
 * Fragment zum Trend View. Das Fragment wird benötigt, um die Facebook Session zu erstellen.
 */
public class TrendFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private Button saveButton;
    private Button backButton;
    private TextView headerTextView;
    private LinearLayout trendLinearLayout;
    private ImageButton trendUpImageButton;
    private ImageButton trendDownImageButton;
    private TextView textTextView;
    private EditText detailText;
    private LoginButton facebookButton;
    private Button datetimePicker;
    private RelativeLayout mainLayout;
    private RelativeLayout actionBar;
    private Trend actualTrend;
    private Trend lastTrend;

    private ProgressDialog progressDialog;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(state);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trend, container, false);

        trendLinearLayout = (LinearLayout) view.findViewById(R.id.trendLinearLayout);
        textTextView = (TextView) view.findViewById(R.id.textTextView);
        trendUpImageButton = (ImageButton) view.findViewById(R.id.trendUpImageButton);
        trendDownImageButton = (ImageButton) view.findViewById(R.id.trendDownImageButton);
        headerTextView = (TextView) view.findViewById(R.id.headerTextView);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        detailText = (EditText) view.findViewById(R.id.detailEditText);
        actionBar = (RelativeLayout) view.findViewById(R.id.actionBar);
        mainLayout = (RelativeLayout) view.findViewById(R.id.trendMainLayout);
        datetimePicker = (Button) view.findViewById(R.id.datetimePicker);
        facebookButton = (LoginButton) view.findViewById(R.id.facebookLoginButton);
        facebookButton.setFragment(this);
        facebookButton.setPublishPermissions(Arrays.asList("publish_actions", "manage_pages"));

        // Init Trend Objekt
        lastTrend = TrendDAO.getInstance(getActivity()).getLastTrendWithWeight();

        // Wenn es keinen Vorgänger gibt
        if(lastTrend == null) {
            lastTrend = new Trend(getActivity());
            lastTrend.setWeight(Constants.INIT_WEIGHT);
        }

        actualTrend = new Trend(getActivity());
        String currentWeight = getActivity().getIntent().getStringExtra(Constants.CURRENT_WEIGHT);

        if(currentWeight != null){
            // In diesem Fall wird die Aktivity vom WeightChooser aufgerufen
            mainLayout.setBackgroundResource(R.color.orange_background);

            trendLinearLayout.setVisibility(View.GONE);
            textTextView.setVisibility(View.VISIBLE);

            // Hack um die Padding zu behalten, dieser verliert man, wenn ein neuer Background gesetzt wird
            int pL = actionBar.getPaddingLeft();
            int pT = actionBar.getPaddingTop();
            int pR = actionBar.getPaddingRight();
            int pB = actionBar.getPaddingBottom();

            actionBar.setBackgroundResource(R.drawable.actionbar_background_orange);
            actionBar.setPadding(pL, pT, pR, pB);

            headerTextView.setText("Gewicht");

            actualTrend.setWeight(currentWeight);

            double weightDiff = Double.parseDouble(actualTrend.getWeight()) - Double.parseDouble(lastTrend.getWeight());
            weightDiff = Math.round(weightDiff * 10) / 10.0;

            if(weightDiff > 0){
                actualTrend.setTrend(Trend.TrendType.Up);
            } else {
                actualTrend.setTrend(Trend.TrendType.Down);
            }

            actualTrend.setText(actualTrend.getTrend().getSymbol() + Math.abs(weightDiff) + " kg");

            textTextView.setText(actualTrend.getText());

        } else {
            // In diesem Fall soll nur ein Trend vermerkt werden,
            // die Default Background Einstellungen sind für den Trend
            actualTrend.setWeight(null);

            String currentTrend = getActivity().getIntent().getStringExtra(Constants.CURRENT_TREND);

            actualTrend.setTrend(Trend.TrendType.valueOf(currentTrend));
            setTrend();

        }

        // Init Controls
        datetimePicker.setText(Commons.getFormattedDatetime(Calendar.getInstance()));
        datetimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putIntArray(Constants.DATE_INT_ARRAY, new int[]{actualTrend.getDatetime().get(Calendar.YEAR),
                        actualTrend.getDatetime().get(Calendar.MONTH),
                        actualTrend.getDatetime().get(Calendar.DAY_OF_MONTH)});

                getActivity().showDialog(Constants.FROM_DATE_PICKER, bundle);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSave();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        trendDownImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualTrend.setTrend(Trend.TrendType.Down);
                setTrend();
            }
        });
        trendUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualTrend.setTrend(Trend.TrendType.Up);
                setTrend();
            }
        });

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

        // For scenarios where the weight_chooser activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
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


    public DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, selectedDay);

            actualTrend.setDatetime(calendar);

            datetimePicker.setText(Commons.getFormattedDatetime(actualTrend.getDatetime()));
        }
    };

    private void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            facebookButton.setBackgroundResource(R.drawable.facebook_button_enable);
        } else if (state.isClosed()) {
            facebookButton.setBackgroundResource(R.drawable.facebook_button_disable);
        }
    }

    private void performSave(){

        actualTrend.setDetailText(detailText.getText().toString());

        SaveTrendTask saveTrendTask = new SaveTrendTask();
        saveTrendTask.execute(actualTrend);
   }

    private void setTrend(){

        if(actualTrend.getTrend() == Trend.TrendType.Up){

            actualTrend.setTrend(Trend.TrendType.Up);
            trendDownImageButton.setImageResource(R.drawable.trend_down_button);
            trendUpImageButton.setImageResource(R.drawable.trend_up_active_button);

        } else {

            actualTrend.setTrend(Trend.TrendType.Down);
            trendDownImageButton.setImageResource(R.drawable.trend_down_active_button);
            trendUpImageButton.setImageResource(R.drawable.trend_up_button);

        }

        actualTrend.setText(actualTrend.getTrend().getText());
    }

    private class SaveTrendTask extends AsyncTask<Trend, Integer, Void> {

        protected Void doInBackground(Trend... trends) {

            for (int i = 0; i < trends.length; i++) {

                // Zuerst Facebook, da hier die FacebookId gesetzt wird
                FacebookHelper.sendToWeightTrendFacebookGroup(Session.getActiveSession(), actualTrend);

                WebserviceHelper.insert(actualTrend);
                TrendDAO.getInstance(getActivity()).save(actualTrend);
            }

            return null;
        }

        protected void onPreExecute(){
            progressDialog=ProgressDialog.show(getActivity(),"",Constants.TEXT_WAIT);
        }

        protected void onPostExecute(Void Result){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

            progressDialog.dismiss();
        }
    }
}
