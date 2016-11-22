package miewsukanya.com.findsign;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "myapp";
    //Explicit
    //private String[] SignIDStrings, SignNameStrings, LatitudeStrings,LongitudeStrings;
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleClient;
    TextView textJsonTextView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_map_search);
            initMap();
            getJson();

        } else {
            //No google map layout
        }
    }//Main Method

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
    }
    private void getJson() {
        //textJsonTextView = new TextView(this);
        // setContentView(textJsonTextView);
        String url = "http://202.28.94.32/2559/563020232-9/getlatlong.php";
        progressDialog = new ProgressDialog(this);
        progressDialog .setMessage("Loading...");
        progressDialog.show();
        //ส่งค่า request แบบ jsonAraayRequest ไปยังเซิฟเวอร์
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                // textJsonTextView.setText(response.toString());

                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        int SignID = jsonObject.getInt("SignID");
                        String SignName = jsonObject.getString("SignName");
                        double Latitude = jsonObject.getDouble("Latitude");
                        double Longitude = jsonObject.getDouble("Longitude");
                        Log.d(TAG, SignID + ","+SignName+ "," + Latitude + "," + Longitude);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.hide();
            }//onResponse
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textJsonTextView.setText("onErrorResponse():"+error.getMessage());
                progressDialog.hide();
            }
        });//request

        //สรา้งอินสแตนซ์ queue แล้วส่ง request ไปเข้าคิวเพื่อรัน
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        //latlng sc06
        LatLng comsci = new LatLng(16.474475, 102.82313);
        LatLng drom8 = new LatLng(16.4795142, 102.809175);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(drom8)
                    .title("comsci"));
            //  แพนเลื่อนแผนที่ไปพิกัดที่ระบุ
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(drom8));
            goToLocationZoom(16.4795142, 102.809175, 15);
    }
    //class zoom map
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mGoogleMap.moveCamera(update);
    }


}//Main Class
