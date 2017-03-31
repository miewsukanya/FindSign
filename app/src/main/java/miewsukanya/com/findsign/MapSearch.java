package miewsukanya.com.findsign;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import java.text.DecimalFormat;

import miewsukanya.com.findsign.arview.ARView;

import static miewsukanya.com.findsign.R.array.idDistance;
import static miewsukanya.com.findsign.R.array.idSign;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    //about calculate speed
    static ProgressDialog locate;
    static int p=0;
    static long endTime;
    static long startTime;
    static TextView speed;
    LocationService myService;
    static boolean status;
    private static final int MY_PERMISSION_REQUEST = 5;
    //New speed 03/03/17
    final int update_interval = 3000; // milliseconds

    // Data shown to user
    float speed2 = 0.0f;
    float speed_max = 0.0f;

    int num_updates = 0; // GPS update counter
    int no_loc = 0; // Number of null GPS updates
    int no_speed = 0; // Number of GPS updates which don't have speed

    LocationManager loc_mgr;
    //Explicit
    private String[] perms = {"android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.INTERNET"};
    GPSTracker gps;
    private LocationManager locationManager;
    private LocationListener locationListener;
    GoogleMap mGoogleMap;
    EditText edt_distance;
    Button btn_getLatLng,btn_return,btn_AR;
    TextView txtView_gpsLat,txtView_gpsLng,txt_Distance,txtDistance,txt_speed,txtidMap,txtSignName;
    TextView txtidSignSetting, txtidDistSetting;
    private static final int REQUEST_CAMERA = 0;
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
        // btn_return = (Button) findViewById(R.id.btn_return);
        // speed=(TextView)findViewById(R.id.txt_speed);

        txtidMap = (TextView) findViewById(R.id.txtIDMap);
        txtidSignSetting = (TextView) findViewById(R.id.txtidSignSetting);
        txtidDistSetting = (TextView) findViewById(R.id.txtidDistSetting);
        txtSignName = (TextView) findViewById(R.id.txt_SignNameMS);
        btn_AR = (Button) findViewById(R.id.btnAR);

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
        Log.d("26FebV6", "distance:" + distance + "idMap:" + idMap + "idSign:" + idSign + "idDist:" + idDistance);

        update_speed(0.0f); //update speed


        //get lat lng location device
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //set marker when lat && lng changed
                txtView_gpsLat.setText(location.getLatitude() + "");
                txtView_gpsLng.setText(location.getLongitude() + "");
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
                        .position(new LatLng(lat, lng));
                marker = mGoogleMap.addMarker(options);
                // add circle in marker
                circle = drawCircle(new LatLng(lat, lng));
                LatLng coordinate = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
                goToLocationZoom(lat, lng, 16);

                CalculateDistance calculatedistance = new CalculateDistance(MapSearch.this);
                calculatedistance.execute();
                //update_speed( 0.0f ); //update speed

                int idMap2;
                int idSign2, idDistance2, seekbarDist;
                double speed, dist;
                String SignName;
                idMap2 = Integer.parseInt(txtidMap.getText().toString());
                idSign2 = Integer.parseInt(txtidSignSetting.getText().toString());
                idDistance2 = Integer.parseInt(txtidDistSetting.getText().toString());
                speed = Double.valueOf(txt_speed.getText().toString());
                dist = Double.valueOf(txt_Distance.getText().toString());
                seekbarDist = Integer.parseInt(txtDistance.getText().toString());
                SignName = txtSignName.getText().toString();
                Log.d("19MarV1", "speed:"+dist+":"+seekbarDist);
                //ค้นหาทุกป้าย แต่เลือกการแจ้งเตือนว่าจะแจ้งป้ายไหน
                if (dist <= seekbarDist) {

                    //-----------------All Sign-----------//
                    if (idMap2 == 1) {
                        //แจ้งเตือนป้าย 45 ขึ้นไป
                        if (idSign2 == 1 && idDistance2 == 1) {
                            if (dist <= 0.3) {
                                if (SignName.equals("Sign45")) {
                                   // Log.d("19MarV1", "speed:"+speed);
                                    if (speed >= 45.0) {

                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        //  mp.start() ;
                                        //  Toast.makeText(getApplicationContext(), "Alert!", Toast.LENGTH_LONG).show();
                                        //  Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist+SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                        //  Log.d("28FebV1.1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist+SignName);
                                    }
                                } else if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        // mp.start();
                                        Log.d("28FebV2", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                } else if (SignName.equals("Sign80")) {
                                    if (speed >= 80.0) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        //mp.start();
                                        Log.d("28FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                                try {
                                //    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                 //   MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                //    mp.stop();
                                } catch (Exception e) {
                                }
                            }//if check distance
                        }//idSign == 1 idDistance==1

                        else if (idSign2 == 1 && idDistance2 == 2) {
                            if (dist > 0.1 && dist <= 0.4) {
                                if (SignName.equals("Sign45")) {
                                    if (speed >= 45.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        //mp.start() ;
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                    }
                                } else if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        // mp.start();
                                        Log.d("28FebV2", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                    }
                                } else if (SignName.equals("Sign80")) {
                                    if (speed >= 80.0) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        // mp.start();
                                        Log.d("28FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                              //  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                              //  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                              //  mp.stop();
                            }//if check distance

                        }//idSign == 1 idDistance==2
                        else if (idSign2 == 1 && idDistance2 == 3) {
                            if (dist > 0.1 && dist <= 0.5) {
                                if (SignName.equals("Sign45")) {
                                    if (speed >= 45.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                    }
                                } else if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        // mp.start();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV2", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                    }
                                } else if (SignName.equals("Sign80")) {
                                    if (speed >= 80.0) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        // mp.start();
                                        Log.d("28FebV3", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        //mp.stop();
                                    }
                                }
                            } else {
                             //   Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                             //   MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                             //   mp.start();
                                // mp.stop();
                            }//if check distance

                        }//idSign == 1 idDistance==3
                    }//idMap2 == 1 หน้าค้นหาทุกป้าย

                    else if (idMap2 == 2) {
                        //แจ้งเตือนป้าย 45
                        if (idSign2 == 1 && idDistance2 == 1) {
                            if (dist <= 0.3) {
                                if (SignName.equals("Sign45")) {
                                    if (speed >= 45.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        // mp.stop();
                                    }
                                }
                            } else {
                            //    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                             //   MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                             //   mp.stop();
                                // mp.stop();
                            }//if check distance
                        }//Distance = 300 m.
                        else if (idSign2 == 1 && idDistance2 == 2) {
                            if (dist > 0.1 && dist <= 0.4) {
                                if (SignName.equals("Sign45")) {
                                    if (speed >= 45.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        //  mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        //mp.stop();
                                    }
                                }
                            } else {
                               // Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                               // MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                               // mp.stop();
                                //  mp.stop();
                            }//if check distance
                        }//Distance = 400 m.
                        else if (idSign2 == 1 && idDistance2 == 3) {
                            if (dist > 0.1 && dist <= 0.5) {
                                if (SignName.equals("Sign45")) {
                                    if (speed >= 45.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                        //mp.stop();
                                    }
                                }
                            } else {
                               // Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                               // MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                               // mp.stop();
                                //mp.stop();
                            }//if check distance
                        }//Distance = 500 m.
                    }//idMap2 == 2 ค้นหาเฉพาะป้าย 45

                    else if (idMap2 == 3) {
                        //แจ้งเตือนป้าย 60
                        if (idSign2 == 2 && idDistance2 == 1) {
                            if (dist <= 0.3) {
                                if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        //  mp.stop();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                                //mp.stop();
                               /// Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                               // MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                               // mp.stop();
                            }//if check distance
                        }//Distance = 300 m.
                        else if (idSign2 == 2 && idDistance2 == 2) {
                            if (dist > 0.1 && dist <= 0.4) {
                                if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        //mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        // mp.stop();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                                //mp.stop();
                              //  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                              //  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                              //  mp.stop();
                            }//if check distance
                        }//Distance = 400 m.
                        else if (idSign2 == 2 && idDistance2 == 3) {
                            if (dist > 0.1 && dist <= 0.5) {
                                if (SignName.equals("Sign60")) {
                                    if (speed >= 60.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        // mp.stop();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                                // mp.stop();
                               // Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                               // MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                               // mp.stop();
                            }//if check distance
                        }//Distance = 500 m.
                    }//idMap2 == 3 ค้นหาเฉพาะป้าย 60

                    else if (idMap2 == 4) {
                        //แจ้งเตือนป้าย 60
                        if (idSign2 == 3 && idDistance2 == 1) {
                            if (dist <= 0.3) {
                                if (SignName.equals("Sign80")) {
                                    if (speed >= 80.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        //mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        //mp.stop();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                }
                            } else {
                              //  mp.stop();
                            }//if check distance
                        }//Distance = 300 m.
                        else if (idSign2 == 3 && idDistance2 == 2) {
                            if (dist > 0.1 && dist <= 0.4) {
                                if (SignName.equals("Sign80")) {
                                    if (speed >= 80.0) {
                                        // distance <= 300 m. && speed >= 45.0 km/hr.
                                        // mp.start() ;
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                    } else {
                                        //mp.stop();
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.stop();
                                    }
                                } else {
                                    //mp.stop();
                                  //  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                  ///  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                  //  mp.stop();
                                }//if check distance
                            }//Distance = 400 m.
                            else if (idSign2 == 3 && idDistance2 == 3) {
                                if (dist > 0.1 && dist <= 0.5) {
                                    if (SignName.equals("Sign80")) {
                                        if (speed >= 80.0) {
                                            // distance <= 300 m. && speed >= 45.0 km/hr.
                                            // mp.start() ;
                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                            mp.start();
                                            Log.d("28FebV1", "idSign:" + idSign2 + "idDist:" + idDistance2 + "speed:" + speed + "dist:" + dist + SignName);
                                        } else {
                                            //  mp.stop();
                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                            mp.stop();
                                        }
                                    }
                                } else {
                                    //mp.stop();
                                  //  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                  //  MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                  //  mp.stop();
                                }//if check distance
                            }//Distance = 500 m.
                        }//idMap2 == 4 ค้นหาเฉพาะป้าย 80
                    } else {
                        txt_Distance.setText("-");
                    } //dist seekbar
                }//on locationChanged
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
        //btn_AR.setOnClickListener(new View.OnClickListener(){
         //   @Override
          //  public void onClick (View v){
          //      Intent intent = new Intent(getApplicationContext(), ARView.class);
          //      startActivity(intent);
          //  }
       // });//btnAR
        try {
            configure_button() ; //call permission
            getlatlngOnclick();
            // locationManager.requestLocationUpdates("gps", 3000, 0, locationListener); //get lat lng in 3 ms.
            loc_mgr = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            loc_mgr.requestLocationUpdates( LocationManager.GPS_PROVIDER, update_interval, 0.0f, this );

        } catch (Exception e) {
        }
        GetMap getMap = new GetMap(MapSearch.this);
        getMap.execute();
        initMap();
        CalculateDistance calculatedistance = new CalculateDistance(MapSearch.this);
        calculatedistance.execute();
        setRequestCamera(); //ขออนุญาตใช้งานกล้อง permission

    }//Main method

    public void getlatlngOnclick(){
        try {
            locationManager.requestLocationUpdates("gps", 3000, 0, locationListener); //อัพเดท location in 3 ms.
        } catch (Exception e) {
        }
      //  loc_mgr.requestLocationUpdates( LocationManager.GPS_PROVIDER, update_interval, 0.0f, this );
    }//getlatlngOnclick()
    //New Speed
    void update_speed( float x ) {
        speed2= x;
        if ( x > speed_max )
            speed_max = x;

        String s = String.format("%.0f", speed2 * 3.6f);
        txt_speed.setText( s ); //set speed in textView
    }//update_speed
    public void onLocationChanged( Location loc ) {
        num_updates++;
        if ( loc == null ) {
            no_loc++;
            return;
        }
        if ( !loc.hasSpeed() ) {
            no_speed++;
            return;
        }
        update_speed( loc.getSpeed() );
    }//onLocationChanged speed

    public void onStatusChanged( String arg0, int arg1, Bundle arg2 ) {}
    public void onProviderEnabled( String arg0 ) {}
    public void onProviderDisabled( String arg0 ) {}
    //NewSpeed

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            case 11:
                setRequestCamera();
                break;
            default:
                break;
        }
    }//onRequestPermissionsResult

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        btn_getLatLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 3000, 0, locationListener);
            }
        });
    }//configure_button() allow location
    public void onClickAr(View view) {
        try {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();

        } else {
            //จะส่งค่าไปหน้า ARView
            //intent data from MapSearch to ARView  31/03/2017
            String distance = txtDistance.getText().toString();
            String idMap = txtidMap.getText().toString();

            Intent intent = new Intent(getApplicationContext(), ARView.class);
            intent.putExtra("distance",distance);
            intent.putExtra("idMap", idMap);

            Log.d("31MarV3", "idMap: " + idMap + "Distance:" + distance);
            startActivity(intent);
            //startActivity(new Intent(MapSearch.this,ARView.class));
        }
        } catch (Exception e) {
        }
    }//onClickAr button AR

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(MapSearch.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(MapSearch.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }//requestPermissions()

    void setRequestCamera(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.INTERNET}
                        ,11);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
    }//setRequestCamera() allow Camera

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
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                    } else if (meterInKm <= seekBar && idMap == 3 && strSignName.equals("Sign60")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign60_ss));
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                    } else if (meterInKm <= seekBar &&  idMap == 4 && strSignName.equals("Sign80")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng))))
                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign80_ss));
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));
                    }//if check distance && checked idMap

                    Log.d("04FebV3", "" + meterInKm +":"+seekBar+":"+idMap+":"+strSignID+":"+strSignName);
                 //   mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                  //  goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng),16);

                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng coordinate = new LatLng (Double.parseDouble(strLat), Double.parseDouble(strLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
                    goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));
                    //ปักหมุดในครั้งแรกที่เปิดหน้าแมพ แล้วลบหมุดออกถ้าแลตลองเปลี่ยน
                    // marker.remove();
                }// for
                //ถ้ามีการปักหมุดอยู่แล้ว จะลบหมุดอันเดิมออกจากแผนที่
                if (marker != null && circle != null) {
                    marker.remove();
                    circle.remove();
                }
                //set marker from gps device 16/02/17
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(gps.getLatitude(), gps.getLongitude()));
                marker = mGoogleMap.addMarker(options);
                circle = drawCircle(new LatLng(gps.getLatitude(), gps.getLongitude()));
                LatLng coordinate = new LatLng (gps.getLatitude(),gps.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12));
                goToLocationZoom(gps.getLatitude(),gps.getLongitude(),12);
                Log.d("16FebV2", "Marker" + "Lat:" + gps.getLatitude() + "Lng:" + gps.getLongitude());
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
                        if (exIntArray[i] < min && strSignName.equals("Sign45")) {
                            min = exIntArray[i];
                            //แสดงค่าระยะห่างใน textView
                            txt_Distance.setText(min +"");
                            txtSignName.setText(strSignName+"");

                            Log.d("01MarV1", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);
                        } else if (exIntArray[i] < min && strSignName.equals("Sign60")) {
                            min = exIntArray[i];
                            //แสดงค่าระยะห่างใน textView
                            txt_Distance.setText(min +"");
                            txtSignName.setText(strSignName+"");

                            Log.d("01MarV2", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);
                        } else if (exIntArray[i] < min && strSignName.equals("Sign80")) {
                            min = exIntArray[i];
                            //แสดงค่าระยะห่างใน textView
                            txt_Distance.setText(min +"");
                            txtSignName.setText(strSignName+"");

                            Log.d("01MarV3", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);
                        }

                    } else if (exIntArray[i] <= seekBar && idMap == 2 && strSignName.equals("Sign45")) {
                        if (exIntArray[i]<min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min+"");
                        txtSignName.setText(strSignName+"");
                        Log.d("01MarV2", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);

                    } else if (exIntArray[i] <= seekBar && idMap == 3 && strSignName.equals("Sign60")) {
                        if (exIntArray[i] < min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min+"");
                        txtSignName.setText(strSignName+"");
                        Log.d("01MarV3", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);

                    } else if (exIntArray[i] <= seekBar && idMap == 4 && strSignName.equals("Sign80")) {
                        // if (exIntArray[i] > max)
                        //     max = exIntArray[i];
                        if (exIntArray[i] < min)
                            min = exIntArray[i];
                        //แสดงค่าระยะห่างใน textView
                        txt_Distance.setText(min + "");
                        txtSignName.setText(strSignName+"");
                        Log.d("01MarV4", "distance:" + distance[0] + "id: " + strSignID + "signName:" + strSignName+":"+idMap);
                    } else {
                        //  txt_Distance.setText("0");
                    }
                }//for

                Log.d("22FebV1", "distance:" + min);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }//onPost
    }//CalculateDistance

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
                .radius(50)
                .fillColor(0x6633b5e5)
                .strokeColor(Color.BLUE)
                .strokeWidth(1);
        return mGoogleMap.addCircle(circleOptions);
    }//drawCircle
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }//onMapReady
}//Main Class