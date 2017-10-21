package com.paramv.sunbreak;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by vivekparam on 9/17/17.
 */

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ForecastDataHolder> {


    private List<ThreeHourForecastData> weatherList;

    public static class ForecastDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String FORECAST_KEY = "FORECAST";

        View rootView;

        View mCloudPercentIndicatorView;
        TextView mTimeView;
        TextView mCloudCoverPercentTextView;

        public ForecastDataHolder(View v) {
            super(v);

            rootView = v;
            mCloudPercentIndicatorView = v.findViewById(R.id.cloudPercentIndicator);
            mTimeView = (TextView) v.findViewById(R.id.time);
            mCloudCoverPercentTextView = (TextView) v.findViewById(R.id.cloudCoverPercentText);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, mTimeView.getText() +  " Clicked");

            WeatherInfoDialogFragment bottomSheet = new WeatherInfoDialogFragment();
            bottomSheet.setData((ThreeHourForecastData)v.getTag());
            bottomSheet.show(((MainActivity)v.getContext()).getSupportFragmentManager(),
                    WeatherInfoDialogFragment.TAG);

        }

        public void bindForecast(ThreeHourForecastData data) {
            ((LinearLayout.LayoutParams) mCloudPercentIndicatorView.getLayoutParams()).weight = data.cloudCover;

            rootView.setTag(data);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            mTimeView.setText(sdf.format(data.endTime));

            mCloudCoverPercentTextView.setText("Cloud Cover: " + data.cloudCover + "%");

            if (data.cloudCover < 35) {
                mCloudPercentIndicatorView.setBackground(ContextCompat.getDrawable(mCloudPercentIndicatorView.getContext(), R.drawable.layout_cloudbar_sunny_bg));
                mTimeView.setTextColor(ContextCompat.getColor(mTimeView.getContext(), R.color.colorDarkGrey));
            }
        }
    }

    public void setWeather(List<ThreeHourForecastData> weatherList) {
        this.weatherList = weatherList;
    }


    @Override
    public WeatherListAdapter.ForecastDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);
        return new ForecastDataHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ForecastDataHolder holder, int position) {
        holder.bindForecast(weatherList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if(weatherList == null) return 0;
        return weatherList.size();
    }
}
