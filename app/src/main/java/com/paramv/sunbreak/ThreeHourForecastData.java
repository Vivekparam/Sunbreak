package com.paramv.sunbreak;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vivekparam on 9/17/17.
 */
public class ThreeHourForecastData {

    public double temp;
    public double minTemp;
    public double maxTemp;

    public double pressure; // pressure sea level by default , hPa

    public double seaLevel; // pressure on sea level, hPa

    public double groundLevel; // pressure at ground level, hPa

    public int humidity; // relative %

    public int weatherId; // condition id

    public String weatherStringMain; // string weather name

    public String weatherStringDesc; // string weather desc

    public int cloudCover; // cloud cover percentage

    public int windSpeed; // meters/second

    public double windDirection; // degrees

    public double rainVolume; // volume for last three hours, mm

    public double snowVolume; // snow volume last three hours

    public Date startTime;

    public Date endTime; // endTime of calculation


    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm aa");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd'th'");


    public String getReadableTime() {
        return timeFormatter.format(startTime) + " to " + timeFormatter.format(endTime);
    }

    public String getReadableDate() {
        return dateFormatter.format((endTime));
    }

    public String getReadableTempRange() {
        return kelvinToFarenheit(temp) + "°";
    }

    private double kelvinToFarenheit(double k) {
        return Math.round(k * 9 / 5 - 459.67);
    }
}
