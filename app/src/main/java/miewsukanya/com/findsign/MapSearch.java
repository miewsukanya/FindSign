package miewsukanya.com.findsign;

import android.app.Dialog;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    //Explicit
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleClient;
    TextView txtJson;
    //private MyConstant myConstant;
    //MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_map_search);
            //getjson();
            //myConstant = new MyConstant();
            //myAdapter = new MyAdapter();
            SynUser synUser = new SynUser(MapSearch.this);
            synUser.execute();
            initMap();

        } else {
            //No google map layout
        }
    }//Main Method

    /*public void getjson() {
        txtJson = (TextView) findViewById(R.id.textJson);
        String JsonUrl = "http://202.28.94.32/2559/563020232-9/getlatlong.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);
                        int SignID = jsonObject.getInt("SignID");
                        String SignName = jsonObject.getString("SignName");
                        String Latitude = jsonObject.getString("Latitude");
                        String Longitude = jsonObject.getString("Longitude");

                        Log.d("Json", SignID + "," + SignName + "," + Latitude + "," + Longitude);

                        txtJson.setText(SignID+"\n"+SignName+"\n"+Latitude+"\n"+Longitude+"\n");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtJson.setText("onErrorRespone():"+error.getMessage());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }//getJson*/

    private class SynUser extends AsyncTask<Void, Void, String> {
        //Explicit
        //private String[] SingNameStrings,LatitudeStrings, LongitudeStrings;
       // private int[] SingIDInts;
        //Explicit
        private Context context;
        private static final String urlJSON = "http://202.28.94.32/2559/563020232-9/getlatlong.php";


        public SynUser(Context context) {
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

                /*SingIDInts = new int[jsonArray.length()];
                SingNameStrings = new String[jsonArray.length()];
                LatitudeStrings = new String[jsonArray.length()];
                LongitudeStrings = new String[jsonArray.length()];
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    SingIDInts[i] = jsonObject.getInt("SignID");
                    SingNameStrings[i] = jsonObject.getString("SignName");
                    LatitudeStrings[i] = jsonObject.getString("Latitude");
                    LongitudeStrings[i] = jsonObject.getString("Longitude");


                    txtJson = (TextView) findViewById(R.id.textJson);
                    txtJson.setText(+SingIDInts[i]+"\n");

                    Log.d("26novV1", "name(" + i + ") ==>" + SingIDInts[i]);*/
                for (int i = 0; i < jsonArray.length(); i += 1) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strSignID = jsonObject.getString("SignID");
                    String strSignName = jsonObject.getString("SignName");
                    String strLat = jsonObject.getString("Latitude");
                    String strLng = jsonObject.getString("Longitude");

                    //Create Marker Shop
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng)))
                            .title(strSignID+","+strSignName));
                }// for
               // }//for
            } catch (Exception e) {
                e.printStackTrace();
            }
            //super.onPostExecute(s);
        }//onPost
    }//SnyUser

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
        LatLng comsci = new LatLng(16.4795142,102.809175);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(comsci)
                .title("comsci"));
        //  แพนเลื่อนแผนที่ไปพิกัดที่ระบุ
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(comsci));
        goToLocationZoom(16.4795142, 102.809175, 15);
    }//onMapReady

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mGoogleMap.moveCamera(update);
    }//class zoom map

}//Main Class
