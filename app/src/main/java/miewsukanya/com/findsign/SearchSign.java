package miewsukanya.com.findsign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SearchSign extends AppCompatActivity {
    //Explicit
    private ImageView searchSignAllImageView,searchSign45ImageView,searchSign60ImageView, searchSign80ImageView;
    private SeekBar seekBar;
    public TextView txtValueTextView,txtidSign,txtidDist;
    int seekBarValue;
    int seekBarMax = 10000/1000;
    int seekBarStart = 5000/1000;
    //CameraDevice cameraDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sign);

        //Bind widget
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtValueTextView = (TextView) findViewById(R.id.txtValue);
        txtidSign = (TextView) findViewById(R.id.txtidSignSearchSign);
        txtidDist = (TextView) findViewById(R.id.txtidDistSearchSign);

        searchSignAllImageView = (ImageView) findViewById(R.id.SearchSignAll);
        searchSign45ImageView = (ImageView) findViewById(R.id.SearchSign45);
        searchSign60ImageView = (ImageView) findViewById(R.id.SearchSign60);
        searchSign80ImageView = (ImageView) findViewById(R.id.SearchSign80);

        //get distant from SelectTypeSign 26/02/2017
        TextView textView = (TextView) findViewById(R.id.txtidSignSearchSign);
        Intent intent = getIntent();
        String idSign = intent.getStringExtra("idSign");
        textView.setText(idSign);
        textView.setTextSize(20);
        //get distant from SelectTypeSign 26/02/2017
        TextView textView2 = (TextView) findViewById(R.id.txtidDistSearchSign);
        intent = getIntent();
        String idDistance = intent.getStringExtra("idDistance");
        textView2.setText(idDistance);
        textView2.setTextSize(20);
        Log.d("26FebV7", "idSign :" + idSign+"idDist: "+idDistance);

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
                //send distant to MapSearch 23/02/2017
                String distant = txtValueTextView.getText().toString();
                String idMap = "1";
                String idSign = txtidSign.getText().toString();
                String idDistance = txtidDist.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("distant", distant);
                intent.putExtra("idMap", idMap);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("26FebV6","distance:"+distant+"idMap:"+idMap+"idSign:"+idSign+"idDistance:"+idDistance);
                startActivity(intent);
               // finish();
            }
        });
        //SearchSign45
        searchSign45ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send distant to MapSearch 23/02/2017
                String distant = txtValueTextView.getText().toString();
                String idMap = "2";
                String idSign = txtidSign.getText().toString();
                String idDistance = txtidDist.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("distant", distant);
                intent.putExtra("idMap", idMap);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("26FebV6","distance:"+distant+"idMap:"+idMap+"idSign:"+idSign+"idDistance:"+idDistance);
                startActivity(intent);
            }
        });
        //SearchSign60
        searchSign60ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send distant to MapSearch 23/02/2017
                String distant = txtValueTextView.getText().toString();
                String idMap = "3";
                String idSign = txtidSign.getText().toString();
                String idDistance = txtidDist.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("distant", distant);
                intent.putExtra("idMap", idMap);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("26FebV6","distance:"+distant+"idMap:"+idMap+"idSign:"+idSign+"idDistance:"+idDistance);
                startActivity(intent);
            }
        });
        //SearchSign80
        searchSign80ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send distant to MapSearch 23/02/2017
                String distant = txtValueTextView.getText().toString();
                String idMap = "4";
                String idSign = txtidSign.getText().toString();
                String idDistance = txtidDist.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                intent.putExtra("distant", distant);
                intent.putExtra("idMap", idMap);
                intent.putExtra("idSign", idSign);
                intent.putExtra("idDistance", idDistance);
                Log.d("26FebV6","distance:"+distant+"idMap:"+idMap+"idSign:"+idSign+"idDistance:"+idDistance);
                startActivity(intent);
            }
        });


    }//Main Method


}//Main Class
