package miewsukanya.com.findsign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    //Explicit
    //RadioGroup RadioGroupDistance;
   // RadioButton RadioButtonDistance;
    TextView textViewDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        textViewDistance = (TextView) findViewById(R.id.txtDistanceSetting);
        textViewDistance.setEnabled(false);
    }//main method

    public void onDistance(View view) {
        boolean check = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rad300:
                if (check) {
                    textViewDistance.setText("0.30");
                    textViewDistance.setEnabled(true);
                } else {
                    textViewDistance.setEnabled(false);
                }
            break;
            case R.id.rad400:
                if (check) {
                    textViewDistance.setText("0.40");
                    textViewDistance.setEnabled(true);
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
            case R.id.rad500:
                if (check) {
                    textViewDistance.setText("0.50");
                    textViewDistance.setEnabled(true);
                } else {
                    textViewDistance.setEnabled(false);
                }
                break;
        }//switch case
    }//onDistance
}//main class
