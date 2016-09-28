package com.idn.ict.geumgo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GridViewActivity extends AppCompatActivity {
    private static final String TAG = "GridViewActivity";

    private ImageView imageView;
    private GridView gridView;
    private ProgressBar progressBar;

    private GridViewAdapter gridViewAdapter;
    private ArrayList<GridViewItem> gridViewItems;
    private final String FEED_URL = "http://192.168.137.2/retrieve_log.php";
    private final String UPDATE_STATUS_URL = "http://192.168.137.2/update_status.php";
    private final String RETRIEVE_STATUS_URL =  "http://192.168.137.2/retrieve_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view_activity);

        imageView = (ImageView) findViewById(R.id.grid_item_switch);
        gridView = (GridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "switch clicked");
            }
        });
        
        gridViewItems = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_view_item, gridViewItems);
        gridView.setAdapter(gridViewAdapter);
        
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridViewItem item = (GridViewItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(GridViewActivity.this, DetailsViewActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image_url);

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                intent
                    .putExtra("left", screenLocation[0])
                    .putExtra("top", screenLocation[1])
                    .putExtra("width", imageView.getWidth())
                    .putExtra("height", imageView.getHeight())
                    .putExtra("title", item.getCapuredAt())
                    .putExtra("image", item.getImageUrl());

                startActivity(intent);
            }
        });

        new AsyncHttpTask().execute(FEED_URL);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class RetrieveStatusAsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                gridViewAdapter.setGridData(gridViewItems);
            } else {
                Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
        }
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                gridViewAdapter.setGridData(gridViewItems);
            } else {
                Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
        }
    }

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (null != stream) {
            stream.close();
        }
        return result;
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);

            JSONArray logs = response.getJSONArray("logs");
            int success = response.getInt("success");
            String message = response.getString("message");

            if(success == 1) {
                GridViewItem item;
                for(int ii = 0; ii < logs.length(); ii++) {
                    JSONObject log = (JSONObject) logs.get(ii);

                    item = new GridViewItem();
                    item.setLogId(log.getInt("log_id"));
                    item.setImageUrl(log.getString("image_url"));
                    item.setCapuredAt(log.getString("captured_at"));
                    item.setLevel(log.getString("level"));
                    item.setDescription(log.getString("description"));

                    gridViewItems.add(item);
                }
            }
            else {
                Toast.makeText(this.getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}