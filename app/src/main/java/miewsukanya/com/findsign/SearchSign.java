package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SearchSign extends AppCompatActivity {
    //Explicit
    private ImageView searchSignAllImageView,searchSign45ImageView,searchSign60ImageView, searchSign80ImageView;
    private SeekBar seekBar;
    public TextView txtValueTextView;
    int seekBarValue;
    int seekBarMax = 100;
    int seekBarStart = 50;
    //CameraDevice cameraDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sign);

        //Bind widget
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtValueTextView = (TextView) findViewById(R.id.txtValue);

        searchSignAllImageView = (ImageView) findViewById(R.id.SearchSignAll);
        searchSign45ImageView = (ImageView) findViewById(R.id.SearchSign45);
        searchSign60ImageView = (ImageView) findViewById(R.id.SearchSign60);
        searchSign80ImageView = (ImageView) findViewById(R.id.SearchSign80);


        //SeekBarController
        seekBar.setMax(seekBarMax);
        seekBar.setProgress(seekBarStart);
        txtValueTextView.setText(Integer.toString(seekBarStart));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekBarValue = seekBar.getProgress();
                txtValueTextView.setText(Integer.toString(seekBarValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });//SeekBar

        //SearchSignAll
        searchSignAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchSign.this,MapSearch.class));
            }
        });
        //SearchSign45
        searchSign45ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchSign.this,MapSearch45.class));
            }
        });
        //SearchSign60
        searchSign60ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchSign.this,MapSearch60.class));
            }
        });
        //SearchSign80
        searchSign80ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchSign.this,MapSearch80.class));
            }
        });


    }//Main Method


}//Main Class
