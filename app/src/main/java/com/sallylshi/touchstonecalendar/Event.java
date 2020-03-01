package com.sallylshi.touchstonecalendar;

import java.util.Date;

public class Event {
    String title;
    Date start, end;
    String description;
    String costType;

    public Event(String title, Date start, Date end, String description, String costType){
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.costType = costType;
    }
}
