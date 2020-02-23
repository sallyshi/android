package com.sallylshi.touchstonecalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
//        HttpURLConnection httpURLConnection = null;
//        try {
//            URL url = new URL(MISSION_CLIFFS_URL);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//        }
    }

    private void getHtmlFromWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(MISSION_CLIFFS_URL).get();
                    Element element = doc.select("script[id*=\"timely-calendar-state\"]").first();
                   TextView view = findViewById(R.id.test);
                   runOnUiThread( () ->view.setText(element.data()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
