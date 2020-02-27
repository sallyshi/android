package com.sallylshi.touchstonecalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {
    private static String MISSION_CLIFFS_URL = "https://calendar.time.ly/rl4r7fx3/stream?tags=151613968&timely_id=timely_0.761031607867843";

    private JsonParser jsonParser;

    private class EventListAdapter extends BaseAdapter {

        List<Event> eventList;

        public EventListAdapter(List<Event> eventList) {
            this.eventList = eventList;
        }

        @Override
        public int getCount() {
            return eventList.size();
        }

        @Override
        public Object getItem(int position) {
            return eventList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.title)).setText(eventList.get(position).title);
            ((TextView) convertView.findViewById(R.id.description)).setText(eventList.get(position).description);

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonParser = new JsonParser();

//        Event ev1 = new Event("Yoga", new Date(), new Date(), "This is a yoga class.", "Mission Cliffs");
//        Event ev2 = new Event("Climbing", new Date(), new Date(), "Climb things.", "Some other place");
//
//        List<Event> events = new ArrayList<>();
//        events.add(ev1);
//        events.add(ev2);

        //ListView listView = findViewById(R.id.list);

       // EventListAdapter eventListAdapter = new EventListAdapter(events);
       // listView.setAdapter(eventListAdapter);
        getHtmlFromWeb();
    }

    private void getHtmlFromWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MISSION_CLIFFS_URL).get();
                    Element element = doc.select("script[id*=\"timely-calendar-state\"]").first();

                    TextView view = findViewById(R.id.test);

                    String json = element.data();
                    final String realjson = json.replaceAll("&q;", "\"");

                    JsonReader reader = new JsonReader(new StringReader(realjson));
                    reader.setLenient(true);

                    final String thisIsreallytheoutput = jsonParser.read(reader);//parse(reader);
                    //final String thisIsreallytheoutput = parse(reader);

                    // Test on UI
                    runOnUiThread(() -> view.setText(thisIsreallytheoutput));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String parse(JsonReader reader) throws IOException {
        String output = "";
        String indent = "";
        int counter = 0;
        while (reader.peek() == JsonToken.END_ARRAY || reader.peek() == JsonToken.END_OBJECT || reader.hasNext()) {
            Log.i("parse", "" + reader.peek());
            switch (reader.peek()) {
                case BEGIN_ARRAY: {
                    reader.beginArray();
                    output += "[\n" + indent;
                    break;
                }
                case BEGIN_OBJECT: {
                    indent += "" + counter++;
                    reader.beginObject();
                    output += "{\n" + indent;
                    break;
                }
                case BOOLEAN: {
                    output += reader.nextBoolean() + "\n" + indent;
                    break;
                }
                case END_ARRAY: {
                    reader.endArray();
                    output += "]\n" + indent;
                    break;
                }
                case END_DOCUMENT: {
                    reader.close();
                    return output;
                }
                case END_OBJECT: {
                    counter--;
                    if (indent.length() > 0) {
                        indent = indent.substring(0, indent.length() - 1);
                    }
                    reader.endObject();
                    output += "}\n" + indent;
                    break;
                }
                case NAME: {
                    output += "\"" + reader.nextName() + "\": ";
                    break;
                }
                case NULL: {
                    reader.nextNull();
                    output += "null,\n" + indent;
                    break;
                }
                case NUMBER: {
                    output += reader.nextDouble() + ",\n" + indent;
                    break;
                }
                case STRING: {
                    output += reader.nextString() + ",\n" + indent;
                    break;
                }
            }
        }
        return output;
    }
}
