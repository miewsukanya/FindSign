package miewsukanya.com.findsign.arview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import miewsukanya.com.findsign.GPSTracker;
import miewsukanya.com.findsign.utils.PaintUtils;


public class RadarView implements LocationListener {
    /**
     * The screen
     */
    public DataView view;
    /**
     * The radar's range
     */
    float range;
    /**
     * Radius in pixel on screen
     */
    public static float RADIUS = 90;
    /**
     * Position on screen
     */
    static float originX = 0, originY = 0;

    /**
     * You can change the radar color from here.
     */
    static int radarColor = Color.argb(100, 0, 0, 0);

    /**
     * Your current location is defined later
     */
    Location currentLocation;
    Location destinedLocation = new Location("provider");

    /*
     * pass the same set of coordinates to plot POI's on radar
     * */

    /*double[] latitudes = new double[]{16.32348644175188,16.480202875616925,16.464869183517187,16.4593155,16.4708212,16.458162964453987,16.442553,16.466050472661454,16.42827363024214,16.437217117208423,16.4744147}; //กำหนดขนาดของอาเรย์
    double[] longitudes= new double[]{102.79603835195304,102.83281110227108,102.81515311449766,102.8119574,102.8113152,102.83159136772156,102.8297802,102.83200711011888,102.82246414572,102.82639861106873,102.8231184}; //กำหนดขนาดของอาเรย์*/
    /*double[] latitudes = new double[100];
    double[] longitudes = new double[100];*/
    double latitudes[]; //กำหนดขนาดของอาเรย์
    double longitudes[]; //กำหนดขนาดของอาเรย์
    double lat[];
    protected LocationManager locationManager;

    public float[][] coordinateArray = new float[1000][2];

    float angleToShift;
    public float degreetopixel;
    public float bearing;
    public float circleOriginX;
    public float circleOriginY;
    private float mscale;

    public float x = 0;
    public float y = 0;
    public float z = 0;

    float yaw = 0;
    double[] bearings;
    ARView arView = new ARView();
    final int update_interval = 1000; // milliseconds
    public DataView2 dataView2;
    public DataView3 dataView3;
    public DataView4 dataView4;


    public RadarView(Context context, DataView dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();

        //Get lat Lng
        GetLocation getLocation = new GetLocation(RadarView.this);
        getLocation.execute();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, update_interval, 0.0f, this);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, update_interval, 0.0f, netListener);
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            // currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }
    }//RadarView AR_all
    //RadarView Sign45
    public RadarView(Context context, DataView2 dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();

        //Get lat Lng
        GetLocation45 getLocation45 = new GetLocation45(RadarView.this);
        getLocation45.execute();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, update_interval, 0.0f, this);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, update_interval, 0.0f, netListener);
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            // currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }

    }//RadarView AR_all
    //RadarView Sign60
    public RadarView(Context context, DataView3 dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();

        //Get lat Lng
        GetLocation60 getLocation60 = new GetLocation60(RadarView.this);
        getLocation60.execute();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, update_interval, 0.0f, this);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, update_interval, 0.0f, netListener);
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            // currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }
    }//RadarView AR_all
    //RadarView Sign80
    public RadarView(Context context, DataView4 dataView, double[] bearings) {
        this.bearings = bearings;
        calculateMetrics();

        //Get lat Lng
        GetLocation80 getLocation80 = new GetLocation80(RadarView.this);
        getLocation80.execute();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, update_interval, 0.0f, this);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, update_interval, 0.0f, netListener);
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            // currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }
    }//RadarView AR_all


    public void calculateMetrics() {
        circleOriginX = originX + RADIUS;
        circleOriginY = originY + RADIUS;

        /**
         * Range of the RadarView
         */
        range = (float) arView.convertToPix(10) * 30;
        mscale = range / arView.convertToPix((int) RADIUS);
    }

    public void paint(PaintUtils dw, float yaw) {

        this.yaw = yaw;
        dw.setFill(true);
        dw.setColor(radarColor);
        dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

        /** put the markers in it */
        /**
         * Draw dots for each POI
         */
        for (int i = 0; i < lat.length; i++) {
            Log.d("28AprV6", "length:" + lat.length);

            destinedLocation.setLatitude(latitudes[i]);
            destinedLocation.setLongitude(longitudes[i]);
            convLocToVec(currentLocation, destinedLocation);

            float x = this.x / mscale;
            float y = this.z / mscale;

            if (x * x + y * y < RADIUS * RADIUS) {
                dw.setFill(true);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.paintRect(x + RADIUS, y + RADIUS, 2, 2);
            }
        }
    }

    /*public void calculateDistances(PaintUtils dw, float yaw) {
          //Calculate the distance from currentLocation to each POI's one
        for (int i = 0; i < latitudes.length; i++) {
            if (bearings[i] < 0) {
                bearings[i] = 360 - bearings[i];
            }
            if (Math.abs(coordinateArray[i][0] - yaw) > 3) {
                angleToShift = (float) bearings[i] - this.yaw;
                coordinateArray[i][0] = this.yaw;
            } else {
                angleToShift = (float) bearings[i] - coordinateArray[i][0];
            }
            destinedLocation.setLatitude(latitudes[i]);
            destinedLocation.setLongitude(longitudes[i]);
            float[] z = new float[1];
            z[0] = 0;
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destinedLocation.getLatitude(), destinedLocation.getLongitude(), z);
            bearing = currentLocation.bearingTo(destinedLocation);
            this.x = (float) (circleOriginX + 40 * (Math.cos(angleToShift)));
            this.y = (float) (circleOriginY + 40 * (Math.sin(angleToShift)));
            if (x * x + y * y < RADIUS * RADIUS) {
                dw.setFill(true);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.paintRect(x + RADIUS - 1, y + RADIUS - 1, 2, 2);
            }
        }
    }*/

    /**
     * Width on screen
     */
    public float getWidth() {
        return RADIUS * 2;
    }

    /**
     * Height on screen
     */
    public float getHeight() {
        return RADIUS * 2;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void convLocToVec(Location source, Location destination) {
        float[] z = new float[1];
        z[0] = 0;
        Location.distanceBetween(source.getLatitude(), source.getLongitude(), destination
                .getLatitude(), source.getLongitude(), z);

        float[] x = new float[1];
        Location.distanceBetween(source.getLatitude(), source.getLongitude(), source
                .getLatitude(), destination.getLongitude(), x);
        if (source.getLatitude() < destination.getLatitude())
            z[0] *= -1;
        if (source.getLongitude() > destination.getLongitude())
            x[0] *= -1;

        set(x[0], (float) 0, z[0]);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Your current location coordinate here.
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setAltitude((location.getAltitude()));

        //Get lat Lng
        /*GetLocation getLocation = new GetLocation(RadarView.this);
        getLocation.execute();*/
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
            default:
                break;
        }
    }


    private class GetLocation extends AsyncTask<Void, Void, String> {
        //Explicit
        private RadarView dataview;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";
        // private Bitmap imge;

        public GetLocation(RadarView dataview) {
            this.dataview = dataview;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                com.squareup.okhttp.Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("26novV1", "e doIn==>" + e.toString());
                return null;
            }
            //return null;
        }//doInBack
        @Override
        public void onPostExecute(String s) {

            Log.d("26novV1", "Json ==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                // places = new String[jsonArray.length()];
                latitudes = new double[jsonArray.length()];
                longitudes = new double[jsonArray.length()];

                lat = new double[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // places[i] = jsonObject.getString("SignName");
                    latitudes[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    longitudes[i] = Double.parseDouble(jsonObject.getString("Longitude"));

                    lat[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    Log.d("04ArpV3", "lat:" + latitudes[i] + "lng:" + longitudes[i]);

                }//for
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//Getlocation

    private class GetLocation45 extends AsyncTask<Void, Void, String> {
        //Explicit
        private RadarView dataview;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getsign45.php";
        // private Bitmap imge;

        public GetLocation45(RadarView dataview) {
            this.dataview = dataview;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                com.squareup.okhttp.Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("26novV1", "e doIn==>" + e.toString());
                return null;
            }
            //return null;
        }//doInBack
        @Override
        public void onPostExecute(String s) {

            Log.d("26novV1", "Json ==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                // places = new String[jsonArray.length()];
                latitudes = new double[jsonArray.length()];
                longitudes = new double[jsonArray.length()];

                lat = new double[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // places[i] = jsonObject.getString("SignName");
                    latitudes[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    longitudes[i] = Double.parseDouble(jsonObject.getString("Longitude"));

                    lat[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    Log.d("04ArpV3", "lat:" + latitudes[i] + "lng:" + longitudes[i]);

                }//for
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//Getlocation
    private class GetLocation60 extends AsyncTask<Void, Void, String> {
        //Explicit
        private RadarView dataview;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getsign60.php";
        // private Bitmap imge;

        public GetLocation60(RadarView dataview) {
            this.dataview = dataview;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                com.squareup.okhttp.Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("26novV1", "e doIn==>" + e.toString());
                return null;
            }
            //return null;
        }//doInBack
        @Override
        public void onPostExecute(String s) {

            Log.d("26novV1", "Json ==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                // places = new String[jsonArray.length()];
                latitudes = new double[jsonArray.length()];
                longitudes = new double[jsonArray.length()];

                lat = new double[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // places[i] = jsonObject.getString("SignName");
                    latitudes[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    longitudes[i] = Double.parseDouble(jsonObject.getString("Longitude"));

                    lat[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    Log.d("04ArpV3", "lat:" + latitudes[i] + "lng:" + longitudes[i]);

                }//for
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//Getlocation
    private class GetLocation80 extends AsyncTask<Void, Void, String> {
        //Explicit
        private RadarView dataview;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getsign80.php";
        // private Bitmap imge;

        public GetLocation80(RadarView dataview) {
            this.dataview = dataview;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                com.squareup.okhttp.Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("26novV1", "e doIn==>" + e.toString());
                return null;
            }
            //return null;
        }//doInBack
        @Override
        public void onPostExecute(String s) {

            Log.d("26novV1", "Json ==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                // places = new String[jsonArray.length()];
                latitudes = new double[jsonArray.length()];
                longitudes = new double[jsonArray.length()];

                lat = new double[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // places[i] = jsonObject.getString("SignName");
                    latitudes[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    longitudes[i] = Double.parseDouble(jsonObject.getString("Longitude"));

                    lat[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    Log.d("04ArpV3", "lat:" + latitudes[i] + "lng:" + longitudes[i]);

                }//for
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//Getlocation
}//RadarView