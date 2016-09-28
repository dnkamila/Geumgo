package com.idn.ict.geumgo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridViewAdapter extends ArrayAdapter<GridViewItem> {
    private final String TAG = "GridViewAdapter";

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridViewItem> mGridData = new ArrayList<GridViewItem>();

    private String REMOVE_URL = "http://192.168.137.2/delete_log.php?";

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
    public View getView(int position, View convertView, ViewGroup parent) {
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

        GridViewItem item = mGridData.get(position);
        holder.capturedAt.setText(Html.fromHtml(item.getCapuredAt()));
        holder.level.setText(Html.fromHtml(item.getLevel()));
        holder.description.setText(Html.fromHtml(item.getDescription()));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "remove clicked");
            }
        });

        Picasso.with(mContext).load(item.getImageUrl()).into(holder.imageUrl);

        return row;
    }

    static class ViewHolder {
        ImageView imageUrl;
        TextView capturedAt;
        TextView level;
        TextView description;
        ImageView remove;
    }
}