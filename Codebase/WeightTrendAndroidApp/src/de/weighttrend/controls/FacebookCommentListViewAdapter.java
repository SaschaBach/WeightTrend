package de.weighttrend.controls;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.weighttrend.activities.R;
import de.weighttrend.io.FacebookHelper;
import de.weighttrend.models.FacebookComment;
import de.weighttrend.util.Commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bach
 * Date: 05.04.14
 * Time: 11:54
 * Adapter für einen ListView mit Elementen der Klasse FacebookComment
 */
public class FacebookCommentListViewAdapter extends ArrayAdapter<FacebookComment>
        implements View.OnClickListener {

    Activity activity;
    int listViewResourceId;
    int backgroundColorId;

    public FacebookCommentListViewAdapter(Activity activity, int resourceId, int backgroundColorId) {
        super(activity, resourceId, new ArrayList<FacebookComment>());

        this.activity = activity;
        this.listViewResourceId = resourceId;
        this.backgroundColorId = backgroundColorId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FacebookComment rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = mInflater.inflate(listViewResourceId, null);
            holder = new ViewHolder();
            holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.userNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.textTextView = (TextView) convertView.findViewById(R.id.textTextView);
            holder.commentCountTextView = (TextView) convertView.findViewById(R.id.commentCountTextView);
            holder.likesCountTextView = (TextView) convertView.findViewById(R.id.likesCountTextView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dateTextView.setText(Commons.getFormattedDatetime(rowItem.getDate()));
        holder.userNameTextView.setText(rowItem.getUserName());
        holder.textTextView.setText(rowItem.getText());
        holder.commentCountTextView.setText(String.valueOf(rowItem.getCommentCount()));
        holder.likesCountTextView.setText(String.valueOf(rowItem.getLikesCount()));

        holder.textTextView.setTag(rowItem.getFacebookId());

        convertView.setOnClickListener(this);

        convertView.setBackgroundResource(backgroundColorId);

        return convertView;
    }

    public void updateList(List<FacebookComment> newlist) {

        super.clear();

        for(FacebookComment comment : newlist){
            super.add(comment);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.textTextView);
        String id = titleTextView.getTag().toString();

        Intent intent = FacebookHelper.createIntentToOpenPost(getContext(), id);
        activity.startActivity(intent);
    }

    /**
     * private view holder class
     */
    private class ViewHolder {
        TextView dateTextView;
        TextView userNameTextView;
        TextView textTextView;
        TextView commentCountTextView;
        TextView likesCountTextView;
    }
}