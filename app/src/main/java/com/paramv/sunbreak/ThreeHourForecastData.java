package com.paramv.sunbreak;

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

    public Date dateTime; // datetime of calculation
}
