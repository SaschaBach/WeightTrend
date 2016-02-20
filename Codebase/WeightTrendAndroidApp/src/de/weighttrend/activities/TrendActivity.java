package de.weighttrend.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.weighttrend.fragments.TrendFragment;
import de.weighttrend.util.Constants;

public class TrendActivity extends FragmentActivity {

    private TrendFragment trendFragment;

     /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trend);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            trendFragment = new TrendFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, trendFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            trendFragment = (TrendFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case Constants.FROM_DATE_PICKER:
                // set date picker as current date
                int[] date = bundle.getIntArray(Constants.DATE_INT_ARRAY);

                return new DatePickerDialog(this, trendFragment.datePickerListener,
                        date[0],
                        date[1],
                        date[2]);
        }
        return null;
    }
}
