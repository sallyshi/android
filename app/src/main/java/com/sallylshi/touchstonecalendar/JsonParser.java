package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JsonParser {

    private String readNameFromObject(JsonReader reader, String inputName) throws IOException {
        String output = "";
        reader.beginObject();
        while (reader.hasNext()) {
            output = reader.nextName();
            if (output.equals(inputName)) {
                return output;
            } else {
                reader.skipValue();
            }
        }
        return null;
    }

    private Event parseEvent(JsonReader reader) throws IOException, ParseException {
        String start = "";
        String end = "";
        String timezone = "";
        Date start_date, end_date;
        String title = "";
        String description = "";
        String costType = "";

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("start_datetime")) {
                start = reader.nextString();
            } else if (name.equals("end_datetime")) {
                end = reader.nextString();
            } else if (name.equals("timezone")) {
                timezone = TimeZone.getTimeZone(reader.nextString()).getDisplayName();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("description_short")) {
                description = reader.nextString();
            } else if (name.equals("cost_type")) {
                costType = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss z");
        start_date = f.parse(start + " " + timezone);
        end_date = f.parse(end + " " + timezone);

        return new Event(title, start_date, end_date, description, costType);
    }

    String read(JsonReader reader) throws IOException, ParseException {
        reader.beginObject();

        // The 4th name's value contains the data we want to extract
        for (int i = 0; i < 3; i++) {
            reader.nextName();
            reader.skipValue();
        }

        reader.nextName();
        reader.beginObject();
        reader.nextName(); //this is body

        readNameFromObject(reader, "data");
        readNameFromObject(reader, "items");
        readNameFromObject(reader, "2020-03-01");

        reader.beginArray();
        reader.beginObject();

        Event event = parseEvent(reader);
        return event.start.toString();


        //SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd hh:mm z");


    }
}
