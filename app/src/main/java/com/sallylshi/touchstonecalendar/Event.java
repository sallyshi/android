package com.sallylshi.touchstonecalendar;

import java.net.URL;
import java.util.Date;

public class Event {
    String title;
    Date start, end;
    String description;
    String costType;
    URL url;

    public Event(String title, Date start, Date end, String description, String costType, URL url){
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.costType = costType;
        this.url = url;
    }
}
