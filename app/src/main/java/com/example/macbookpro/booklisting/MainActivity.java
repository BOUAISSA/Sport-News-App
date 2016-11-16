package com.example.macbookpro.booklisting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<News> NewsFeed;
    ArrayList<News> NewsFeed2;
    NewsAdapter adapter;
    String JSON_String;
    TextView noresults;
    String IsthereUpdate="yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new JSONTask().execute("http://content.guardianapis.com/search?q=sport&show-fields=thumbnail&show-references=author&api-key=test");
        NewsFeed = new ArrayList<News>();
        NewsFeed2 = new ArrayList<News>();
        noresults= (TextView)findViewById(R.id.NoResults);
        noresults.setVisibility(View.GONE);
        // Adapter
        adapter = new NewsAdapter(this, NewsFeed);
        final ListView Newslist = (ListView) findViewById(R.id.list_view_news);
        Newslist.setAdapter(adapter);
        //when an Item is clicked
        Newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String urlString = NewsFeed.get(position).getForBrowser();
                Intent i = new Intent(Intent.ACTION_VIEW);
                PackageManager packageManager = getPackageManager();
                //making sure that there is an app that will open the intent
                if (i.resolveActivity(packageManager) != null) {
                i.setData(Uri.parse(urlString));
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this,"You have no Browser App installed in your Phone",Toast.LENGTH_LONG).show();
                }
            }
        });
        //refrech
        final Handler handler = new Handler();

        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                if (NewsFeed2.size()==NewsFeed.size()){
                    IsthereUpdate="no";}
                else{
                    for (News x : NewsFeed2){
                        if (!NewsFeed.contains(x))
                            NewsFeed.add(x);

                    }}
                new JSONTask().execute("http://content.guardianapis.com/search?q=sport&show-fields=thumbnail&show-references=author&api-key=test");
                handler.postDelayed(this, 600 * 1000);
                NewsFeed2.add(NewsFeed.get(2));
            }
        };
        handler.postDelayed(refresh, 600 * 1000);
    }

    public class JSONTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                //handling Serveur error
                String  result = "";
                int statusCode = connection.getResponseCode();
                switch (statusCode) {
                    case 400:
                        result+="Error 400 - Bad request.";
                        return  result;
                    case 401:
                        result+="Error 401 - Unauthorized request.";
                        return result;
                                     }
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSON_String=result;
            if (JSON_String == "Error 400 - Bad request.") {
                Toast.makeText(getApplicationContext(), JSON_String, Toast.LENGTH_SHORT).show();
            }
            else if (JSON_String == "Error 400 - Bad request."){
                Toast.makeText(getApplicationContext(), JSON_String, Toast.LENGTH_SHORT).show();
            }else {
                //checking if there is internet first
                if (new CheckNetwork(getApplicationContext()).isNetworkAvailable()) {
                    NewsFeed.clear();
                    adapter.notifyDataSetChanged();
                    //
                    JSONObject responces=null;
                    int total=0;
                    JSONArray resultsJSONArray;
                    String title;
                    String date;
                    String linkForBrowser;
                    String thumbnail;
                    try {
                        responces = new JSONObject(JSON_String);
                        responces = responces.getJSONObject("response");
                        total = Integer.parseInt(responces.getString("total"));
                        resultsJSONArray = responces.getJSONArray("results");
                        for(int i=0; i<resultsJSONArray.length();i++) {
                            title = resultsJSONArray.getJSONObject(i).getString("webTitle");
                            date = resultsJSONArray.getJSONObject(i).getString("webPublicationDate");
                            linkForBrowser = resultsJSONArray.getJSONObject(i).getString("webUrl");
                            thumbnail = resultsJSONArray.getJSONObject(i).getJSONObject("fields").getString("thumbnail");
                            URL url = new URL(thumbnail);
                            if(IsthereUpdate=="yes")
                                NewsFeed.add(new News(title, date, url, linkForBrowser));

                            NewsFeed2.add(new News(title, date, url, linkForBrowser));
                        }
                    } catch (JSONException e) {
                            if (total== 0){
                                Toast.makeText(MainActivity.this,"No results! would you type only one word",Toast.LENGTH_LONG).show();
                                noresults.setVisibility(View.VISIBLE);
                            }else
                                noresults.setVisibility(View.GONE);
                            e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (total== 0){
                        noresults.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "no internet!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}



