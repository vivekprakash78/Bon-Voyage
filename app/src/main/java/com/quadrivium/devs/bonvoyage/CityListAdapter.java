package com.quadrivium.devs.bonvoyage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vivek on 22-02-2018.
 */

public class CityListAdapter extends ArrayAdapter<City>{


        public CityListAdapter(Activity context, ArrayList<City> city) {

            super(context, 0, city);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItemView = convertView;
            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_favorite, parent, false);
            }
            TextView cityID = listItemView.findViewById(R.id.cityID);
            TextView cityName = listItemView.findViewById(R.id.cityName);

            City currentCity = getItem(position);

            cityID.setText(currentCity.getCityID());
            cityName.setText(currentCity.getCityName());

            return listItemView;
        }
}