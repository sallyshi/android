package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        String location = "";

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
                case "taxonomies":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String v = reader.nextName();
                        if (v.equals("taxonomy_venue")) {
                            reader.beginArray();
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String t = reader.nextName();
                                if (t.equals("title")) {
                                    location = reader.nextString();

                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                            reader.endArray();

                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        SimpleDateFormat f;
        if (start.contains("T")) {
            f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss z");
        } else {
            f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        }
        start_date = f.parse(start + " " + timezone);
        end_date = f.parse(end + " " + timezone);

        return new Event(title, start_date, end_date, description, costType, url, location);
    }

    List<Event> read(JsonReader reader) throws IOException, ParseException {
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
