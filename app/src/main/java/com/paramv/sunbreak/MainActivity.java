package com.paramv.sunbreak;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String API_KEY = "9cbb051e9228df0d24df5c8c675e2edc";
    public static final String OPEN_WEATHER_FORECAST_URL= "http://api.openweathermap.org/data/2.5/forecast";
    public static final int NUM_DAYS_MAX = 2;

    // Location provider
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION_CODE = 5555;

    // Requestqueue
    RequestQueue queue;

    // Weather data, hourly
    List<ThreeHourForecastData> hourlyForecastData;

    // Handy Views to keep hold of
    AppBarLayout appBar;
    RecyclerView mDataRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    WeatherListAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get location services client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // create a new request queue
        queue = Volley.newRequestQueue(this);

        // initialize list of forecast data
        hourlyForecastData = new LinkedList<ThreeHourForecastData>();

        // set up recycler view and adapter
        mDataRecyclerView = (RecyclerView) findViewById(R.id.weatherContainer);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mDataRecyclerView.setLayoutManager(mLinearLayoutManager);

        // get the collapsing toolbar pointer
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        // get the appBar pointer
        appBar = (AppBarLayout) findViewById(R.id.main_appbar);
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onExpanded(AppBarLayout appBarLayout) {
                if(hourlyForecastData.size() == 0) { getLocationAndPopulateList(); }
                collapsingToolbarLayout.setTitle(" ");
            }

            @Override
            public void onCollapsed(AppBarLayout appBarLayout) {
                collapsingToolbarLayout.setTitle("Sunbreak");
            }

            @Override
            public void onIdle(AppBarLayout appBarLayout) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Get and populate the data
                getLocationAndPopulateList();

                // 1. If view is swiped down, swipe it up.
                appBar.setExpanded(false);
            }
        });
    }

    private void getLocationAndPopulateList() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_CODE);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "Got location!");
                        Log.d(TAG, "Latitude:" + location.getLatitude());
                        Log.d(TAG, "Longitude:" + location.getLongitude());

                        getData(location);
                    }
                });
    }

    private void getData(Location location) {


        String url = OPEN_WEATHER_FORECAST_URL + "?lat=" + location.getLatitude() +
                "&lon=" + location.getLongitude() + "&APPID=" + API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    int numEntries = list.length();

                    Date limitDate = getLimitDate();

                    if(numEntries > 0) {
                        hourlyForecastData.clear();
                    }

                    for(int i = 0; i < numEntries; i++) {
                        // Get the hourly data
                        JSONObject hourObj = list.getJSONObject(i);
                        ThreeHourForecastData hourData = new ThreeHourForecastData();


                        hourData.endTime = new Date((long)hourObj.getInt("dt") * 1000);
                        hourData.startTime = new Date(hourData.endTime.getTime() - 3 * 3600 * 1000);

                        // we only care about the next n days. ignore the rest
                        if(hourData.endTime.after(limitDate)) continue;

                        // Get main data
                        JSONObject mainData = hourObj.getJSONObject("main");

                        hourData.temp = mainData.getDouble("temp");
                        hourData.minTemp = mainData.getDouble("temp_min");
                        hourData.maxTemp = mainData.getDouble("temp_max");

                        // get weather desc data
                        JSONObject weatherData = hourObj.getJSONArray("weather").getJSONObject(0);
                        hourData.weatherId = weatherData.getInt("id");
                        hourData.weatherStringDesc = weatherData.getString("description");
                        hourData.weatherStringMain = weatherData.getString("main");

                        // get cloud cover data
                        JSONObject cloudData = hourObj.getJSONObject("clouds");
                        hourData.cloudCover = cloudData.getInt("all");

                        Log.d(TAG, "At " + hourData.endTime.toString() + " weather is " + hourData.weatherStringMain);
                        hourlyForecastData.add(hourData);
                    }

                    // We have the data, now populate
                    mAdapter.notifyItemInserted(hourlyForecastData.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // add progressbar
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new Toolbar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        progressBar.setIndeterminate(true);
//        dataView.setEmptyView(progressBar);
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        root.addView(progressBar);

        mAdapter = new WeatherListAdapter();
        mAdapter.setWeather(hourlyForecastData);
        mDataRecyclerView.setAdapter(mAdapter);

        queue.add(request);
    }

    private Date getLimitDate() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, NUM_DAYS_MAX);
        return c.getTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        Log.d(TAG, "Permissions result received");
        switch(requestCode) {
            case REQUEST_LOCATION_CODE: {
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted. continue with flow.
                    getLocationAndPopulateList();
                } else {
                }
                return;
            }
            default:
                Log.d(TAG, "Unknown request code:" + requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
