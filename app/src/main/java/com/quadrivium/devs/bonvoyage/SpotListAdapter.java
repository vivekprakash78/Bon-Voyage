package com.quadrivium.devs.bonvoyage;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vivek on 02-02-2018.
 */

public class SpotListAdapter extends ArrayAdapter<Spot> {

        private static final String LOG_TAG = SpotListAdapter.class.getSimpleName();

        public SpotListAdapter(Activity context, ArrayList<Spot> spot) {

            super(context, 0, spot);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItemView = convertView;
            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.listview, parent, false);
            }

            TextView spotName = listItemView.findViewById(R.id.name);
            TextView spotAddress = listItemView.findViewById(R.id.address);
            TextView spotOpenNow= listItemView.findViewById(R.id.openNow);
            TextView spotRating = listItemView.findViewById(R.id.rating);

            Spot currentSpot = getItem(position);

            spotName.setText(currentSpot.getSpotname());
            spotAddress.setText(currentSpot.getSpotAddress());
            if(currentSpot.getSpotOpenNow()){
                spotOpenNow.setTextColor(Color.rgb(42,230,50)); //2ae632
                spotOpenNow.setText("Open Now");
            }
            else{
                spotOpenNow.setTextColor(Color.rgb(255,58,58));
                spotOpenNow.setText("Closed");
            }
            if(currentSpot.getspotRating()!=0.0)
                spotRating.setText(""+currentSpot.getspotRating());
            else
                spotRating.setText("- -");
            return listItemView;
        }

    }
