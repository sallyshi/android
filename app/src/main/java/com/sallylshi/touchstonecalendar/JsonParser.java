package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String read(JsonReader reader) throws IOException, ParseException {
        String title;
        Date start, end;
        String description;
        String venue;
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
        readNameFromObject(reader, "start_datetime");
       // SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss z");

       // start = f.parse(reader.nextString());

       return reader.nextString();


    }
}
