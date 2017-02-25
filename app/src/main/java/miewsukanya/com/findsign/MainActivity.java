package miewsukanya.com.findsign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Explicit

    private ImageView searchQuickImageView,searchSignImageView, knowLedgeImageView,btn_setting;
    TextView txtidSignPref,txtidDistancePref;
    private static final int REQ_LOAD_PREF = 103; //ตั้งรหัสสำหรับส่งค่ากลับ
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

        //search quick controller
        searchQuickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchQuick.class));
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

}//Main Class
