package miewsukanya.com.findsign;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    //Explicit
    GPSTracker gps;
    private LocationManager locationManager;
    private LocationListener listener;
    GoogleMap mGoogleMap;
    EditText edt_distance;
    TextView txtView_gpsLat,txtView_gpsLng,txtView_mapLat, txtView_mapLng,txt_Distance,txt_space,txtDistance;
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

        } else {
            //No google map layout
        }

        //get distant from SearchSign 03/02/2017
        //TextView textView = (TextView) findViewById(R.id.txt_Distant);
        //EditText editText = (EditText) findViewById(R.id.edt_distance);
        TextView textView = (TextView) findViewById(R.id.txtDistance);
        Intent intent = getIntent();
        String distance = intent.getStringExtra("distant");
        textView.setText(distance);
        textView.setTextSize(20);
        Log.d("03FebV2", "distance from SearchSign :" + distance);

        txtView_gpsLat = (TextView) findViewById(R.id.txtView_gpsLat);
        txtView_gpsLng = (TextView) findViewById(R.id.txtView_gpsLng);
       // txtView_mapLat = (TextView) findViewById(R.id.txtView_mapLat);
      //  txtView_mapLng = (TextView) findViewById(R.id.txtView_mapLng);
        txt_Distance = (TextView) findViewById(R.id.txt_distance);
      //  edt_distance = (EditText) findViewById(R.id.edt_distance);
        txtDistance = (TextView) findViewById(R.id.txtDistance);

        //get lat lng location device
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
        };{
            gps = new GPSTracker(MapSearch.this);

            if(gps.canGetLocation()){

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                txtView_gpsLat.setText(latitude+"");
                txtView_gpsLng.setText(longitude+"");

                Log.d("01FebV1", "Marker" + "Lat:" + latitude + "Lng:" + longitude);

            }else{
                // txtLocation.setText("อุปกรณ์์ของคุณ ปิด GPS");
            }
            configure_button();
        }//listener


    }//Main Method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
    }//onRequestPermissionsResult

    Marker marker;
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

                    int Radius = 6371; // radius of earth in Km
                    double lat2, lng2;
                    double lat1 = gps.getLatitude(); //start Lat พิกัดจาก gps มือถือ
                    double lng1 = gps.getLongitude(); //start Lng พิกัดจาก gps มือถือ

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
                    double valueResult = Radius * c*1000;
                    double km = valueResult / 1;
                    DecimalFormat newFormat = new DecimalFormat("####");
                    int kmInDec = Integer.valueOf(newFormat.format(km));
                    double meter = valueResult % 1000;
                    int meterInDec = Integer.valueOf(newFormat.format(meter));

                    int seekBar; //ค่ารัศมีที่รับมาจากค่า seekBar
                    seekBar = Integer.parseInt(txtDistance.getText().toString());

                    Log.d("04FebV2", "" + valueResult + "   KM  " + kmInDec
                            + " Meter   " + meterInDec +":"+seekBar);
                    //ถ้าค่ารัศมีเท่ากับค่ารัศมีที่คำนวณจากดาต้าเบสเท่ากันหรือน้อยกว่า ก็จะวนลูปปักหมุด
                    if (meterInDec <= seekBar) {
                         //Create Marker Sign
                        if (strSignName.equals("Sign45") || strSignName.equals("sign45")) {
                         mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng)))
                                    .title(strSignName)
                                    .snippet(String.valueOf(meterInDec)))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign45_ss));


                        } else if (strSignName.equals("Sign60") || strSignName.equals("sign60")) {
                          mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng)))
                                    .title(strSignName)
                                    .snippet(String.valueOf(meterInDec)))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign60_ss));

                        } else {
                          mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng)))
                                    .title(strSignName)
                                    .snippet(String.valueOf(meterInDec)))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sign80_ss));

                        }

                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng coordinate = new LatLng (Double.parseDouble(strLat), Double.parseDouble(strLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                        goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));

                       // txt_Distance.setText((int) valueResult);

                    }//if check distance

                    /*ถ้าไม่มีป้ายบริเวณที่ค้นหา ก้จะไม่แสดง Marker
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    LatLng coordinate = new LatLng (Double.parseDouble(strLat), Double.parseDouble(strLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                    goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng));*/
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    goToLocationZoom(Double.parseDouble(strLat), Double.parseDouble(strLng),15);
                    //remove marker
                    if (marker != null) {
                        marker.remove();
                    }
                    //set marker from gps device
                    MarkerOptions options = new MarkerOptions()
                            .position(new LatLng(gps.getLatitude(), gps.getLongitude()));
                    marker = mGoogleMap.addMarker(options);

                  //  marker.setPosition(results[0].geometry.location);
                    marker.showInfoWindow();
                    LatLng coordinate = new LatLng (gps.getLatitude(),gps.getLongitude());
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
                    goToLocationZoom(gps.getLatitude(),gps.getLongitude(),15);
                    Log.d("01FebV2", "Marker" + "Lat:" + gps.getLatitude() + "Lng:" + gps.getLongitude());
                }// for

               // txt_Distance.setText(marker.getSnippet());

            } catch (Exception e) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                //blind widget
                //final EditText lat = (EditText) findViewById(R.id.edt_lat);
                //final EditText lng = (EditText) findViewById(R.id.edt_lng);
                //final EditText signName = (EditText) findViewById(R.id.edtSignName);

                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
               // TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                LatLng latLng = marker.getPosition();
                tvLocality.setText(marker.getTitle());
                tvLat.setText("Latitude: "+latLng.latitude);
                tvLng.setText("Longitude: "+latLng.longitude);
              //  tvSnippet.setText(marker.getSnippet());
                txt_Distance.setText(marker.getSnippet());

                //show lat long in edit text
               // lat.setText(latLng.latitude+"");
               // lng.setText(latLng.longitude+"");
                return v;
            }
        });

    }//onMapReady
}//Main Class
