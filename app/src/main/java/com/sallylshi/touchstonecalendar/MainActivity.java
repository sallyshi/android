package com.sallylshi.touchstonecalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.io.StringReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {
    private static String MISSION_CLIFFS_URL = "https://calendar.time.ly/rl4r7fx3/stream?tags=151613968&timely_id=timely_0.761031607867843";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHtmlFromWeb();
    }

    private void getHtmlFromWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String output = "";
                    Document doc = Jsoup.connect(MISSION_CLIFFS_URL).get();
                    Element element = doc.select("script[id*=\"timely-calendar-state\"]").first();

                    TextView view = findViewById(R.id.test);

                    String json = element.data();
                    final String realjson = json.replaceAll("&q;", "\"");

//                    runOnUiThread( () ->view.setText(realjson));

                    JsonReader reader = new JsonReader(new StringReader(realjson));
                    reader.setLenient(true);


                    final String thisIsreallytheoutput = parse(reader);

                    // Test on UI
                    runOnUiThread( () ->view.setText(thisIsreallytheoutput));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String parse(JsonReader reader) throws IOException{
        String output = "";
        while(reader.peek() == JsonToken.END_ARRAY || reader.peek() == JsonToken.END_OBJECT || reader.hasNext()) {
            Log.i("parse", "" + reader.peek());
            switch (reader.peek()) {
                case BEGIN_ARRAY: {
                    reader.beginArray();
                    output += "[\n";
                    break;
                }
                case BEGIN_OBJECT: {
                    reader.beginObject();
                    output += "{\n";
                    break;
                }
                case BOOLEAN: {
                    output += reader.nextBoolean() + "\n";
                    break;
                }
                case END_ARRAY: {
                    reader.endArray();
                    output += "]\n";
                    break;
                }
                case END_DOCUMENT: {
                    reader.close();
                    return output;
                }
                case END_OBJECT: {
                    reader.endObject();
                    output += "}\n";
                    break;
                }
                case NAME: {
                    output += "\"" + reader.nextName() + "\": ";
                    break;
                }
                case NULL: {
                    reader.nextNull();
                    output += "null,\n";
                    break;
                }
                case NUMBER: {
                    output += reader.nextDouble() + ",\n";
                    break;
                }
                case STRING: {
                    output += reader.nextString() + ",\n";
                    break;
                }
            }
        }
        return output;
    }
}
