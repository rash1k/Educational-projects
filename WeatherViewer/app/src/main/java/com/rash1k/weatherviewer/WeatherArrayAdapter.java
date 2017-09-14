package com.rash1k.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rash1k.weatherviewer.model.Weather;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherArrayAdapter extends RecyclerView.Adapter<WeatherArrayAdapter.ViewHolder> {

    private static final String LOG_TAG = WeatherArrayAdapter.class.getSimpleName();
    private WeakReference<Context>  mContext;
    private List<Weather> mWeathers;

    //    Кеш для уже загруженных изображений Bitmap
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    public WeatherArrayAdapter(Context context, List<Weather> weathers) {
        this.mContext = new WeakReference<>(context);
        this.mWeathers = weathers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather day = mWeathers.get(position);
        Log.d(LOG_TAG, "iconURL " + day.iconUrl);
        Context context = mContext.get();

        if (bitmaps.containsKey(day.iconUrl)) {
            holder.conditionImageView.setImageBitmap(bitmaps.get(day.iconUrl));
        } else {
            new LoadImageTask(holder.conditionImageView).execute(day.iconUrl);
        }

        holder.dayTextView.setText
                (context.getString(R.string.day_description, day.dayOfWeek, day.description));

        holder.hiTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        holder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        holder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));
    }

    @Override
    public int getItemCount() {
        return mWeathers != null ? mWeathers.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView conditionImageView;
        private TextView dayTextView;
        private TextView lowTextView;
        private TextView hiTextView;
        private TextView humidityTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            conditionImageView = (ImageView) itemView.findViewById(R.id.conditionImageView);
            dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
            lowTextView = (TextView) itemView.findViewById(R.id.lowTextView);
            hiTextView = (TextView) itemView.findViewById(R.id.hiTextView);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidityTextView);
        }
    }


    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public LoadImageTask(ImageView conditionImageView) {
            this.mImageView = conditionImageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(strings[0], bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}
