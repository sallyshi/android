package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class JsonParser {

    private void readNameFromObject(JsonReader reader, String inputName) throws IOException {
        String output = "";
        reader.beginObject();
        while (reader.hasNext()) {
            output = reader.nextName();
            if (output.equals(inputName)) {
                return;
            } else {
                reader.skipValue();
            }
        }
    }

    private Event parseEvent(JsonReader reader) throws IOException, ParseException {
        String start = "";
        String end = "";
        String timezone = "";
        Date start_date, end_date;
        String title = "";
        String description = "";
        String costType = "";
        URL url = null;

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "start_datetime":
                    start = reader.nextString();
                    break;
                case "end_datetime":
                    end = reader.nextString();
                    break;
                case "timezone":
                    timezone = TimeZone.getTimeZone(reader.nextString()).getDisplayName();
                    break;
                case "title":
                    title = reader.nextString();
                    break;
                case "description_short":
                    description = reader.nextString();
                    break;
                case "cost_type":
                    costType = reader.nextString();
                    break;
                case "url":
                    url = new URL(reader.nextString());
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        SimpleDateFormat f;
        if(start.contains("T")) {
            f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss z");
        } else {
            f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        }
        start_date = f.parse(start + " " + timezone);
        end_date = f.parse(end + " " + timezone);

        return new Event(title, start_date, end_date, description, costType, url);
    }

    List<Event> read(JsonReader reader) throws IOException, ParseException {
        reader.beginObject();

        // The 4th name's value contains the data we want to extract
        for (int i = 0; i < 3; i++) {
            reader.nextName();
            reader.skipValue();
        }

        reader.nextName();
        reader.beginObject();
        reader.nextName();

        readNameFromObject(reader, "data");
        readNameFromObject(reader, "items");

        List<Event> events = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            reader.nextName();
            reader.beginArray();

            while (reader.hasNext()) {
                reader.beginObject();
                events.add(parseEvent(reader));
                reader.endObject();
            }
            reader.endArray();
        }
        return events;
    }
}
