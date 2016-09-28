package com.idn.ict.geumgo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridViewAdapter extends ArrayAdapter<GridViewItem> {
    private final String TAG = "GridViewAdapter";

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridViewItem> mGridData = new ArrayList<GridViewItem>();

    private String DELETE_LOG_URL = "http://192.168.137.2/delete_log.php";

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridViewItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<GridViewItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GridViewItem item = mGridData.get(position);

        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.imageUrl = (ImageView) row.findViewById(R.id.grid_item_image_url);
            holder.capturedAt = (TextView) row.findViewById(R.id.grid_item_captured_at);
            holder.level = (TextView) row.findViewById(R.id.grid_item_level);
            holder.description = (TextView) row.findViewById(R.id.grid_item_description);
            holder.remove = (ImageView) row.findViewById(R.id.grid_item_remove);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        int temp = Integer.parseInt(String.valueOf(Html.fromHtml(item.getLevel())));
        switch (temp) {
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    row.setBackground(getContext().getResources().getDrawable(R.drawable.grid_view_item_warning_one));
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    row.setBackground(getContext().getResources().getDrawable(R.drawable.grid_view_item_warning_two));
                }
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    row.setBackground(getContext().getResources().getDrawable(R.drawable.grid_view_item_warning_three));
                }
                break;
        }

        row.setTag(holder);

        holder.capturedAt.setText(Html.fromHtml(item.getCapuredAt()));
        holder.level.setText(Html.fromHtml(item.getLevel()));
        holder.description.setText(Html.fromHtml(item.getDescription()));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "remove clicked");
                new DeleteLogTask().execute(DELETE_LOG_URL, "" + item.getLogId());
                mGridData.remove(position);
                notifyDataSetChanged();
                notifyDataSetInvalidated();
           }
        });

        Picasso.with(mContext).load(item.getImageUrl()).into(holder.imageUrl);

        return row;
    }

    private String streamToString(InputStream stream) throws IOException {
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

    public class DeleteLogTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Log.d(TAG, "UPDATE doInBackground");
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(params[0]);

                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("log_id", params[1]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
                HttpResponse httpResponse = httpclient.execute(httpPost);

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    deleteLogProcessing(response);
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

        }
    }

    private void deleteLogProcessing(String response) {
        Log.d(TAG, response);
    }

    static class ViewHolder {
        ImageView imageUrl;
        TextView capturedAt;
        TextView level;
        TextView description;
        ImageView remove;
    }
}