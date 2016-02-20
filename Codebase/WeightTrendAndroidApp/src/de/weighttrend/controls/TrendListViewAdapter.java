package de.weighttrend.controls;

import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.Session;
import de.weighttrend.activities.R;
import de.weighttrend.activities.ShowTrendActivity;
import de.weighttrend.io.FacebookHelper;
import de.weighttrend.io.TrendDAO;
import de.weighttrend.models.Trend;
import de.weighttrend.util.Commons;
import de.weighttrend.util.Constants;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 05.04.14
 * Time: 11:54
 * Adapter für eine ListView. Generische Version des Adapter liegt in KiO
 */
public class TrendListViewAdapter extends ArrayAdapter<Trend> {

    TrendDAO dataAccessObject;
    Activity activity;
    int listViewResourceId;
    Activity clickActivity;

    public TrendListViewAdapter(Activity activity, int resourceId,
                                List<Trend> items, TrendDAO dataAccessObject, Activity clickActivity) {
        super(activity, resourceId, items);

        this.activity = activity;
        this.dataAccessObject = dataAccessObject;
        this.clickActivity = clickActivity;
        this.listViewResourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Trend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = mInflater.inflate(listViewResourceId, null);
            holder = new ViewHolder();
            holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.textTextView = (TextView) convertView.findViewById(R.id.textTextView);
            holder.trendImageView = (ImageView) convertView.findViewById(R.id.trendImageView);
            holder.facebookIconLayout = (LinearLayout) convertView.findViewById(R.id.facebookIconLayout);
            holder.facebookIconImageView = (ImageView) convertView.findViewById(R.id.facebookIconImageView);
            holder.commentCountTextView = (TextView) convertView.findViewById(R.id.commentCountTextView);
            holder.likesCountTextView = (TextView) convertView.findViewById(R.id.likesCountTextView);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Unterscheidung zwischen Trend- und Gewichtsabgabe
        if (rowItem.getWeight() != null) {
            convertView.setBackgroundResource(R.color.orange_background);

            holder.trendImageView.setVisibility(View.GONE);
            holder.textTextView.setText(rowItem.getWeight() + " kg | " + rowItem.getText());
        } else {
            convertView.setBackgroundResource(R.color.purple_background);

            holder.trendImageView.setImageResource(rowItem.getTrend().getPictureResourceId());

            holder.trendImageView.setVisibility(View.VISIBLE);
            holder.textTextView.setText(Commons.cutStringIfToLong(rowItem.getDetailText()));
        }

        holder.dateTextView.setText(Commons.getFormattedDatetime(rowItem.getDatetime()));

        holder.textTextView.setTag(rowItem.getId());

        // Facebook Daten
        if (rowItem.getFacebookId() != null){

            holder.facebookIconLayout.setVisibility(View.VISIBLE);

            FacebookHelper.writeCommentsAndLikesCountToPost(Session.getActiveSession(), rowItem.getFacebookId(),
                    holder.commentCountTextView, holder.likesCountTextView);
        } else {

            holder.facebookIconLayout.setVisibility(View.GONE);

        }

        final GestureDetector gestureDetector = new GestureDetector(new GestureListener(activity, convertView));
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        return convertView;
    }

    public void updateList(List<Trend> newlist) {
        super.clear();

        for(Trend trend : newlist){
            super.add(trend);
        }

        this.notifyDataSetChanged();
    }

     /**
     * private view holder class
     */
    private class ViewHolder {
        ImageView trendImageView;
        TextView dateTextView;
        TextView textTextView;
        LinearLayout facebookIconLayout;
        ImageView facebookIconImageView;
        TextView likesCountTextView;
        TextView commentCountTextView;
     }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        Activity activity;
        View view;

        public GestureListener(Activity activity, View view){
            this.activity = activity;
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            TextView titleTextView = (TextView) view.findViewById(R.id.textTextView);
            String id = titleTextView.getTag().toString();

            Intent intent = new Intent(activity, ShowTrendActivity.class);
            intent.putExtra(Constants.CHOSEN_TREND, Integer.parseInt(id));
            activity.startActivity(intent);

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

            activity.openContextMenu(view);
        }
    }
}