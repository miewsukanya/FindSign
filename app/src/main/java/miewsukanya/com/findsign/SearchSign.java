package miewsukanya.com.findsign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SearchSign extends AppCompatActivity {
    //Explicit
    private SeekBar seekBar;
    public TextView txtValueTextView;
    int seekBarValue;
    int seekBarMax = 100;
    int seekBarStart = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sign);

        //Bind widget
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtValueTextView = (TextView) findViewById(R.id.txtValue);

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
        

    }//Main Method

}//Main Class
