package miewsukanya.com.findsign;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import miewsukanya.com.findsign.arview.ARView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Explicit


    private ImageView searchQuickImageView,searchSignImageView, knowLedgeImageView,btn_setting;
    TextView txtidSignPref,txtidDistancePref;
    private static final int REQ_LOAD_PREF = 103; //ตั้งรหัสสำหรับส่งค่ากลับ
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind widget
        searchQuickImageView = (ImageView) findViewById(R.id.SearchSignQuick);
        searchSignImageView = (ImageView) findViewById(R.id.SearchSign);
        knowLedgeImageView = (ImageView) findViewById(R.id.Knowledge);
        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        txtidSignPref = (TextView) findViewById(R.id.txtidSignPref);
        txtidDistancePref = (TextView) findViewById(R.id.txtidDistancePref);

        loadPref(); //โหลดค่าข้อมูลจากการตั้งค่ามาแสดง
        //setting controller
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent,REQ_LOAD_PREF); //ส่งข้อมูลไปยังหย้าตั้งค่า

            }
        });
        //search sign controller
        searchSignImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send distant to MapSearch 23/02/2017
                String idSign = txtidSignPref.getText().toString();
                String idDistance = txtidDistancePref.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchSign.class);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("25FebV1","Select idSign :"+ idSign);
                Log.d("25FebV2","Select idDistance :"+ idDistance);
                startActivity(intent);
                // finish();
                // startActivity(new Intent(MainActivity.this,SelectTypeSearch.class));
            }
        });

        //knowledge controller
        knowLedgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Knowledge.class));
            }
        });

        configure_button();
    }//Main Method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_LOAD_PREF) {
            loadPref(); //โหลดค่าในการตั้งค่าปัจจุบันแล้วแสดงผล
        }
    }//onActivityResult

    public void loadPref() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int idSign = Integer.valueOf(sharedPreferences.getString("idSign", "1"));
        int idDistance = Integer.valueOf(sharedPreferences.getString("idDistance", "1"));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(idSign);

        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(idDistance);

        txtidSignPref.setText(stringBuilder); //นำค่าที่ได้จากการตั้งค่ามาแสดงใน TextView
        txtidDistancePref.setText(stringBuilder1); //นำค่าที่ได้จากการตั้งค่ามาแสดงใน TextView
    }//loadPref

    public void onSearchQ(View view) {
        try {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        } else {

            String idMap = "1";
            Intent intent = new Intent(getApplicationContext(), ARView.class);
            intent.putExtra("idMap", idMap);
            Log.d("29MarV1","idMap:" +idMap);
            startActivity(intent);
          //  startActivity(new Intent(MainActivity.this,ARView.class));
//            Log.d("29Mar1","Select idSign :"+ idSign + "idDistance:" + idDistance);
        }
        } catch (Exception e) {

        }
    }//onSearchQ

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

        }
    }//requestPermissions()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String idMap = "1";
                Intent intent = new Intent(getApplicationContext(), ARView.class);
                intent.putExtra("idMap", idMap);
                Log.d("29MarV1","idMap:" +idMap);
                configure_button(); //ขออนุญาตใช้งาน location
                startActivity(intent);
                //startActivity(new Intent(MainActivity.this, ARView.class)); //หลังจากอนุญาตใช้งานกล้องแล้วจะเช้าหน้า  ARView
            } else {
                Log.d("06MarchV1", "CAMERA was not open");
            }
        }
        } catch (Exception e) {
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
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}//Main Class