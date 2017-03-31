package miewsukanya.com.findsign.arview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import miewsukanya.com.findsign.GPSTracker;
import miewsukanya.com.findsign.utils.Compatibility;
import miewsukanya.com.findsign.utils.PaintUtils;

public class ARView extends Activity implements SensorEventListener,LocationListener {

    private static Context _context;
    WakeLock mWakeLock;
    CameraView cameraView;
    RadarMarkerView radarMarkerView;
    public RelativeLayout upperLayerLayout;
    static PaintUtils paintScreen;
    static DataView dataView;
    static DataView2 dataView2;
    static DataView3 dataView3;
    static DataView4 dataView4;
    boolean isInited = false;
    boolean isInited2 = false;
    boolean isInited3 = false;
    boolean isInited4 = false;
    public static float azimuth;
    public static float pitch;
    public static float roll;
    public double latitudeprevious;
    public double longitude;
    String locationContext;
    String provider;
    DisplayMetrics displayMetrics;
    Camera camera;
    public int screenWidth;
    public int screenHeight;

    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];
    private float results[] = new float[3];
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav, sensorMag;

    static final float ALPHA = 0.25f;
    protected float[] gravSensorVals;
    protected float[] magSensorVals;
    GPSTracker gps;

    //New speed 01/04/17
    private LocationManager locationManager;
    private LocationListener locationListener;
    final int update_interval = 3000; // milliseconds
    // Data shown to user
    float speed2 = 0.0f;
    float speed_max = 0.0f;
    int num_updates = 0; // GPS update counter
    int no_loc = 0; // Number of null GPS updates
    int no_speed = 0; // Number of GPS updates which don't have speed
    LocationManager loc_mgr;
    String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ดึงข้อมูลจากหน้าตั้งค่า
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int idSign = Integer.valueOf(sharedPreferences.getString("idSign", "1"));
        int idDistance = Integer.valueOf(sharedPreferences.getString("idDistance", "1"));
        Log.d("28MarV1", "idSign:" + idSign + "idDistance:" + idDistance);

        //ค่าที่รับมาจากหน้า MainActivity เพื่อที่จะใช้ในการค้นหาทุกป้าย
        Intent intent = getIntent();
        String idMap = intent.getStringExtra("idMap"); //ใช้ในการกำหนดการค้นหาทุกป้าย
        Log.d("29MarV2", "Select idSign :" + idSign + "idDistance:" + idDistance + "idMap:" + idMap);

        //ค่าที่รับมาจาก MapSearch  31/03/2017
        Intent intent2 = getIntent();
        idMap = intent2.getStringExtra("idMap");
        String distance = intent2.getStringExtra("distance");
        Log.d("31MarV3", "idMap: " + idMap + "Distance: " + distance);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        upperLayerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams upperLayerLayoutParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.FILL_PARENT, android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
        upperLayerLayout.setLayoutParams(upperLayerLayoutParams);
        upperLayerLayout.setBackgroundColor(Color.TRANSPARENT);

        _context = this;
        cameraView = new CameraView(this);
        radarMarkerView = new RadarMarkerView(this, displayMetrics, upperLayerLayout);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout headerFrameLayout = new FrameLayout(this);
        RelativeLayout headerRelativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relaLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

          headerRelativeLayout.setBackgroundColor(Color.WHITE);
          headerRelativeLayout.setLayoutParams(relaLayoutParams);
        /*//  Button button = new Button(this);
         RelativeLayout.LayoutParams buttonparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
         buttonparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        button.setLayoutParams(buttonparams);
        button.setText("Cancel");
        button.setPadding(15, 0, 15, 0);//*/

        TextView titleTextView = new TextView(this);
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        textparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        titleTextView.setLayoutParams(textparams);
        titleTextView.setText("Speed: "+String.format("%.0f", speed2 * 3.6f)); //set speed in textView
       // titleTextView.setTextColor();

       // headerRelativeLayout.addView(button);
        headerRelativeLayout.addView(titleTextView);
        headerFrameLayout.addView(headerRelativeLayout);

        setContentView(cameraView);
        addContentView(radarMarkerView, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(headerFrameLayout, new FrameLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 500,
                Gravity.BOTTOM));
        addContentView(upperLayerLayout, upperLayerLayoutParams);

        //ค้นหาทุกป้าย SearchQuick ค้นหาด่วน && MapSearch
        if (!isInited && idMap.equals("1")) {
            if (idSign == 1 && idDistance == 1) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 2) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 3) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 1) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 2) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 3) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 1) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 2) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 3) {
                //ค้นหาทุกป้าย
                dataView = new DataView(ARView.this);
                paintScreen = new PaintUtils();
                isInited = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            }
        }//ค้นหาทุกป้าย SearchQuick ค้นหาด่วน && MapSearch

        //ค้นหาป้าย45
        else if (!isInited && idMap.equals("2")) {
            if (idSign == 1 && idDistance == 1) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 2) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 3) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 1) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 2) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 3) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 1) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 2) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 3) {
                //ค้นหาทุกป้าย45
                dataView2 = new DataView2(ARView.this);
                paintScreen = new PaintUtils();
                isInited2 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            }
        }//ค้นหาป้าย45

        //ค้นหาป้าย60
        else if (!isInited && idMap.equals("3")) {
            if (idSign == 1 && idDistance == 1) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 2) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 3) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 1) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 2) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 3) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 1) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 2) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 3) {
                //ค้นหาทุกป้าย60
                dataView3 = new DataView3(ARView.this);
                paintScreen = new PaintUtils();
                isInited3 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            }
        }//ค้นหาป้าย60

        //ค้นหาป้าย80
        else if (!isInited && idMap.equals("4")) {
            if (idSign == 1 && idDistance == 1) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 2) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 1 && idDistance == 3) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 1) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 2) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 2 && idDistance == 3) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 1) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 2) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            } else if (idSign == 3 && idDistance == 3) {
                //ค้นหาทุกป้าย80
                dataView4 = new DataView4(ARView.this);
                paintScreen = new PaintUtils();
                isInited4 = true;
                Log.d("31MarV1", "idDistance:" + idDistance);
            }
        }//ค้นหาป้าย80

        update_speed(0.0f); //update speed
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Log.d("01ApV1", "Lat:" + lat + "Lng:" + lng);

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        try {
            // locationManager.requestLocationUpdates("gps", 3000, 0, locationListener); //get lat lng in 3 ms.
            loc_mgr = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            loc_mgr.requestLocationUpdates( LocationManager.GPS_PROVIDER, update_interval, 0.0f, this );

        } catch (Exception e) {

        }



        upperLayerLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(_context, "RELATIVE LAYOUT CLICKED", Toast.LENGTH_SHORT).show();
            }
        });

        cameraView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                for (int i = 0; i < dataView.coordinateArray.length; i++) {
                    if ((int) event.getX() < dataView.coordinateArray[i][0] && ((int) event.getX() + 100) > dataView.coordinateArray[i][0]) {
                        if ((int) event.getY() <= dataView.coordinateArray[i][1] && ((int) event.getY() + 100) > dataView.coordinateArray[i][1]) {
                            Toast.makeText(_context, "match Found its " + dataView.places[i], Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }//for
                return true;
            }//onTouch
        });

    }//Main method

    public static Context getContext() {
        return _context;
    }

    public int convertToPix(int val) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, _context.getResources().getDisplayMetrics());
        return (int) px;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mWakeLock.release();

        sensorMgr.unregisterListener(this, sensorGrav);
        sensorMgr.unregisterListener(this, sensorMag);
        sensorMgr = null;
    }

    @Override
    protected void onResume() {

        super.onResume();
        this.mWakeLock.acquire();
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensorGrav = sensors.get(0);
        }
        sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) {
            sensorMag = sensors.get(0);
        }
        sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(evt.values.clone(), gravSensorVals);
            grav[0] = evt.values[0];
            grav[1] = evt.values[1];
            grav[2] = evt.values[2];

        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magSensorVals = lowPass(evt.values.clone(), magSensorVals);
            mag[0] = evt.values[0];
            mag[1] = evt.values[1];
            mag[2] = evt.values[2];

        }
        if (gravSensorVals != null && magSensorVals != null) {
            SensorManager.getRotationMatrix(RTmp, I, gravSensorVals, magSensorVals);

            int rotation = Compatibility.getRotation(this);

            if (rotation == 1) {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
            } else {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
            }

            SensorManager.getOrientation(Rot, results);
            //set ตำแหน่งไอคอนบนหน้าจอ
            ARView.azimuth = (float) (((results[0] * 180) / Math.PI) + 180);
            ARView.pitch = (float) (((results[1] * 180 / Math.PI)) + 95);
            ARView.roll = (float) (((results[2] * 180 / Math.PI)));

            radarMarkerView.postInvalidate();
        }
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    //New Speed
    void update_speed( float x ) {
        speed2= x;
        if ( x > speed_max )
            speed_max = x;

         //s = String.format("%.0f", speed2 * 3.6f);
        //txt_speed.setText( s ); //set speed in textView
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
}//Main Class

class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    ARView arView;
    SurfaceHolder holder;
    Camera camera;

    public CameraView(Context context) {
        super(context);
        arView = (ARView) context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            camera.setDisplayOrientation(90);
            try {
                List<Camera.Size> supportedSizes = null;
                supportedSizes = Compatibility.getSupportedPreviewSizes(parameters);

                Iterator<Camera.Size> itr = supportedSizes.iterator();
                while(itr.hasNext()) {
                    Camera.Size element = itr.next();
                    element.width -= w;
                    element.height -= h;
                }
                Collections.sort(supportedSizes, new ResolutionsOrder());
                parameters.setPreviewSize(w + supportedSizes.get(supportedSizes.size()-1).width, h + supportedSizes.get(supportedSizes.size()-1).height);
            } catch (Exception ex) {
                parameters.setPreviewSize(arView.screenWidth , arView.screenHeight);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }

            camera = Camera.open();
            arView.camera = camera;
            camera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            try {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ignore) {
                    }
                    try {
                        camera.release();
                    } catch (Exception ignore) {
                    }
                    camera = null;
                }
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class RadarMarkerView extends View {

    ARView arView;
    DisplayMetrics displayMetrics;
    RelativeLayout upperLayoutView = null;
    public RadarMarkerView(Context context, DisplayMetrics displayMetrics, RelativeLayout rel) {
        super(context);

        arView = (ARView) context;
        this.displayMetrics = displayMetrics;
        upperLayoutView = rel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            //26/03/17
        super.onDraw(canvas);
        ARView.paintScreen.setWidth(canvas.getWidth());
        ARView.paintScreen.setHeight(canvas.getHeight());
        ARView.paintScreen.setCanvas(canvas);

            //show all arSign
            if (!ARView.dataView.isInited()) {
                ARView.dataView.init(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(), arView.camera, displayMetrics, upperLayoutView);
            } else {
                ARView.dataView.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll);
            }

            //show arSign45
            if (!ARView.dataView2.isInited2()) {
                ARView.dataView2.init2(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(), arView.camera, displayMetrics, upperLayoutView);
            } else {
                ARView.dataView2.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll);
            }

            //show arSign60
            if (!ARView.dataView3.isInited3()) {
                ARView.dataView3.init3(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(), arView.camera, displayMetrics, upperLayoutView);
            } else {
                ARView.dataView3.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll);
            }

            //show arSign80
            if (!ARView.dataView4.isInited4()) {
                ARView.dataView4.init4(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(), arView.camera, displayMetrics, upperLayoutView);
            } else {
                ARView.dataView4.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll);
            }

        } catch (Exception e) {
     }
    }
}

class ResolutionsOrder implements java.util.Comparator<Camera.Size> {
    public int compare(Camera.Size left, Camera.Size right) {

        return Float.compare(left.width + left.height, right.width + right.height);
    }
}
