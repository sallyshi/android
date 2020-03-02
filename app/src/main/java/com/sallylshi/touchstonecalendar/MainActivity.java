package com.sallylshi.touchstonecalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {
    private static String MISSION_CLIFFS_URL = "https://calendar.time.ly/rl4r7fx3/stream?&timely_id=timely_0.761031607867843";

    private class EventListAdapter extends BaseAdapter {
        List<Event> eventList;

        EventListAdapter(List<Event> eventList) {
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

            TextView title = convertView.findViewById(R.id.title);
            TextView description = convertView.findViewById(R.id.description);
            TextView start_time = convertView.findViewById(R.id.start_time);
            TextView end_time = convertView.findViewById(R.id.end_time);
            TextView cost_type = convertView.findViewById(R.id.cost_type);

            title.setText(String.format("%s%s", getString(R.string.title), eventList.get(position).title));
            description.setText(String.format("%s%s", getString(R.string.description), eventList.get(position).description
                            .replaceAll("&a;hellip;", "...")
                            .replaceAll("&s;", "'")
                            .replaceAll("&a;", "&")
                            .replaceAll("&q;", "\"")));
            cost_type.setText(String.format("%s%s", getString(R.string.cost), eventList.get(position).costType));
            start_time.setText(String.format("%s%s", getString(R.string.start_time), eventList.get(position).start.toString()));
            end_time.setText(String.format("%s%s", getString(R.string.end_time), eventList.get(position).end.toString()));

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            Spinner spinner = findViewById(R.id.dropdown_title);
            ArrayAdapter<CharSequence > adapter = ArrayAdapter.createFromResource(this, R.array.dropdown_title_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Filter filter = new Filter() {
//                        @Override
//                        protected FilterResults performFiltering(CharSequence constraint) {
//                            return null;
//                        }
//
//                        @Override
//                        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//                        }
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        getHtmlFromWeb();
    }

    private void getHtmlFromWeb() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(MISSION_CLIFFS_URL).get();
                Element element = doc.select("script[id*=\"timely-calendar-state\"]").first();

                String json = element.data();
                final String strippedJson = json.replaceAll("&q;", "\"");
                JsonParser jsonParser = new JsonParser();
                JsonReader reader = new JsonReader(new StringReader(strippedJson));
                reader.setLenient(true);

                ListView listView = findViewById(R.id.list);

                EventListAdapter eventListAdapter = new EventListAdapter(jsonParser.read(reader));
                runOnUiThread(() -> listView.setAdapter(eventListAdapter));

                // TextView view = findViewById(R.id.test);
                // final String thisIsreallytheoutput = jsonParser.read(reader);
                // runOnUiThread(() -> view.setText(thisIsreallytheoutput));

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * This parser outputs the original Json read. It is only used for testing.
     */
    public String testParser(JsonReader reader) throws IOException {
        StringBuilder output = new StringBuilder();
        String indent = "";
        int counter = 0;
        while (reader.peek() == JsonToken.END_ARRAY || reader.peek() == JsonToken.END_OBJECT || reader.hasNext()) {
            Log.i("parse", "" + reader.peek());
            switch (reader.peek()) {
                case BEGIN_ARRAY: {
                    reader.beginArray();
                    output.append("[\n").append(indent);
                    break;
                }
                case BEGIN_OBJECT: {
                    indent += "" + counter++;
                    reader.beginObject();
                    output.append("{\n").append(indent);
                    break;
                }
                case BOOLEAN: {
                    output.append(reader.nextBoolean()).append("\n").append(indent);
                    break;
                }
                case END_ARRAY: {
                    reader.endArray();
                    output.append("]\n").append(indent);
                    break;
                }
                case END_DOCUMENT: {
                    reader.close();
                    return output.toString();
                }
                case END_OBJECT: {
                    counter--;
                    if (indent.length() > 0) {
                        indent = indent.substring(0, indent.length() - 1);
                    }
                    reader.endObject();
                    output.append("}\n").append(indent);
                    break;
                }
                case NAME: {
                    output.append("\"").append(reader.nextName()).append("\": ");
                    break;
                }
                case NULL: {
                    reader.nextNull();
                    output.append("null,\n").append(indent);
                    break;
                }
                case NUMBER: {
                    output.append(reader.nextDouble()).append(",\n").append(indent);
                    break;
                }
                case STRING: {
                    output.append(reader.nextString()).append(",\n").append(indent);
                    break;
                }
            }
        }
        return output.toString();
    }
}
