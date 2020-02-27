package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;

public class JsonParser {

    public String read(JsonReader reader) throws IOException {
        Log.e("SALLY", "JsonParser.read");
        String output = "";
        String name = "";

        reader.beginObject();

            for(int i=0; i < 3; i++) {
                if (reader.peek() == JsonToken.NAME) {
                    name = reader.nextName();
                }
                reader.skipValue();
            }

            name = reader.nextName();
            reader.beginObject();

         return name;
    }
}
