package com.testserverapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;


public class MainActivity extends AppCompatActivity {
    private String a;
    private String b;
    private URL url;
    private final JSONObject jsonObject = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText first = findViewById(R.id.first);
        final EditText second = findViewById(R.id.second);
        Button button = findViewById(R.id.button);
        final TextView textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = String.valueOf(first.getText());
                b = String.valueOf(second.getText());

                if(isOnline()){
                    try {
                        url = new URL("https://5b152b376255.ngrok.io/save"); //ССЫЛКА НА СЕРВЕР
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String c = requestServer();
                    textView.setText(c);
                }
            }
        });
    }

    //метод обращения к серверу
    String requestServer() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<String> future = executorService.submit(task);
        try {
            String response = future.get();
            return response;
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return "null";
    }

    //Поток обращения к серверу
    final Callable<String> task = new Callable<String>() {
        @Override
        public String call() {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                jsonObject.put("id",null);
                jsonObject.put("fio",a);
                jsonObject.put("tel",b);
                BufferedOutputStream stream = new BufferedOutputStream(urlConnection.getOutputStream());
                stream.write((jsonObject.toString().getBytes()));
                stream.flush();
                stream.close();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String string_response = reader.readLine();
                    return string_response;

                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "resp_null";
        }
    };


    @SuppressWarnings("deprecation")
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
