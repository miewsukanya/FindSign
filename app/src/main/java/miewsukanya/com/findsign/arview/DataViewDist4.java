package miewsukanya.com.findsign.arview;

/**
 * Created by Sukanya Boonpun on 01/05/2560.
 */

/**
 * Created by Sukanya Boonpun on 31/03/2560.
 */
//ค้นหาทุกป้ายในระยะ 1 Km.
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import miewsukanya.com.findsign.R;
import miewsukanya.com.findsign.utils.PaintUtils;
import miewsukanya.com.findsign.utils.RadarLines;

public class DataViewDist4 extends Activity implements LocationListener{

    RelativeLayout.LayoutParams[] layoutParams;
    RelativeLayout[] locationMarkerView;
    RelativeLayout.LayoutParams[] subjectImageViewParams;
    ImageView[] subjectImageView;
    RelativeLayout.LayoutParams[] subjectTextViewParams;
    TextView[] locationTextView;
    RelativeLayout.LayoutParams[] distanceViewParams;
    TextView[] distanceTextView;

    String places[]; //กำหนดขนาดของอาเรย์
    double latitudes[]; //กำหนดขนาดของอาเรย์
    double longitudes[]; //กำหนดขนาดของอาเรย์

    double lat[];
    public int[][] coordinateArray = new int[100][2];
    double[] exIntArray = new  double[1000]; //กำหนดขนาดอาเรย์ของระยะห่างที่ลบจากแลตลองในดาต้าเบส

    /*     *  Array or Array lists of latitude and longitude to plot
     *  In your case you can populate with an ArrayList
     * */
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    int[] nextXofText;
    ArrayList<Integer> nextYofText = new ArrayList<Integer>();

    double[] bearings;
    float angleToShift;
    float yPosition;
    Location currentLocation;
    Location destinedLocation = new Location("provider");
    /**
     * is the view Inited?
     */
    boolean isInitDist4 = false;
    boolean isDrawing = true;
    boolean isFirstEntry;
    Context _context;

    /**
     * width and height of the view
     */
    int width, height;
    Camera camera;

    float yawPrevious;
    float yaw = 0;
    float pitch = 0;
    float roll = 0;

    DisplayMetrics displayMetrics;
    RadarView radarPoints;

    RadarLines lrl = new RadarLines();
    RadarLines rrl = new RadarLines();
    float rx = 10, ry = 20;
    public float addX = 0, addY = 0;
    public float degreetopixelWidth;
    public float degreetopixelHeight;
    public float pixelstodp;
    public float bearing;

    public int locationBlockWidth;
    public int locationBlockHeight;

    public float deltaX;
    public float deltaY;
    Bitmap bmp;
    final int update_interval = 1000; // milliseconds

    public DataViewDist4(Context ctx) {
        this._context = ctx;

        //Get lat Lng
        GetLocation getLocation = new GetLocation(DataViewDist4.this);
        getLocation.execute();

        CalculateDistance calculateDistance = new CalculateDistance(DataViewDist4.this);
        calculateDistance.execute();

        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, update_interval, 0.0f, this);
            currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        } catch (Exception e) {

        }
    }//DataView

    @Override
    public void onLocationChanged(Location location) {
        // Your current location coordinate here.
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setAltitude((location.getAltitude()));
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
    class GetLocation extends AsyncTask<Void, Void, String> {
        //Explicit
        private DataViewDist4 dataview;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";
        // private Bitmap imge;

        public GetLocation(DataViewDist4 dataview) {
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

                places = new String[jsonArray.length()];
                latitudes = new double[jsonArray.length()];
                longitudes = new double[jsonArray.length()];
                lat = new double[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    places[i] = jsonObject.getString("SignName").trim();
                    latitudes[i] = Double.parseDouble(jsonObject.getString("Latitude"));
                    longitudes[i] = Double.parseDouble(jsonObject.getString("Longitude"));

                    lat[i] = Double.parseDouble(jsonObject.getString("Latitude"));

                    Log.d("24AprV5", "name:" + places[i]+":"+latitudes[i]+":"+longitudes[i] +":"+lat.length +":"+jsonObject.getString("SignName"));
                    Log.d("28AprV7", String.valueOf(lat[i]));
                }//for
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error", e.toString());
            }
        }//onPost
    }//Getlocation

    public boolean isInitDist4() {
        return isInitDist4;
    }

    double seekbar = 6.0;
    double seekbar2 = 10.0;
    public void init4(int widthInit, int heightInit, Camera camera, DisplayMetrics displayMetrics, RelativeLayout rel) {
        Log.d("okmiew", String.valueOf(lat.length)); //เบรคข้อมูลก่อนเข้าฟังก์ชันวาดเออาร์
        Log.d("okmiew2", String.valueOf(isInitDist4)); //เบรคข้อมูลก่อนเข้าฟังก์ชันวาดเออาร์

        try {
            layoutParams = new RelativeLayout.LayoutParams[lat.length];
            locationMarkerView = new RelativeLayout[lat.length];

            subjectImageViewParams = new RelativeLayout.LayoutParams[lat.length];
            subjectImageView = new ImageView[lat.length];

            subjectTextViewParams = new RelativeLayout.LayoutParams[lat.length];
            locationTextView = new TextView[lat.length];

            nextXofText = new int[lat.length];
            Log.e("latlenght", String.valueOf(lat.length));
            /**
             * Set POI's View
             */
            for (int i = 0; i < latitudes.length; i++) {
                Log.d("18MarV2", "length:" + latitudes.length);
                /**
                 * POI's Layout Creation
                 */
                layoutParams[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams[i].setMargins(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2, 0, 0);
                locationMarkerView[i] = new RelativeLayout(_context);

                //set AR Show AR ทั้งหมด ในระยะ 5 km.
                if ( places[i].equals("Sign80") && exIntArray[i] < seekbar && exIntArray[i] <= seekbar2)  {
                    locationMarkerView[i].setBackgroundResource(R.drawable.but_80);
                    Log.d("17MarV1", "Location:" + lat[i] +":"+exIntArray[i]);
                }
                //set AR Show AR ทั้งหมด ในระยะ 5 km.

                locationMarkerView[i].setId(i);
                locationMarkerView[i].setLayoutParams(layoutParams[i]);

                /**
                 * POI's Icon Creation
                 */
                subjectImageViewParams[i] = new RelativeLayout.LayoutParams(100, 100);
                subjectImageViewParams[i].setMargins(15, 15, 15, 15);
                subjectImageViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                subjectImageView[i] = new ImageView(_context);
                subjectImageView[i].setId(i);
                subjectImageView[i].setLayoutParams(subjectImageViewParams[i]);
                locationMarkerView[i].addView(subjectImageView[i]);

                /**
                 * POI's Title Creation
                 */
                subjectTextViewParams[i] = new RelativeLayout.LayoutParams(300, 100);
                subjectTextViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_RIGHT, subjectImageView[i].getId());
                subjectTextViewParams[i].topMargin = 1000;
                locationTextView[i] = new TextView(_context);
                locationTextView[i].setText(checkTextToDisplay(places[i]));
                locationTextView[i].setId(i);
                locationTextView[i].setLayoutParams(subjectTextViewParams[i]);
                locationMarkerView[i].addView(locationTextView[i]);
                /**
                 * Adding the components to the View
                 */
                rel.addView(locationMarkerView[i]);
            }//for

            bmp = BitmapFactory.decodeResource(_context.getResources(), R.drawable.icon);

            this.displayMetrics = displayMetrics;
            this.degreetopixelWidth = this.displayMetrics.widthPixels / camera.getParameters().getHorizontalViewAngle();
            this.degreetopixelHeight = this.displayMetrics.heightPixels / camera.getParameters().getVerticalViewAngle();

            bearings = new double[lat.length];

            if (bearing < 0)
                bearing = 360 + bearing;

            for (int i = 0; i < lat.length; i++) {
                destinedLocation.setLatitude(latitudes[i]);
                destinedLocation.setLongitude(longitudes[i]);
                bearing = currentLocation.bearingTo(destinedLocation);

                if (bearing < 0) {
                    bearing = 360 + bearing;
                }
                bearings[i] = bearing;
            }
            radarPoints = new RadarView(this._context, this, bearings);
            this.camera = camera;
            width = widthInit;
            height = heightInit;

            /**
             * Set Radar's Lines
             */
            lrl.set(0, -RadarView.RADIUS);
            lrl.rotate(miewsukanya.com.findsign.utils.Camera.DEFAULT_VIEW_ANGLE / 2);
            lrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
            rrl.set(0, -RadarView.RADIUS);
            rrl.rotate(-miewsukanya.com.findsign.utils.Camera.DEFAULT_VIEW_ANGLE / 2);
            rrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            /*
             * initialization is done, so dont call init() again.
             * */
        isInitDist4 = true;
    }//init
    private class CalculateDistance extends AsyncTask<Void, Void, String> {
        //Explicit
        private DataViewDist4 context;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";

        public CalculateDistance(DataViewDist4 context) {
            this.context = context;

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
        protected void onPostExecute(String s) {

            Log.d("26novV1", "Json ==>" + s);
            try {

                JSONArray jsonArray = new JSONArray(s);

                for (int i = 0; i < jsonArray.length(); i += 1) {
                    //Get Json from Database
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strLat = jsonObject.getString("Latitude");
                    String strLng = jsonObject.getString("Longitude");


                    double latitude = currentLocation.getLatitude();
                    double longitude = currentLocation.getLongitude();
                    Log.d("26MarV8", "Marker" + "Lat:" + latitude + "Lng:" + longitude);

                    int Radius = 6371; // radius of earth in Km
                    double lat2, lng2;
                    double lat1 = latitude; //start Lat พิกัดจาก gps มือถือ
                    double lng1 = longitude; //start Lng พิกัดจาก gps มือถือ

                    lat2 = Double.parseDouble(strLat); //end Lat พิกัดที่ดึงจากดาต้าเบส แปลงสตริงให้เป็น double
                    lng2 = Double.parseDouble(strLng); //eng Lng พิกัดที่ดึงจากดาต้าเบส แปลงสตริงให้เป็น double

                    //สูตรคำนวณระยะห่างระหว่างพิกัดมือถือกับพิกัดป้ายจากดาต้าเบส
                    double dLat = Math.toRadians(lat2 - lat1);
                    double dLon = Math.toRadians(lng2 - lng1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                            + Math.cos(Math.toRadians(lat1))
                            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                            * Math.sin(dLon / 2);
                    double c = 2 * Math.asin(Math.sqrt(a));
                    double valueResult = Radius * c * 1000;
                    double km = valueResult / 1;
                    DecimalFormat newFormat = new DecimalFormat("####");
                    int kmInDec = Integer.valueOf(newFormat.format(km));
                    double meter = valueResult / 1000;
                    DecimalFormat newFormat2 = new DecimalFormat("#.##");
                    double  meterInKm = Double.valueOf(newFormat2.format(meter));
                    Log.d("26MarV7", " Meter : " + meterInKm);
                    //เก็บค่า meterInKm เป็นอาเรย์
                    exIntArray[i] = meterInKm;
                    Log.d("27MarV1", " Meter : " + exIntArray[i] + "==>"+meterInKm);

                }//for
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//CalculateDistance
    public void draw(PaintUtils dw, float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        /**
         *  Draw Radar
         */
        String dirTxt = "";
        int bearing = (int) this.yaw;
        int range = (int) (this.yaw / (360f / 16f));
        if (range == 15 || range == 0) dirTxt = "N";
        else if (range == 1 || range == 2) dirTxt = "NE";
        else if (range == 3 || range == 4) dirTxt = "E";
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt = "S";
        else if (range == 9 || range == 10) dirTxt = "SW";
        else if (range == 11 || range == 12) dirTxt = "W";
        else if (range == 13 || range == 14) dirTxt = "NW";

        radarPoints.dataViewDist4 = this;

        dw.paintObj(radarPoints, rx + PaintUtils.XPADDING, ry + PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
        dw.setFill(false);

        /**
         * Draw Radar Lines
         */
        dw.setColor(Color.argb(100, 255, 255, 255));
        dw.paintLine(lrl.x, lrl.y, rx + RadarView.RADIUS, ry + RadarView.RADIUS);
        dw.paintLine(rrl.x, rrl.y, rx + RadarView.RADIUS, ry + RadarView.RADIUS);
        dw.setColor(Color.rgb(255, 255, 255));
        dw.setFontSize(12);
        radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx + RadarView.RADIUS, ry - 5, true, false, -1);

        drawTextBlock(dw);
    }

    void drawPOI(PaintUtils dw, float yaw) {
        if (isDrawing) {
            dw.paintObj(radarPoints, rx + PaintUtils.XPADDING, ry + PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
            isDrawing = false;
        }
    }

    void radarText(PaintUtils dw, String txt, float x, float y, boolean bg, boolean isLocationBlock, int count) {

        float padw = 4, padh = 2;
        float w = dw.getTextWidth(txt) + padw * 2;
        float h;
        if (isLocationBlock) {
            h = dw.getTextAsc() + dw.getTextDesc() + padh * 2 + 10;
        } else {
            h = dw.getTextAsc() + dw.getTextDesc() + padh * 2;
        }
        if (bg) {

            if (isLocationBlock) {
                layoutParams[count].setMargins((int) (x - w / 2 - 10), (int) (y - h / 2 - 10), 0, 0);
                layoutParams[count].height = 300;
                layoutParams[count].width = 300;
                locationMarkerView[count].setLayoutParams(layoutParams[count]);

            } else {
                dw.setColor(Color.rgb(0, 0, 0));
                dw.setFill(true);
                dw.paintRect((x - w / 2) + PaintUtils.XPADDING, (y - h / 2) + PaintUtils.YPADDING, w, h);
                pixelstodp = (padw + x - w / 2) / ((displayMetrics.density) / 160);
                dw.setColor(Color.rgb(255, 255, 255));
                dw.setFill(false);
                dw.paintText((padw + x - w / 2) + PaintUtils.XPADDING, ((padh + dw.getTextAsc() + y - h / 2)) + PaintUtils.YPADDING, txt);
            }
        }

    }//radarText

    /**
     * Check if the string contains more than 15 characters
     * if so, write the first 15 characters then "..."
     *
     * @param str
     * @return
     */
    String checkTextToDisplay(String str) {

        if (str.length() > 15) {
            str = str.substring(0, 15) + "...";
        }
        return str;

    }

    void drawTextBlock(PaintUtils dw) {

        for (int i = 0; i < bearings.length; i++) {
            if (bearings[i] < 0) {

                if (this.pitch != 90) {
                    yPosition = (this.pitch - 90) * this.degreetopixelHeight + 200;
                } else {
                    yPosition = (float) this.height / 2;
                }

                bearings[i] = 360 - bearings[i];
                angleToShift = (float) bearings[i] - this.yaw;
                nextXofText[i] = (int) (angleToShift * degreetopixelWidth);
                yawPrevious = this.yaw;
                isDrawing = true;

                coordinateArray[i][0] = nextXofText[i];
                coordinateArray[i][1] = (int) yPosition;

            } else {
                angleToShift = (float) bearings[i] - this.yaw;

                if (this.pitch != 90) {
                    yPosition = (this.pitch - 90) * this.degreetopixelHeight + 200;
                } else {
                    yPosition = (float) this.height / 2;
                }


                nextXofText[i] = (int) ((displayMetrics.widthPixels / 2) + (angleToShift * degreetopixelWidth));
                if (Math.abs(coordinateArray[i][0] - nextXofText[i]) > 50) {
                    radarText(dw, places[i], (nextXofText[i]), yPosition, true, true, i);
                    coordinateArray[i][0] = (int) ((displayMetrics.widthPixels / 2) + (angleToShift * degreetopixelWidth));
                    coordinateArray[i][1] = (int) yPosition;

                    isDrawing = true;
                } else {
                    radarText(dw, places[i], coordinateArray[i][0], yPosition, true, true, i);
                    isDrawing = false;
                }
            }
        }
    }
}//DataView
