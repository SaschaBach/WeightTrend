package de.weighttrend.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.weighttrend.io.SettingsDAO;
import de.weighttrend.io.TrendDAO;
import de.weighttrend.io.WebserviceHelper;
import de.weighttrend.models.Settings;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Constants;

public class WeightChooserActivity extends Activity {

    TextView currentWeightTextView;
    TextView differenceTextView;
    RelativeLayout scrollView;
    ImageButton saveWeightImageButton;
    ImageButton increaseWeightImageButton;
    ImageButton decreaseWeightImageButton;

    double initValue;
    double currentValue;
    double rangeFactor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_chooser);

        currentWeightTextView = (TextView) findViewById(R.id.currentWeightTextView);
        differenceTextView = (TextView) findViewById(R.id.differenceTextView);
        scrollView = (RelativeLayout) findViewById(R.id.scrollBar);
        saveWeightImageButton = (ImageButton) findViewById(R.id.saveWeightImageButton);
        increaseWeightImageButton = (ImageButton) findViewById(R.id.increaseWeightImageButton);
        decreaseWeightImageButton = (ImageButton) findViewById(R.id.decreaseWeightImageButton);

        // Berechne die Display-abhängigen Basiswerte
        final String currentWeight = getIntent().getStringExtra(Constants.CURRENT_WEIGHT);
        initValue = Double.parseDouble(currentWeight);
        currentValue = initValue;

        Display display = getWindowManager().getDefaultDisplay();
        int displayHeight = display.getHeight();
        double maxWeightDecreasePerSlide = initValue + 15;
        double maxWeightIncreasePerSlide = initValue - 15;
        double weightDifference = maxWeightDecreasePerSlide - maxWeightIncreasePerSlide;
        rangeFactor = weightDifference / displayHeight;

        // Init Controls
        currentWeightTextView.setText(String.valueOf(initValue));
        differenceTextView.setText("0.0");

        saveWeightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWeight();
            }
        });

        increaseWeightImageButton.setOnTouchListener(new IncreaseTouchListener());

        decreaseWeightImageButton.setOnTouchListener(new DecreaseTouchListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        final GestureDetector gestureDetector = new GestureDetector(new GestureListener(this));
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void saveWeight(){

        Settings settings = SettingsDAO.getInstance(this).get();

        if(settings.isInitWeight()){

            Trend trend = new Trend(this);
            trend.setWeight(currentWeightTextView.getText().toString());
            trend.setText(Constants.TEXT_INIT_WEIGHT);
            trend.setTrend(Trend.TrendType.Down);

            settings.setInitWeight(false);

            WebserviceHelper.insert(trend);
            TrendDAO.getInstance(this).save(trend);
            SettingsDAO.getInstance(this).update(settings);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TrendActivity.class);
            intent.putExtra(Constants.CURRENT_WEIGHT, currentWeightTextView.getText());
            startActivity(intent);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        Activity activity;

        public GestureListener(Activity activity){
            this.activity = activity;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY) {

            // Berechne die Wert Veränderung
            double scaledY = e2.getY() * rangeFactor;
            double scaledCenter = e1.getY() * rangeFactor;
            double shift = scaledCenter - scaledY;
            double newValue = currentValue + shift;
            newValue = Math.round(newValue * 10) / 10.0;
            currentWeightTextView.setText(String.valueOf(newValue));

            double difference = newValue - initValue;
            difference = Math.round(difference * 10) / 10.0;
            differenceTextView.setText(String.valueOf(difference));

            return true;
        }

        @Override
        public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {

            currentValue = Double.parseDouble(currentWeightTextView.getText().toString());

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            saveWeight();

            return false;
        }
    }

    private class IncreaseTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            currentValue = currentValue + 0.1;
            double newValue = Math.round(currentValue * 10) / 10.0;
            currentWeightTextView.setText(String.valueOf(newValue));

            return true;
        }
    }

    private class DecreaseTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            currentValue = currentValue - 0.1;
            double newValue = Math.round(currentValue * 10) / 10.0;
            currentWeightTextView.setText(String.valueOf(newValue));

            return true;
        }
    }
}
