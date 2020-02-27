package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;

public class JsonParser {

    public String read(JsonReader reader) throws IOException {
        String output = "";
        String name = "";

        reader.beginObject();

        // The 4th name's value contains the data we want to extract
        for (int i = 0; i < 3; i++) {
            name = reader.nextName();
            reader.skipValue();
        }

        reader.nextName();
        reader.beginObject();
        reader.nextName(); //this is body
        reader.beginObject(); // this is "success"
        int count = 1;
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("data")) {
                break;
            } else {
                reader.skipValue();
            }
        }
       reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("items")) {
                break;
            } else {
                reader.skipValue();
            }
        }
        return name;
    }
}
