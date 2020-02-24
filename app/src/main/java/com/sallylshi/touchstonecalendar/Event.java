package com.sallylshi.touchstonecalendar;

import java.util.Date;

public class Event {
    String title;
    Date start, end;
    String description;
    String venue;

    public Event(String title, Date start, Date end, String description, String venue){
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.venue = venue;
    }
}
