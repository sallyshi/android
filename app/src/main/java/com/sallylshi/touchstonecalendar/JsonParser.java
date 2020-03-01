package com.sallylshi.touchstonecalendar;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;

public class JsonParser {

    private JsonReader readName(JsonReader reader, String savedName, String inputName) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            savedName = reader.nextName();
            if (savedName.equals(inputName)) {
                return reader;
            } else {
                reader.skipValue();
            }
        }
        return null;
    }

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

        readName(reader,name, "data");

        //G.timelyblahelhie



        //readName(readName(readName(reader, "data"), "items"), "start_datetime");

//        reader.beginObject(); // this is "success"
//        while (reader.hasNext()) {
//            name = reader.nextName();
//            if (name.equals("data")) {
//                break;
//            } else {
//                reader.skipValue();
//            }
//        }
//       reader.beginObject();
//        while (reader.hasNext()) {
//            name = reader.nextName();
//            if (name.equals("items")) {
//                break;
//            } else {
//                reader.skipValue();
//            }
//        }
//
//        reader.beginObject();
//        while (reader.hasNext()) {
//            name = reader.nextName();
//            if (name.equals("start_datetime")) {
//                break;
//            } else {
//                reader.skipValue();
//            }
//        }
        return name;
    }
}
