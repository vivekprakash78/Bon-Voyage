package com.quadrivium.devs.bonvoyage;


public class Event {

    private String eventID;
    private String eventName;
    private String eventLocation;
    private String eventFromDate;
    private String eventToDate;
    private String eventFromTime;
    private String eventToTime;
    private String eventDescription;
    private String eventCreator;

    public Event(String eventID, String eventName, String eventLocation, String eventFromDate, String eventToDate, String eventFromTime, String eventToTime, String eventDescription, String eventCreator) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventFromDate = eventFromDate;
        this.eventToDate = eventToDate;
        this.eventFromTime = eventFromTime;
        this.eventToTime = eventToTime;
        this.eventDescription = eventDescription;
        this.eventCreator = eventCreator;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventFromDate() {
        return eventFromDate;
    }

    public String getEventToDate() {
        return eventToDate;
    }

    public String getEventFromTime() {
        return eventFromTime;
    }

    public String getEventToTime() {
        return eventToTime;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventCreator() {
        return eventCreator;
    }
}
