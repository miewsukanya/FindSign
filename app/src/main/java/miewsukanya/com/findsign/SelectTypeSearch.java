package miewsukanya.com.findsign;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectTypeSearch extends AppCompatActivity {
    //Explicit
    ImageView ShowARImageView, ShowMapImageView;
    private MyConstant myConstant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_search);

        myConstant = new MyConstant();
        getJson getJson = new getJson(SelectTypeSearch.this);
        getJson.execute(myConstant.getUrlGetJSON());

        //BindWidGet
        ShowARImageView = (ImageView) findViewById(R.id.ShowAR);
        ShowMapImageView = (ImageView) findViewById(R.id.ShowMap);

        //showARImageViewController
        ShowARImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectTypeSearch.this,ArViewActivity.class));
            }
        });
        ShowMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectTypeSearch.this,MapSearch.class));
            }
        });

    }//Main method

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

                    //intent to Service
                    Intent intent = new Intent(SelectTypeSearch.this,MapSearch.class);
                    intent.putExtra("SignID", SignIDStrings);
                    intent.putExtra("SignName", SignNameStrings);
                    intent.putExtra("Latitude", LatitudeStrings);
                    intent.putExtra("Longitude", LongitudeStrings);
                    startActivity(intent);
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }//get json
}//Main Class
