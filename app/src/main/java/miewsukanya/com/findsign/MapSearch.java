package miewsukanya.com.findsign;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "myapp";
    //Explicit
    //private String[] SignID, SignName,Latitude,Longitude;
    private  String[] SignIDStrings, SignNameStrings,LatitudeStrings,LongitudeStrings;
    //private Double Latitude = 0.00;
    //private Double Longitude = 0.00;
    //ArrayList<HashMap<String, String>> location = null;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleClient;
    TextView textJsonTextView;
    ProgressDialog progressDialog;
    private MyConstant myConstant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_map_search);
            initMap();
           // getJson();
            myConstant = new MyConstant();
            getJson getJson = new getJson(MapSearch.this);
            getJson.execute(myConstant.getUrlGetJSON());



        } else {
            //No google map layout
        }
    }//Main Method
    private class getJson extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;
        private String[] SignIDStrings,SignNameStrings,LatitudeStrings, LongitudeStrings;
        //private Boolean aBoolean = true;

        public getJson(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request  = builder.url(params[0]).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("24novV1", "e doIn ==>" + e.toString());
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {

            Log.d("24novV1", "Json ==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                /*location = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map;*/
                SignIDStrings = new String[jsonArray.length()];
                SignNameStrings = new String[jsonArray.length()];
                LatitudeStrings = new String[jsonArray.length()];
                LongitudeStrings = new String[jsonArray.length()];

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    SignIDStrings[i] = jsonObject.getString("SignID");
                    SignNameStrings[i] = jsonObject.getString("SignName");
                    LatitudeStrings[i] = jsonObject.getString("Latitude");
                    LongitudeStrings[i] = jsonObject.getString("Longitude");

                    /*map = new HashMap<String, String>();
                    map.put("SignID", jsonObject.getString("SignID"));
                    map.put("SignName", jsonObject.getString("SignName"));
                    map.put("Latitude", jsonObject.getString("Latitude"));
                    map.put("Longitude", jsonObject.getString("Longitude"));
                    location.add(map);*/

                    Log.d("24novV1", "Latitude (" + LatitudeStrings[i] + ")" + ",(" + LongitudeStrings[i] + ")");


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }//get json

    //Show map
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }
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
    }//google services

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        /*Latitude = Double.parseDouble(location.get(0).get("Latitude").toString());
        Longitude = Double.parseDouble(location.get(0).get("Longitude").toString());
        LatLng coordinate = new LatLng(Latitude, Longitude);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));*/
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //latlng sc06
       // LatLng comsci = new LatLng(lat, lng);
        LatLng drom8 = new LatLng(16.4795142, 102.809175);

        mGoogleMap.addMarker(new MarkerOptions()
                    .position(drom8)
                    .title("comsci"));
            //  แพนเลื่อนแผนที่ไปพิกัดที่ระบุ
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(drom8));
        goToLocationZoom(16.4795142, 102.809175, 15);
        /*for (int i = 0; i < location.size(); i++) {
            Latitude = Double.parseDouble(location.get(i).get("Latitude").toString());
            Longitude = Double.parseDouble(location.get(i).get("Longitude").toString());
            String name = location.get(i).get("SignName").toString();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Latitude, Longitude))
                    .title(name));
            //mGoogleMap.addMarker(marker);
        }*/
    }//onMapReady


    //class zoom map
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mGoogleMap.moveCamera(update);
    }
}//Main Class
