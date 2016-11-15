package miewsukanya.com.findsign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SearchSign extends AppCompatActivity {
    //Explicit
    private SeekBar seekBar;
    public TextView txtValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sign);

        //Bind widget
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtValueTextView = (TextView) findViewById(R.id.txtValue);

    }//Main Method

}//Main Class
