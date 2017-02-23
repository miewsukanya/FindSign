package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //Explicit

    private ImageView searchQuickImageView,searchSignImageView, knowLedgeImageView,btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind widget
        searchQuickImageView = (ImageView) findViewById(R.id.SearchSignQuick);
        searchSignImageView = (ImageView) findViewById(R.id.SearchSign);
        knowLedgeImageView = (ImageView) findViewById(R.id.Knowledge);
        btn_setting = (ImageView) findViewById(R.id.btn_setting);

        //setting controller
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerForContextMenu(v);
                openContextMenu(v);
                //startActivity(new Intent(MainActivity.this,SettingActivity.class));

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu,menu);

    }//onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.distance300sign45:
                String idSetting = "1";
                Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("idSetting", idSetting);
                startActivity(intent);
                finish();
                Log.d("24FebV1","Select idSetting :"+ idSetting);

            case R.id.distance400sign45:
                idSetting = "2";
                intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("idSetting", idSetting);
                Log.d("24FebV2","Select idSetting :"+ idSetting);

        }
        return super.onContextItemSelected(item);
    }
}//Main Class
