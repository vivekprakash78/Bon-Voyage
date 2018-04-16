package com.quadrivium.devs.bonvoyage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> {
    public EventListAdapter(Activity context, ArrayList<Event> event) {
        super(context, 0, event);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.event_list_view, parent, false);
        }

        TextView eventID = listItemView.findViewById(R.id.eventID);
        TextView eventName = listItemView.findViewById(R.id.eventName);
        //TextView eventLocation = listItemView.findViewById(R.id.eventLocation);
        TextView eventDateFrom = listItemView.findViewById(R.id.eventDateFrom);
        TextView eventDateTo = listItemView.findViewById(R.id.eventDateTo);
        TextView eventTimeFrom = listItemView.findViewById(R.id.eventTimeFrom);
        TextView eventTimeTo = listItemView.findViewById(R.id.eventTimeTo);
        TextView eventDescription = listItemView.findViewById(R.id.eventDescription);
        TextView eventCreator = listItemView.findViewById(R.id.eventCreator);

        Event currentEvent = getItem(position);

        eventID.setText(currentEvent.getEventID());
        eventName.setText(currentEvent.getEventName());
        //eventLocation.setText(currentEvent.getEventLocation());
        eventDateFrom.setText(currentEvent.getEventFromDate());
        eventDateTo.setText(currentEvent.getEventToDate());
        eventTimeFrom.setText(currentEvent.getEventFromTime());
        eventTimeTo.setText(currentEvent.getEventToTime());
        eventDescription.setText(currentEvent.getEventDescription());
        eventCreator.setText(currentEvent.getEventCreator());

        return listItemView;
    }
}