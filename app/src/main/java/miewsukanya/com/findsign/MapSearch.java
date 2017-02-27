package miewsukanya.com.findsign;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.internal.LocationRequestUpdateData;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;

import static android.R.attr.focusable;
import static android.R.attr.id;
import static android.R.attr.max;
import static android.os.Build.ID;
import static miewsukanya.com.findsign.LocationService.distance;
import static miewsukanya.com.findsign.R.array.idDistance;
import static miewsukanya.com.findsign.R.array.idSign;
import static miewsukanya.com.findsign.R.id.seekBar;
import static miewsukanya.com.findsign.R.id.textView;
import static miewsukanya.com.findsign.R.id.txt_speed;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {


    //about calculate speed
    static ProgressDialog locate;
    static int p=0;
    static long endTime;
    static long startTime;
    static TextView speed;
    LocationService myService;
    static boolean status;
    private static final int MY_PERMISSION_REQUEST = 5;
    //Explicit
    private String[] perms = {"android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.INTERNET"};
    GPSTracker gps;
    private LocationManager locationManager;
    private LocationListener locationListener;
    GoogleMap mGoogleMap;
    EditText edt_distance;
    Button btn_getLatLng,btn_return;
    TextView txtView_gpsLat,txtView_gpsLng,txt_Distance,txtDistance,txt_speed,txtidMap;
    TextView txtidSignSetting, txtidDistSetting;
    //GoogleApiClient mGoogleClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            // Toast.makeText(this, "Perfect!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_map_search);

            GetMap getMap = new GetMap(MapSearch.this);
            getMap.execute();
            initMap();

            CalculateDistance calculatedistance = new CalculateDistance(MapSearch.this);
            calculatedistance.execute();

        } else {
            //No google map layout
        }

        txtView_gpsLat = (TextView) findViewById(R.id.txtView_gpsLat);
        txtView_gpsLng = (TextView) findViewById(R.id.txtView_gpsLng);
        btn_getLatLng = (Button) findViewById(R.id.btn_getLatLng);
        txt_Distance = (TextView) findViewById(R.id.txt_distance);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txt_speed = (TextView) findViewById(R.id.txt_speed);
        btn_return = (Button) findViewById(R.id.btn_return);
        speed=(TextView)findViewById(R.id.txt_speed);

        txtidMap = (TextView) findViewById(R.id.txtIDMap);
        txtidSignSetting = (TextView) findViewById(R.id.txtidSignSetting);
        txtidDistSetting = (TextView) findViewById(R.id.txtidDistSetting);


        //sound alert
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.dog);
        //get distant from SearchSign 03/02/2017
        TextView textView = (TextView) findViewById(R.id.txtDistance);
        Intent intent = getIntent();
        String distance = intent.getStringExtra("distant");
        textView.setText(distance);
        textView.setTextSize(20);
        //get distant from SearchSign 23/02/2017
        TextView textView2 = (TextView) findViewById(R.id.txtIDMap);
        intent = getIntent();
        String idMap = intent.getStringExtra("idMap");
        textView2.setText(idMap);
        textView2.setTextSize(20);
        //get distant from SearchSign 26/02/2017
        TextView textView3 = (TextView) findViewById(R.id.txtidSignSetting);
        intent = getIntent();
        String idSign = intent.getStringExtra("idSign");
        textView3.setText(idSign);
        textView3.setTextSize(20);
        //get distant from SearchSign 26/02/2017
        TextView textView4 = (TextView) findViewById(R.id.txtidDistSetting);
        intent = getIntent();
        String idDistance = intent.getStringExtra("idDistance");
        textView4.setText(idDistance);
        textView4.setTextSize(20);
        Log.d("26FebV6", "distance:" + distance+"idMap:"+idMap+"idSign:"+idSign+"idDist:"+idDistance);

        //get lat lng location device
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //set marker when lat && lng changed
                txtView_gpsLat.setText(location.getLatitude()+"");
                txtView_gpsLng.setText(location.getLongitude()+"");
                Log.d("Location", "Lat:" + location.getLatitude() + "Lng:" + location.getLongitude());

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                //ปักหมุดพิกัดของเครื่อง
                //ถ้ามีการปักหมุดอยู่แล้ว จะลบหมุดอันเดิมออกจากแผนที่
                if (marker != null && circle != null) {
                    marker.remove();
                    circle.remove();
                }
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(lat,lng));
                marker = mGoogleMap.addMarker(options);
                //add circle in marker
                circle = drawCircle(new LatLng(lat, lng));

                CalculateDistance calculatedistance = new CalculateDistance(MapSearch.this);
                calculatedistance.execute();

                int idMap2;
                int idSign2, idDistance2,seekbarDist;
                double speed,dist;
                idMap2 = Integer.parseInt(txtidMap.getText().toString());
                idSign2 = Integer.parseInt(txtidSignSetting.getText().toString());
                idDistance2 = Integer.parseInt(txtidDistSetting.getText().toString());
                speed = Double.valueOf(txt_speed.getText().toString());
                dist = Double.valueOf(txt_Distance.getText().toString());
                seekbarDist = Integer.parseInt(txtDistance.getText().toString());

                //ค้นหาทุกป้าย แต่เลือกการแจ้งเตือนว่าจะแจ้งป้ายไหน
                if (dist >= seekbarDist) {
                    //-----------------All Sign-----------//
                    if (idMap2 == 1 && idSign2 == 1 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 45 ขึ้นไป คือทุกป้าย ระยะ 300 m.
                        if (speed >= 45.0 || speed >= 60.0 || speed >= 80 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 1 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 45 ขึ้นไป คือทุกป้าย ระยะ 400 m.
                        if (speed >= 45.0 || speed >= 60.0 || speed >= 80 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV2", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 1 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 45 ขึ้นไป คือทุกป้าย ระยะ 500 m.
                        if (speed >= 45.0 || speed >= 60.0 || speed >= 80 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนทุกป้าย เลือกระยะห่าง

                    else if (idMap2 == 1 && idSign2 == 2 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 60 ขึ้นไป คือทุกป้าย ระยะ 300 m.
                        if (speed >= 60.0 || speed >= 80 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 2 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 60 ขึ้นไป คือทุกป้าย ระยะ 400 m.
                        if (speed >= 60.0 || speed >= 80 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 2 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 60 ขึ้นไป คือทุกป้าย ระยะ 400 m.
                        if (speed >= 60.0 || speed >= 80 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 60 ขึ้นไป แต่เลือกระยะ

                    else if (idMap2 == 1 && idSign2 == 3 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 80 ขึ้นไป คือทุกป้าย ระยะ 300 m.
                        if (speed >= 80 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 3 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 80 ขึ้นไป คือทุกป้าย ระยะ 400 m.
                        if (speed >= 80 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    } else if (idMap2 == 1 && idSign2 == 3 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 80 ขึ้นไป คือทุกป้าย ระยะ 400 m.
                        if (speed >= 80 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 80 แต่เลือกระยะ
                    //----------------Sign45----------------------//
                    else if (idMap2 == 2 && idSign2 == 1 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 45  ระยะ 300 m.
                        if (speed >= 45.0 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 45 แต่เลือกระยะ 300 m.
                    else if (idMap2 == 2 && idSign2 == 1 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 45  ระยะ 300 m.
                        if (speed >= 45.0 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 45 แต่เลือกระยะ 400 m.
                    else if (idMap2 == 2 && idSign2 == 1 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 45  ระยะ 500 m.
                        if (speed >= 45.0 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 45 แต่เลือกระยะ 500 m.
                    //-----------------Sign60---------------------//
                    else if (idMap2 == 3 && idSign2 == 2 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 60  ระยะ 300 m.
                        if (speed >= 60.0 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 60 แต่เลือกระยะ 300 m.
                    else if (idMap2 == 3 && idSign2 == 2 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 60 ระยะ 400 m.
                        if (speed >= 60.0 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 60 แต่เลือกระยะ 400 m.
                    else if (idMap2 == 3 && idSign2 == 2 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 60  ระยะ 500 m.
                        if (speed >= 60.0 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 60 แต่เลือกระยะ 500 m.
                    //----------------Sign80----------------------//
                    else if (idMap2 == 4 && idSign2 == 3 && idDistance2 == 1) {
                        //แจ้งเตือนป้าย 80  ระยะ 300 m.
                        if (speed >= 80.0 && dist <= 0.3) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 80 แต่เลือกระยะ 300 m.
                    else if (idMap2 == 4 && idSign2 == 3 && idDistance2 == 2) {
                        //แจ้งเตือนป้าย 80 ระยะ 400 m.
                        if (speed >= 80.0 && dist <= 0.4) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 80 แต่เลือกระยะ 400 m.
                    else if (idMap2 == 4 && idSign2 == 3 && idDistance2 == 3) {
                        //แจ้งเตือนป้าย 80  ระยะ 500 m.
                        if (speed >= 80.0 && dist <= 0.5) {
                            mp.start();
                            Log.d("27FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist);
                        } else {
                            mp.stop();
                        }
                    }//แจ้งเตือนป้าย 80 แต่เลือกระยะ 500 m.
                } else {
                    txt_Distance.setText("0");
                } //dist seekbar




            }//on locationChanged

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        };

        //Get gps from device
        gps = new GPSTracker(MapSearch.this);

        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.d("16FebV1", "Marker" + "Lat:" + latitude + "Lng:" + longitude);

        }else{
            // txtLocation.setText("อุปกรณ์์ของคุณ ปิด GPS");
        }
        //call permission
        requestPermissions(perms, 1);
        //update location in 0 minute distance 1 meter
        //*calculate speed
        btn_getLatLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, locationListener);
            }
        });

        checkGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            return;
        }
        if(status==false)
            bindService();
        locate=new ProgressDialog(MapSearch.this);
        locate.setIndeterminate(true);
        locate.setCancelable(false);
        locate.setMessage("Getting Location...");
        locate.show();
        //locate.dismiss();//*/

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==true)
                    unbindService();
                p=0;

                checkGps();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                    return;
                }
                //  pause.setText("Pause");
                p = 0;
                Intent i = new Intent(MapSearch.this, SearchSign.class);
                startActivity(i);
            }
        });
    }//Main method


    private void configureButton() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, locationListener);
    }

    private boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1 :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //case เกี่ยวกับอัพเดท latitude and longitude
                    configureButton();

                } else {

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //  readLocation();
                    //case เกี่ยวกับ อัพเดท Location calculate speed
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("check Location")
                                .setMessage("you need to grant location");
                    } else {

                    }
                }
        }
    }//onRequestPermissionsResult

    Marker marker;
    Circle circle;
    //Search Map All
    private class GetMap extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";

        public GetMap(Context context) {
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

                    String strSignID = jsonObject.getString("SignID");
                    String strSignName = jsonObject.getString("SignName");
                    String strLat = jsonObject.getString("Latitude");
                    String strLng = jsonObject.getString("Longitude");

                   /* String[] latArr = new String[jsonArray.length()];
                    latArr[i] = strLat;
                    String[] lngArr = new String[jsonArray.length()];
                    lngArr[i] = strLng;
                    Log.d("20FebV5", "Marker" + "Lat:" + latArr[i] + "Lng:" + lngArr[i]);*/

                    gps = new GPSTracker(MapSearch.this);
                    gps.canGetLocation();
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    Log.d("01FebV1", "Marker" + "Lat:" + latitude + "Lng:" + longitude);

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
                    double meterInKm = Double.valueOf(newFormat2.format(meter));
                    int seekBar; //ค่ารัศมีที่รับมาจากค่า seekBar
                    seekBar = Integer.parseInt(txtDistance.getText().toString());

                    int idMap;
                    idMap = Integer.parseInt(txtidMap.getText().toString());

                    Log.d("04FebV2", "" + valueResult + "   KM  " + kmInDec
                            + " Meter   " + meterInKm +":"+seekBar);

                    Log.d("12FebV1", "Lat:" + lat1 + "" + "Lng:" + lng1);

                    //ถ้าค่ารัศมีเท่ากับค่ารัศมีที่คำนวณจากดาต้าเบสเท่ากันหรือน้อยกว่า ก็จะวนลูปปักหมุด && Checked idMap
                    if (meterInKm <= seekBar && idMap == 1) {

                        //Create Marker Sign
                        if (strSignName.equals("Sign45") || strSignName.equals("sign45")) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign45_ss));

                        } else if (strSignName.equals("Sign60") || strSignName.equals("sign60")) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign60_ss));

                        } else {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                    // .title(strSignName)
                                    // .snippet(String.valueOf(meterInKm)))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign80_ss));
                        }
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng (Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                    } else if (meterInKm <= seekBar &&  idMap == 2 && strSignName.equals("Sign45")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign45_ss));
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng (Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                    } else if (meterInKm <= seekBar && idMap == 3 && strSignName.equals("Sign60")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign60_ss));
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                    } else if (meterInKm <= seekBar &&  idMap == 4 && strSignName.equals("Sign80")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign80_ss));
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));
                    }//if check distance && checked idMap

                    Log.d("04FebV3", "" + meterInKm +":"+seekBar+":"+idMap+":"+strSignID+":"+strSignName);
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng),15);

                    //set marker from gps device 16/02/17
                    MarkerOptions options = new MarkerOptions()
                            .position(new LatLng(gps.getLatitude(), gps.getLongitude()));
                    marker = mGoogleMap.addMarker(options);
                    LatLng coordinate = new LatLng (gps.getLatitude(),gps.getLongitude());
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                    goToLocationZoom(gps.getLatitude(),gps.getLongitude(),15);
                    Log.d("16FebV2", "Marker" + "Lat:" + gps.getLatitude() + "Lng:" + gps.getLongitude());
                    //ปักหมุดในครั้งแรกที่เปิดหน้าแมพ แล้วลบหมุดออกถ้าแลตลองเปลี่ยน
                    marker.remove();
                }// for

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//SnyUser

    private class CalculateDistance extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";

        public CalculateDistance(Context context) {
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

                double max = 0;
                double min = 100;

                for (int i = 0; i < jsonArray.length(); i += 1) {
                    //Get Json from Database
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strSignID = jsonObject.getString("SignID");
                    String strSignName = jsonObject.getString("SignName");
                    String strLat = jsonObject.getString("Latitude");
                    String strLng = jsonObject.getString("Longitude");

                    gps = new GPSTracker(MapSearch.this);
                    gps.canGetLocation();
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    Log.d("01FebV1", "Marker" + "Lat:" + latitude + "Lng:" + longitude);

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
                    double meterInKm = Double.valueOf(newFormat2.format(meter));
                    int seekBar; //ค่ารัศมีที่รับมาจากค่า seekBar
                    seekBar = Integer.parseInt(txtDistance.getText().toString());

                    int idMap;
                    idMap = Integer.parseInt(txtidMap.getText().toString());
                    Log.d("20FebV3", "" + valueResult + "   KM  " + kmInDec
                            + " Meter   " + meterInKm + ":" + seekBar);
                    //เก็บค่า meterInKm เป็นอาเรย์
                    double[] exIntArray = new double[jsonArray.length()];
                    exIntArray[i] = meterInKm;
                    double distance[] = {exIntArray[i]};
                    //เปรียบเทียบค่าระยะห่างระหว่างป้ายกกับตัวผู้ใช้ แล้วแสดงให้ผู้ใช้เห็นว่าป้ายยที่ใกล้ที่ที่สุดห่างเท่าไหร่   idMap 1 คือหาทุกป้าย ,2 หาแค่ป้าย45 , 3 หาแค่ป้าย60 ,4 หาแค่ป้าย80
                    if (exIntArray[i] <= seekBar && idMap == 1) {
                        if (exIntArray[i] < min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min +"");
                        Log.d("23FebV2", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName);

                    } else if (exIntArray[i] <= seekBar && idMap == 2 && strSignName.equals("Sign45")) {
                        if (exIntArray[i]<min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min+"");
                        Log.d("23FebV3", "distance:" + distance[0]+"id: "+strSignID+"signName:"+strSignName);

                    } else if (exIntArray[i] <= seekBar && idMap == 3 && strSignName.equals("Sign60")) {
                        if (exIntArray[i] < min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min+"");
                        Log.d("23FebV4", "distance:" + distance[0] + "id: " + strSignID+"signName:"+strSignName);

                    } else if (exIntArray[i] <= seekBar && idMap == 4 && strSignName.equals("Sign80")) {
                        // if (exIntArray[i] > max)
                        //     max = exIntArray[i];
                        if (exIntArray[i] < min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min + "");
                        Log.d("23FebV5", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName);
                    } else {
                        txt_Distance.setText("0");
                    }
                }//for

                Log.d("22FebV1", "distance:" + min);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//SnyUser

    //ZoomMap
    private void goToLocationZoom(double v, double v1) {
        LatLng latlng = new LatLng(v, v1);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,15);
        mGoogleMap.moveCamera(update);
    }//goToLocationZoom

    //class zoom map
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mGoogleMap.moveCamera(update);
    }//goToLocationZoom

    //Show map
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }//initMap

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }//googleServicesAvailable

    private Circle drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(100)
                .fillColor(0x6633b5e5)
                .strokeColor(Color.BLUE)
                .strokeWidth(1);
        return mGoogleMap.addCircle(circleOptions);
    }//drawCircle
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

/*
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                //blind widget
                final EditText lat = (EditText) findViewById(R.id.edt_lat);
                final EditText lng = (EditText) findViewById(R.id.edt_lng);
               final EditText signName = (EditText) findViewById(R.id.edtSignName);
                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                // TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
                LatLng latLng = marker.getPosition();
                tvLocality.setText(marker.getTitle());
                tvLat.setText("Latitude: "+latLng.latitude);
                tvLng.setText("Longitude: "+latLng.longitude);
                tvSnippet.setText(marker.getSnippet());
                txt_Distance.setText(marker.getSnippet());
                //show lat long in edit text
                lat.setText(latLng.latitude+"");
                lng.setText(latLng.longitude+"");
                return v;
            }
        });*/
    }//onMapReady

    //Calculate Speed
    void checkGps()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            showGPSDisabledAlertToUser();
        }
    }//void checkGps
    void bindService()
    {
        if(status==true)
            return;
        Intent i=new Intent(getApplicationContext(),LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status=true;
        startTime=System.currentTimeMillis();
    }//bindService
    void unbindService()
    {
        if(status==false)
            return;
        Intent i=new Intent(getApplicationContext(),LocationService.class);
        unbindService(sc);
        status=false;
    }//unbindService
    @Override
    protected void onResume() {
        super.onResume();

    }//onResume
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(status==true)
            unbindService();
    }//onDestroy
    @Override
    public void onBackPressed() {
        if(status==false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }//onBackPressed
    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder=(LocationService.LocalBinder)service;
            myService=binder.getService();
            status=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status=false;
        }
    };//ServiceConnection
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }//showGPSDisabledAlertToUser

}//Main Class