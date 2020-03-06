package com.sallylshi.touchstonecalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Filterable;
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
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {
    private static String MISSION_CLIFFS_URL = "https://calendar.time.ly/rl4r7fx3/stream?&timely_id=timely_0.761031607867843";
    private static String REAL_JSON = "https://timelyapp.time.ly/api/calendars/13168648/events?group_by_date=1";
    private static String START_DATE = "start_date";
    private static String PAGE = "page";
    private static String PER_PAGE = "per_page";

    private class EventListAdapter extends BaseAdapter implements Filterable {
        List<Event> eventList;
        List<Event> filteredEventList;
        Filter filter;

        EventListAdapter(List<Event> eventList) {
            this.eventList = filteredEventList = eventList;
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<Event> filteredEvents = new ArrayList<>();
                    if (constraint != null && constraint.length() > 0) {
                        for (Event event : eventList) {
                            if (event.location.equals(constraint)) {
                                filteredEvents.add(event);
                            }
                        }
                        results.count = filteredEvents.size();
                        results.values = filteredEvents;
                    } else {
                        results.count = eventList.size();
                        results.values = eventList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredEventList = (List<Event>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getCount() {
            return filteredEventList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredEventList.get(position);
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

            title.setText(String.format("%s%s", getString(R.string.title), filteredEventList.get(position).title));
            description.setText(String.format("%s%s", getString(R.string.description), filteredEventList.get(position).description
                    .replaceAll("&a;hellip;", "...")
                    .replaceAll("&s;", "'")
                    .replaceAll("&a;", "&")
                    .replaceAll("&q;", "\"")));
            cost_type.setText(String.format("%s%s", getString(R.string.cost), filteredEventList.get(position).costType));
            start_time.setText(String.format("%s%s", getString(R.string.start_time), filteredEventList.get(position).start.toString()));
            end_time.setText(String.format("%s%s", getString(R.string.end_time), filteredEventList.get(position).end.toString()));

            return convertView;
        }

        @Override
        public Filter getFilter() {
            return filter;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHtmlFromWeb();

        Spinner spinner = findViewById(R.id.dropdown_title);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dropdown_title_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private String getFinalUrl(HashMap<String, String> map) {
        String result = "";

        for (String key : map.keySet()) {
            result += "&" + key + "=" + map.get(key);
        }
        return result;
    }

    private void getHtmlFromWeb() {
        new Thread(() -> {
            try {
                String page_value = "1";
                String per_page_value = "20";

                //&page=1&per_page=20&start_date=2020-03-05"
                HashMap<String, String> map = new HashMap<>();

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                String start_date_value = f.format(currentTime);

                map.put(PAGE, page_value);
                map.put(PER_PAGE, per_page_value);
                map.put(START_DATE, start_date_value);

//                Document doc = Jsoup.connect(MISSION_CLIFFS_URL).get();
//                Element element = doc.select("script[id*=\"timely-calendar-state\"]").first();
//                String json = element.data();
//                final String strippedJson = json.replaceAll("&q;", "\"");

                URL url = new URL(REAL_JSON+getFinalUrl(map));
                URLConnection urlConnection = url.openConnection();

                JsonParser jsonParser = new JsonParser();
                JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()));
                reader.setLenient(true);

                ListView listView = findViewById(R.id.list);
                Spinner spinner = findViewById(R.id.dropdown_title);
                String[] spinnerList = getResources().getStringArray(R.array.dropdown_title_array);
                runOnUiThread(() -> {
                    try {
                        EventListAdapter eventListAdapter = new EventListAdapter(jsonParser.read(reader));
                        listView.setAdapter(eventListAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CharSequence c = position == 0 ? "" : spinnerList[position];
                                eventListAdapter.getFilter().filter(c);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
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
