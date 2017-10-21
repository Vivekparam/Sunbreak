package com.paramv.sunbreak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by vivekparam on 10/17/17.
 */

public class WeatherInfoDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "WeatherInfoDialogFragment";
    ThreeHourForecastData data;

    public void setData(ThreeHourForecastData data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_item_menu, container);
        if (data == null) return v;

        TextView title = (TextView)v.findViewById(R.id.weather_dialog_time);
        title.setText(data.getReadableTime());

        TextView date = (TextView)v.findViewById(R.id.weather_dialog_date);
        date.setText(data.getReadableDate());

        TextView cloudCoverPercentLabel = (TextView)v.findViewById(R.id.cloud_cover_pct_label);
        cloudCoverPercentLabel.setText(data.cloudCover + "% Cloud Cover");

        TextView temperatureLabel = (TextView)v.findViewById(R.id.temperature_label);
        temperatureLabel.setText(data.getReadableTempRange());

        return v;

    }
}
