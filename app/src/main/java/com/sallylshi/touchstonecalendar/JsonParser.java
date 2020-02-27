package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;

public class JsonParser {

    public String read(JsonReader reader) throws IOException {
        Log.e("SALLY", "JsonParser.read");
        String output = "";
        String peek = "";
        reader.beginObject();
        output += reader.nextName();

        while(reader.hasNext()) {
            if(reader.peek() == JsonToken.NAME) {
                String name = reader.nextName();
                Log.e("SALLY", name);
//                if(name == "body") {
//                    output += name;
//                    break;
//                }
            }
            reader.skipValue();
//            if(reader.peek() == JsonToken.END_OBJECT) {
//                reader.endObject();
//                reader.beginObject();
//                output += "END OBJECT NEXCT IS " + reader.nextName();
//            }
        }

         return "blank";
    }
}
