package core;

import org.threeten.bp.ZonedDateTime;

public class Event {
    // Link to calendar
    public String calendarId;
    public long instanceId;

    // Basics
    public String title;
    public String details;
    public ZonedDateTime startTime;
    public ZonedDateTime endTime;
    public String timeZone;

    // Contact
    public String location;
    public String phone;
    public String email;
    public String url;
    public String website;

    // Status
    public int status;
    public boolean isEnabled;
    public boolean isFavorite;

    // Props ( cost, value, priority )
    public double cost;
    public int value;
    public int priority;
    public int ordinal;
    public String icon;
}
