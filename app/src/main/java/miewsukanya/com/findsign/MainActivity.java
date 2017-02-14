package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
}//Main Class
