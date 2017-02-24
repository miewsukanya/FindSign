package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Explicit

    private ImageView searchQuickImageView,searchSignImageView, knowLedgeImageView,btn_setting;
    TextView txtSetting;
    int idSettingSelected = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind widget
        searchQuickImageView = (ImageView) findViewById(R.id.SearchSignQuick);
        searchSignImageView = (ImageView) findViewById(R.id.SearchSign);
        knowLedgeImageView = (ImageView) findViewById(R.id.Knowledge);
        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        txtSetting = (TextView) findViewById(R.id.txtSetting);

        //setting controller
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // registerForContextMenu(v);
               // openContextMenu(v);
                startActivity(new Intent(MainActivity.this,SettingActivity.class));

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
                startActivity(new Intent(MainActivity.this,SelectTypeSearch.class));

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

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu,menu);

        MenuItem selectedRad300_45 = menu.findItem(R.id.distance300sign45);
        MenuItem selectedRad300_60 = menu.findItem(R.id.distance300sign60);
        MenuItem selectedRad300_80 = menu.findItem(R.id.distance300sign80);

        MenuItem selectedRad400_45 = menu.findItem(R.id.distance400sign45);
        MenuItem selectedRad400_60 = menu.findItem(R.id.distance400sign60);
        MenuItem selectedRad400_80 = menu.findItem(R.id.distance400sign80);

        MenuItem selectedRad500_45 = menu.findItem(R.id.distance500sign45);
        MenuItem selectedRad500_60 = menu.findItem(R.id.distance500sign60);
        MenuItem selectedRad500_80 = menu.findItem(R.id.distance500sign80);

        if (idSettingSelected == 1) {
            selectedRad300_45.setChecked(true);
        } else if (idSettingSelected == 2) {
            selectedRad300_60.setChecked(true);
        } else if (idSettingSelected == 3) {
            selectedRad300_80.setChecked(true);
        }else if (idSettingSelected == 4) {
            selectedRad400_45.setChecked(true);
        }else if (idSettingSelected == 5) {
            selectedRad400_60.setChecked(true);
        }else if (idSettingSelected == 6) {
            selectedRad400_80.setChecked(true);
        }else if (idSettingSelected == 7) {
            selectedRad500_45.setChecked(true);
        }else if (idSettingSelected == 8) {
            selectedRad500_60.setChecked(true);
        }else if (idSettingSelected == 9) {
            selectedRad500_80.setChecked(true);
        }

    }//onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.distance300sign45:
               // String idSetting = "1";
                txtSetting.setText("1");
                item.setChecked(true);
                idSettingSelected = 1;
                Log.d("24FebV1","Select idSetting :"+ txtSetting.toString());
                return true;
            case R.id.distance300sign60:
                txtSetting.setText("2");
                item.setChecked(true);
                idSettingSelected = 2;
               // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
            case R.id.distance300sign80:
                txtSetting.setText("3");
                item.setChecked(true);
                idSettingSelected = 3;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;

            case R.id.distance400sign45:
                txtSetting.setText("4");
                item.setChecked(true);
                idSettingSelected = 4;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
            case R.id.distance400sign60:
                txtSetting.setText("5");
                item.setChecked(true);
                idSettingSelected = 5;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
            case R.id.distance400sign80:
                txtSetting.setText("6");
                item.setChecked(true);
                idSettingSelected = 6;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;

            case R.id.distance500sign45:
                txtSetting.setText("7");
                item.setChecked(true);
                idSettingSelected = 7;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
            case R.id.distance500sign60:
                txtSetting.setText("8");
                item.setChecked(true);
                idSettingSelected = 8;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
            case R.id.distance500sign80:
                txtSetting.setText("9");
                item.setChecked(true);
                idSettingSelected = 9;
                // Log.d("24FebV2","Select idSetting :"+ idSetting);
                return true;
        }
        return super.onContextItemSelected(item);
    }//*/

}//Main Class
